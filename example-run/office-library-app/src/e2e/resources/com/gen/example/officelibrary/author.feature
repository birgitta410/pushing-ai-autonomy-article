Feature: Complete Author journey testing all Author endpoints

  Background:
    * url demoBaseUrl

  Scenario: Complete Author CRUD journey
    * def timestamp = new java.util.Date().getTime()
    * def uniqueEmail1 = 'john.doe.' + timestamp + '@example.com'
    * def uniqueEmail2 = 'jane.smith.' + timestamp + '@example.com'
    
    # 1. Create a new author
    Given path '/api/authors'
    And request
    """
    {
        "firstName": "John",
        "lastName": "Doe",
        "biography": "A prolific writer of science fiction novels",
        "birthDate": "1970-05-15",
        "nationality": "American",
        "email": "#(uniqueEmail1)"
    }
    """
    When method POST
    Then status 201
    And match response.firstName == 'John'
    And match response.lastName == 'Doe'
    And match response.biography == 'A prolific writer of science fiction novels'
    And match response.birthDate == '1970-05-15'
    And match response.nationality == 'American'
    And match response.email == uniqueEmail1
    And match response.id == '#uuid'
    * def authorId = response.id

    # 2. Get the created author by ID
    Given path '/api/authors', authorId
    When method GET
    Then status 200
    And match response.id == authorId
    And match response.firstName == 'John'
    And match response.lastName == 'Doe'
    And match response.email == uniqueEmail1

    # 3. Create another author for list testing
    Given path '/api/authors'
    And request 
    """
    {
        "firstName": "Jane",
        "lastName": "Smith",
        "biography": "Mystery and thriller author",
        "birthDate": "1985-03-22",
        "nationality": "British",
        "email": "#(uniqueEmail2)"
    }
    """
    When method POST
    Then status 201
    * def authorId2 = response.id

    # 4. List all authors
    Given path '/api/authors'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[*].id contains authorId
    And match response[*].id contains authorId2
    And assert response.length >= 2

    # 5. Update the first author
    Given path '/api/authors', authorId
    And request 
    """
    {
        "firstName": "John",
        "lastName": "Doe",
        "biography": "An award-winning science fiction author with over 20 published novels",
        "birthDate": "1970-05-15",
        "nationality": "American",
        "email": "#(uniqueEmail1)"
    }
    """
    When method PUT
    Then status 200
    And match response.id == authorId
    And match response.firstName == 'John'
    And match response.lastName == 'Doe'
    And match response.biography == 'An award-winning science fiction author with over 20 published novels'
    And match response.email == uniqueEmail1

    # 6. Verify the update by getting the author again
    Given path '/api/authors', authorId
    When method GET
    Then status 200
    And match response.biography == 'An award-winning science fiction author with over 20 published novels'
    And match response.email == uniqueEmail1

    # 7. Search authors by name
    Given path '/api/authors/search'
    And param query = 'John'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].firstName == 'John'
    And match response[0].lastName == 'Doe'

    # 8. Search authors by name (partial match)
    Given path '/api/authors/search'
    And param query = 'Jane'
    When method GET
    Then status 200
    And match response == '#array'
    And match response[0].firstName == 'Jane'
    And match response[0].lastName == 'Smith'

    # 9. Get books by author (should be empty initially)
    Given path '/api/authors', authorId, 'books'
    When method GET
    Then status 200
    And match response == '#array'
    And assert response.length >= 0

    # 10. Try to delete author with no books (should succeed)
    Given path '/api/authors', authorId2
    When method DELETE
    Then status 204

    # 11. Verify author was deleted
    Given path '/api/authors', authorId2
    When method GET
    Then status 404

    # 12. Verify the remaining author still exists
    Given path '/api/authors', authorId
    When method GET
    Then status 200
    And match response.id == authorId

    # 13. Test error cases - Create author with invalid email
    Given path '/api/authors'
    And request 
    """
    {
        "firstName": "Invalid",
        "lastName": "Author",
        "email": "invalid-email"
    }
    """
    When method POST
    Then status 400

    # 14. Test error cases - Create author with missing required fields
    Given path '/api/authors'
    And request 
    """
    {
        "firstName": "Missing",
        "email": "missing@example.com"
    }
    """
    When method POST
    Then status 400

    # 15. Test error cases - Update non-existent author
    Given path '/api/authors/00000000-0000-0000-0000-000000000000'
    And request 
    """
    {
        "firstName": "Non",
        "lastName": "Existent",
        "email": "nonexistent@example.com"
    }
    """
    When method PUT
    Then status 404

    # 16. Test error cases - Get non-existent author
    Given path '/api/authors/00000000-0000-0000-0000-000000000000'
    When method GET
    Then status 404

    # Clean up - Delete the remaining author
    Given path '/api/authors', authorId
    When method DELETE
    Then status 204