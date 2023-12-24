# Problem: Task Manager API

Design and implement a RESTful API for a task manager. The API should allow users to perform basic CRUD (Create, Read, Update, Delete) operations on tasks. Each task should have the following attributes:

- Task ID
- Task Title
- Task Description
- Due Date
- Status (e.g., Pending, In Progress, Completed)

Your API should support the following endpoints:

1. **Create a Task:**
    - Endpoint: `POST /tasks`
    - Description: Allow users to create a new task by providing the task title, task description, due date, and initial status.

2. **Retrieve Tasks:**
    - Endpoint: `GET /tasks`
    - Description: Provide an endpoint to retrieve a list of all tasks. Users should also be able to retrieve a specific task by its ID.

3. **Update a Task:**
    - Endpoint: `PUT /tasks/{taskId}`
    - Description: Allow users to update an existing task by providing its ID. They should be able to update the task title, task description, due date, and status.

4. **Delete a Task:**
    - Endpoint: `DELETE /tasks/{taskId}`
    - Description: Allow users to delete a task by providing its ID.

This problem will give you the opportunity to work with HTTP methods (POST, GET, PUT, DELETE), handle request and response formats (typically JSON), and design a simple database model for tasks. You can choose the programming language and framework that you're most comfortable with or want to learn.
