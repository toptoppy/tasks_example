package com.toptoppy.kotlinSpringBoot.tasks

import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.time.Instant
import java.time.format.DateTimeParseException

class TaskServiceTest {

    private val mockTaskRepository: TaskRepository = mockk()
    private val taskService = TaskService(mockTaskRepository)

    @AfterEach
    fun afterEach() {
        confirmVerified(mockTaskRepository)
    }

    @Nested
    inner class CreateNewTask {
        @Test
        fun `when request task should create new task entity`() {
            // Given
            val newTask = TaskRequest("New Task", "New Description", "2023-12-24T15:30:45Z", TaskStatus.PENDING)
            val task = TaskEntity(
                title = "New Task",
                description = "New Description",
                dueDate = DateTimeUtils.fromString("2023-12-24T15:30:45Z"),
                status = TaskStatus.PENDING.toString()
            )
            val createdTask = TaskEntity(
                id = 1,
                title = "New Task",
                description = "New Description",
                dueDate = DateTimeUtils.fromString("2023-12-24T15:30:45Z"),
                status = TaskStatus.PENDING.toString()
            )

            every { mockTaskRepository.save(task) } returns createdTask

            // When
            val result = taskService.createNewTask(newTask)

            // Then
            verify(exactly = 1) { mockTaskRepository.save(task) }
            assertEquals(createdTask, result)
        }

        @Test
        fun `when request task with invalid date format should throw exception`() {
            // Given
            val invalidDateFormatTask = TaskRequest("Invalid Date", "Description", "2023-12-24", TaskStatus.PENDING)

            // When/Then
            assertThrows<DateTimeParseException> {
                taskService.createNewTask(invalidDateFormatTask)
            }

            verify { mockTaskRepository wasNot Called }
        }

        @Test
        fun `when saving task fails, should throw exception`() {
            // Given
            val newTask = TaskRequest("New Task", "New Description", "2023-12-24T15:30:45Z", TaskStatus.PENDING)

            every { mockTaskRepository.save(any()) } throws RuntimeException("Failed to save task")

            // When/Then
            assertThrows<RuntimeException> {
                taskService.createNewTask(newTask)
            }

            verify(exactly = 1) { mockTaskRepository.save(any()) }
        }

        @Test
        fun `when creating task with due date in the past, should throw exception`() {
            // Given
            val pastDueDateTask =
                TaskRequest("Past Due Date", "Description", "2022-01-01T12:00:00Z", TaskStatus.PENDING)

            // When/Then
            assertThrows<Exception> {
                taskService.createNewTask(pastDueDateTask)
            }

            verify { mockTaskRepository wasNot Called }
        }

        @Test
        fun `when creating task with valid due date, should succeed`() {
            // Given
            val validDueDateTask =
                TaskRequest("Valid Due Date", "Description", "2024-01-01T12:00:00Z", TaskStatus.PENDING)
            val task = TaskEntity(
                title = "Valid Due Date",
                description = "Description",
                dueDate = Instant.parse("2024-01-01T12:00:00Z"),
                status = TaskStatus.PENDING.toString()
            )
            val createdTask = TaskEntity(
                id = 1,
                title = "Valid Due Date",
                description = "Description",
                dueDate = Instant.parse("2024-01-01T12:00:00Z"),
                status = TaskStatus.PENDING.toString()
            )

            every { mockTaskRepository.save(task) } returns createdTask

            // When
            val result = taskService.createNewTask(validDueDateTask)

            // Then
            verify(exactly = 1) { mockTaskRepository.save(task) }
            assertEquals(createdTask, result)
        }
    }


