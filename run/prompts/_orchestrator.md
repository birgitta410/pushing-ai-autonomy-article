# For CRUD workflows

## Main workflow:
Requirements analyst -> Bootstrapper -> Backend architect 

And then a workflow to do per aggregate found in the requirements.md:
-> Backend persistence layer coder -> Backend service layer coder -> Backend controller layer coder -> End to End tester

## CRUD Instructions

Each of the steps should be started as a subtask in **Code mode**, and each subtask has its own instructions file. Read the contents of the respective instructions file when you prepare the prompt for a subtask, and add the instructions to the subtask instructions.

Requirements analyst -> agent_instructions/instructions_requirements_analyst.md
Bootstrapper -> agent_instructions/instructions_bootstrapper.md
Backend architect -> agent_instructions/instructions_backend_architect.md
Backend persistence layer coder -> agent_instructions/instructions_backend_entities_repositories.md
Backend service layer coder -> agent_instructions/instructions_backend_esrvices.md
Backend controller layer coder -> agent_instructions/instructions_backend_controllers.md
End to End tester -> agent_instructions/instructions_end_to_end_testing.md