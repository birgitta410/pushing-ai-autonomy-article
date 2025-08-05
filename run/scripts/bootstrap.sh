#!/bin/bash

# Bootstrap script that handles project generation, bootstrapping and validation

set -e  # Exit on any error

# Get arguments
OUTPUT_PATH=$1
APP_NAME=$2
BASE_PACKAGE=$3

if [ -z "$OUTPUT_PATH" ] || [ -z "$APP_NAME" ]; then
    echo "Error: Missing required arguments"
    echo "Usage: $0 OUTPUT_PATH APP_NAME [BASE_PACKAGE]"
    exit 1
fi

# If BASE_PACKAGE not provided, generate it from APP_NAME
if [ -z "$BASE_PACKAGE" ]; then
    # Convert app-name to com.example.appname format
    BASE_NAME=$(echo "$APP_NAME" | sed 's/-app$//' | sed 's/-//g')
    BASE_PACKAGE="com.example.$BASE_NAME"
fi

echo "=== Starting Unified Build Process ==="
echo "Output Path: $OUTPUT_PATH"
echo "App Name: $APP_NAME"
echo "Base Package: $BASE_PACKAGE"

# Step 1: Create project structure
echo -e "\n=== Step 1: Creating Project Structure ==="

APP_PATH="$OUTPUT_PATH/$APP_NAME"

# # Delete if already exists
# ! rm -rf "$APP_PATH" 2>/dev/null || true
# mkdir -p "$APP_PATH"

cd "$OUTPUT_PATH"

echo -e "\n=== Bootstrap Spring project ==="

spring init --build=gradle --type=gradle-project --java-version=21 --dependencies=web,data-jpa,validation,actuator,h2,lombok --package-name=$BASE_PACKAGE --name=$APP_NAME $APP_NAME

cd "$APP_NAME"

# Overwrite application.properties
cat > "src/main/resources/application.properties" << EOL
# Application Configuration
spring.application.name=$APP_NAME
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:${APP_NAME//-/_}db
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# H2 Console Configuration
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

logging.file.name=application.log
EOL

# Logging configuration
## Short rollover to not overwhelm the context window of log analysis
## Compact log line format, again, to keep tokens low when agents analyse logs
cat > "src/main/resources/logback-spring.xml" << EOL
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_FILE" value="application.log"/>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <appender name="ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>application.%d{yyyy-MM-dd_HH}.%i.log</fileNamePattern>
            <maxFileSize>1MB</maxFileSize>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <logger name="org.springframework.boot" level="INFO"/>
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ROLLING"/>
    </root>
</configuration>
EOL

# Set up Gradle wrapper
echo -e "\n=== Step 3: Setting up Gradle Wrapper ==="

# Create gradle wrapper script
gradle wrapper --gradle-version 8.4

# Add e2e test configuration to build.gradle

mkdir -p "src/e2e/java/$BASE_PACKAGE"
mkdir -p "src/e2e/resources/$BASE_PACKAGE"

cat > karate-config.js << EOL
function fn() {
    var config = {};
    config.demoBaseUrl = 'http://localhost:8080';
    return config;
}
EOL

echo -e "\n// E2E Test Configuration" >> build.gradle
cat >> build.gradle << 'EOL'

sourceSets {
	e2e {
		java.srcDir file('src/e2e/java')
		resources.srcDir file('src/e2e/resources')
		compileClasspath += sourceSets.test.runtimeClasspath
		runtimeClasspath += sourceSets.test.runtimeClasspath
	}
}

configurations {
	e2eImplementation.extendsFrom testImplementation
	e2eRuntimeOnly.extendsFrom testRuntimeOnly
}

task e2eTest(type: Test) {
	description = 'Runs end-to-end tests'
	group = 'verification'
	testClassesDirs = sourceSets.e2e.output.classesDirs
	classpath = sourceSets.e2e.runtimeClasspath
	useJUnitPlatform()
}
tasks.named('processE2eResources') {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
EOL

# Step 5: Validate setup
echo -e "\n=== Step 5: Validating Bootstrap ==="

# Check if wrapper files exist
if [ ! -f "./gradlew" ] || [ ! -f "./gradlew.bat" ]; then
    echo "Error: Gradle wrapper files not found"
    exit 1
fi

# Test gradle wrapper
echo "Testing Gradle wrapper..."
./gradlew --version || {
    echo "Error: Gradle wrapper validation failed"
    exit 1
}

echo -e "\n=== Build Process Completed Successfully ==="
echo "Project location: $APP_PATH"
echo "To run the application: cd $APP_PATH && ./gradlew bootRun"
