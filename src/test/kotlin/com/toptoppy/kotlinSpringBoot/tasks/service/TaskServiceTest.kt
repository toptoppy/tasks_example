package com.toptoppy.kotlinSpringBoot.tasks.service

import com.toptoppy.kotlinSpringBoot.tasks.TaskEntity
import com.toptoppy.kotlinSpringBoot.tasks.TaskRepository
import com.toptoppy.kotlinSpringBoot.tasks.TaskService
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskRequest
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskResponse
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskStatus
import com.toptoppy.kotlinSpringBoot.tasks.error.GeneralException
import com.toptoppy.kotlinSpringBoot.tasks.utils.DateTimeUtils
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
            val newTask = TaskRequest("New Task", "New Description", "2123-12-24T15:30:45Z", TaskStatus.PENDING)
            val task = TaskEntity(
                title = "New Task",
                description = "New Description",
                dueDate = DateTimeUtils.parseIso8601Utc("2123-12-24T15:30:45Z"),
                status = TaskStatus.PENDING.toString()
            )
            val createdTaskResponse = TaskResponse(
                id = 0,
                title = "New Task",
                description = "New Description",
                dueDate = DateTimeUtils.parseIso8601Utc("2123-12-24T15:30:45Z"),
                status = TaskStatus.PENDING.toString()
            )

            every { mockTaskRepository.save(task) } returns task

            // When
            val result = taskService.createNewTask(newTask)

            // Then
            verify(exactly = 1) { mockTaskRepository.save(task) }
            assertEquals(createdTaskResponse, result)
        }

        @Test
        fun `when request task with invalid date format should throw exception`() {
            // Given
            val invalidDateFormatTask = TaskRequest("Invalid Date", "Description", "2123-12-24", TaskStatus.PENDING)

            // When/Then
            assertThrows<DateTimeParseException> {
                taskService.createNewTask(invalidDateFormatTask)
            }

            verify { mockTaskRepository wasNot Called }
        }

        @Test
        fun `when saving task fails, should throw exception`() {
            // Given
            val newTask = TaskRequest("New Task", "New Description", "2123-12-24T15:30:45Z", TaskStatus.PENDING)

            every { mockTaskRepository.save(any()) } throws Exception("INTERNAL_SERVER_ERROR")

            // When/Then
            assertThrows<GeneralException> {
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
                TaskRequest("Valid Due Date", "Description", "2124-01-01T12:00:00Z", TaskStatus.PENDING)
            val task = TaskEntity(
                title = "Valid Due Date",
                description = "Description",
                dueDate = Instant.parse("2124-01-01T12:00:00Z"),
                status = TaskStatus.PENDING.toString()
            )
            val createdTask = TaskResponse(
                id = 0,
                title = "Valid Due Date",
                description = "Description",
                dueDate = Instant.parse("2124-01-01T12:00:00Z"),
                status = TaskStatus.PENDING.toString()
            )

            every { mockTaskRepository.save(task) } returns task

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
            val dueDateFix = Instant.parse("2123-12-25T10:00:00Z")

            val task1 = TaskEntity(1, "Task 1", "Description 1", dueDateFix, TaskStatus.PENDING.toString())
            val task2 = TaskEntity(2, "Task 2", "Description 2", dueDateFix, TaskStatus.IN_PROGRESS.toString())
            val task3 = TaskEntity(3, "Task 3", "Description 3", dueDateFix, TaskStatus.COMPLETED.toString())

            val taskRes1 = TaskResponse(1, "Task 1", "Description 1", dueDateFix, TaskStatus.PENDING.toString())
            val taskRes2 = TaskResponse(2, "Task 2", "Description 2", dueDateFix, TaskStatus.IN_PROGRESS.toString())
            val taskRes3 = TaskResponse(3, "Task 3", "Description 3", dueDateFix, TaskStatus.COMPLETED.toString())
            val tasks = listOf(task1, task2, task3)
            val taskRes = listOf(taskRes1,taskRes2,taskRes3)

            every { mockTaskRepository.findAll() } returns tasks

            // When
            val result = taskService.getAllTasks()

            // Then
            verify(exactly = 1) { mockTaskRepository.findAll() }
            assertEquals(taskRes, result)
        }
    }

    @Nested
    inner class GetTaskById {
        @Test
        fun `when requesting task by ID should return the task`() {
            // Given
            val dueDateFix = Instant.parse("2123-12-25T10:00:00Z")
            val taskId = 1L
            val task = TaskEntity(taskId, "Task 1", "Description 1", dueDateFix, TaskStatus.PENDING.toString())
            val taskRes = TaskResponse(1, "Task 1", "Description 1", dueDateFix, TaskStatus.PENDING.toString())

            every { mockTaskRepository.findByIdOrNull(taskId) } returns task

            // When
            val result = taskService.getTaskById(taskId)

            // Then
            verify(exactly = 1) { mockTaskRepository.findById(taskId) }
            assertEquals(taskRes, result)
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

            every { mockTaskRepository.findByIdOrNull(taskId) } returns null

            // When
            val result = taskService.updateTask(taskId, updatedTaskRequest)

            // Then
            verify(exactly = 1) { mockTaskRepository.findByIdOrNull(taskId) }
            verify(exactly = 0) { mockTaskRepository.save(any()) }
            assertNull(result)
        }

        @Test
        fun `when updating task should return the updated task`() {
            // Given
            val taskId = 1L
            val updatedTaskRequest =
                TaskRequest("Updated Task", "Updated Description", "2123-12-25T10:00:00Z", TaskStatus.IN_PROGRESS)
            val entity = TaskEntity(
                id = taskId,
                title = "Updated Task",
                description = "Updated Description",
                dueDate = Instant.parse("2123-12-25T10:00:00Z"),
                status = TaskStatus.PENDING.toString()
            )
            val updateEntity = TaskEntity(
                id = taskId,
                title = "Updated Task",
                description = "Updated Description",
                dueDate = Instant.parse("2123-12-25T10:00:00Z"),
                status = TaskStatus.IN_PROGRESS.toString()
            )
            val updatedTask = TaskResponse(
                id = 1,
                title = "Updated Task",
                description = "Updated Description",
                dueDate = Instant.parse("2123-12-25T10:00:00Z"),
                status = TaskStatus.IN_PROGRESS.toString()
            )

            every { mockTaskRepository.findByIdOrNull(taskId) } returns entity
            every { mockTaskRepository.save(updateEntity) } returns updateEntity

            // When
            val result = taskService.updateTask(taskId, updatedTaskRequest)

            // Then
            verify(exactly = 1) { mockTaskRepository.findByIdOrNull(taskId) }
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
            assertThrows<GeneralException> { taskService.deleteTask(taskId) }

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