    @Nested
    inner class GetAllTask {

        @Test
        fun `when requesting all tasks should return list of tasks`() {
            // Given
            val task1 = TaskEntity(1, "Task 1", "Description 1", Instant.now(), TaskStatus.PENDING.toString())
            val task2 = TaskEntity(2, "Task 2", "Description 2", Instant.now(), TaskStatus.IN_PROGRESS.toString())
            val task3 = TaskEntity(3, "Task 3", "Description 3", Instant.now(), TaskStatus.COMPLETED.toString())
            val tasks = listOf(task1, task2, task3)

            every { mockTaskRepository.findAll() } returns tasks

            // When
            val result = taskService.getAllTasks()

            // Then
            verify(exactly = 1) { mockTaskRepository.findAll() }
            assertEquals(tasks, result)
        }
    }

    @Nested
    inner class GetTaskById {
        @Test
        fun `when requesting task by ID should return the task`() {
            // Given
            val taskId = 1L
            val task = TaskEntity(taskId, "Task 1", "Description 1", Instant.now(), TaskStatus.PENDING.toString())

            every { mockTaskRepository.findByIdOrNull(taskId) } returns task

            // When
            val result = taskService.getTaskById(taskId)

            // Then
            verify(exactly = 1) { mockTaskRepository.findById(taskId) }
            assertEquals(task, result)
        }

        @Test
        fun `when requesting task by ID and not found record should return null`() {
            // Given
            val taskId = 1L

            every { mockTaskRepository.findByIdOrNull(taskId) } returns null

            // When
            val result = taskService.getTaskById(taskId)

            // Then
            verify(exactly = 1) { mockTaskRepository.findById(taskId) }
            assertNull(result)
        }
    }

    @Nested
    inner class UpdateTask {

        @Test
        fun `when updating task should return null if record not exists`() {
            // Given
            val taskId = 1L
            val updatedTaskRequest =
                TaskRequest("Updated Task", "Updated Description", "2023-12-25T10:00:00Z", TaskStatus.IN_PROGRESS)

            every { mockTaskRepository.existsById(taskId) } returns false

            // When
            val result = taskService.updateTask(taskId, updatedTaskRequest)

            // Then
            verify(exactly = 1) { mockTaskRepository.existsById(taskId) }
            verify(exactly = 0) { mockTaskRepository.save(any()) }
            assertNull(result)
        }

        @Test
        fun `when updating task should return the updated task`() {
            // Given
            val taskId = 1L
            val updatedTaskRequest =
                TaskRequest("Updated Task", "Updated Description", "2023-12-25T10:00:00Z", TaskStatus.IN_PROGRESS)
            val updatedTask = TaskEntity(
                id = taskId,
                title = "Updated Task",
                description = "Updated Description",
                dueDate = Instant.parse("2023-12-25T10:00:00Z"),
                status = TaskStatus.IN_PROGRESS.toString()
            )

            every { mockTaskRepository.existsById(taskId) } returns true
            every { mockTaskRepository.save(any()) } returns updatedTask

            // When
            val result = taskService.updateTask(taskId, updatedTaskRequest)

            // Then
            verify(exactly = 1) { mockTaskRepository.existsById(taskId) }
            verify(exactly = 1) { mockTaskRepository.save(any()) }
            assertEquals(updatedTask, result)
        }
    }

    @Nested
    inner class DeleteTask {

        @Test
        fun `when removing task should return null if record not exists`() {
            // Given
            val taskId = 1L

            every { mockTaskRepository.existsById(taskId) } returns false

            // When
            taskService.deleteTask(taskId)

            // Then
            verify(exactly = 1) { mockTaskRepository.existsById(taskId) }
            verify(exactly = 0) { mockTaskRepository.deleteById(any()) }
        }

        @Test
        fun `when removing task should return the removed task`() {
// Given
            val taskId = 1L

            every { mockTaskRepository.existsById(taskId) } returns true
            every { mockTaskRepository.deleteById(taskId) } returns Unit

            // When
            taskService.deleteTask(taskId)

            // Then
            verify(exactly = 1) { mockTaskRepository.existsById(taskId) }
            verify(exactly = 1) { mockTaskRepository.deleteById(any()) }
        }
    }
}
