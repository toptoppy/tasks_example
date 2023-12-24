package com.toptoppy.kotlinSpringBoot.tasks

import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
    inner class CreateNewTask{
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
            val pastDueDateTask = TaskRequest("Past Due Date", "Description", "2022-01-01T12:00:00Z", TaskStatus.PENDING)

            // When/Then
            assertThrows<Exception> {
                taskService.createNewTask(pastDueDateTask)
            }

            verify { mockTaskRepository wasNot Called }
        }

        @Test
        fun `when creating task with valid due date, should succeed`() {
            // Given
            val validDueDateTask = TaskRequest("Valid Due Date", "Description", "2024-01-01T12:00:00Z", TaskStatus.PENDING)
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

}
