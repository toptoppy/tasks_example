package com.toptoppy.kotlinSpringBoot.tasks.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.toptoppy.kotlinSpringBoot.tasks.TaskService
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskRequest
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskResponse
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskStatus
import com.toptoppy.kotlinSpringBoot.tasks.error.ErrorCode
import com.toptoppy.kotlinSpringBoot.tasks.error.GeneralException
import com.toptoppy.kotlinSpringBoot.tasks.utils.DateTimeUtils
import io.mockk.every
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.Instant
import java.time.temporal.ChronoUnit


@WebMvcTest
class TaskControllerTest(
    @Autowired val mockMvc: MockMvc
) {

    @MockkBean
    lateinit var taskService: TaskService
    val mapper = jacksonObjectMapper().findAndRegisterModules()

    private val dueDateInstant = Instant.now().plus(30, ChronoUnit.DAYS)

    private val taskEntity = TaskResponse(
        1,
        "New Task",
        "New Description",
        dueDateInstant,
        TaskStatus.PENDING.toString()
    )

    @Nested
    inner class Post {
        @Test
        fun `should create a new task`() {
            val newTask = TaskRequest("New Task", "New Description", "2122-01-01T12:00:00Z", TaskStatus.PENDING)

            val createdTask = taskEntity

            every { taskService.createNewTask(newTask) } returns createdTask;

            mockMvc.perform(
                post("/tasks").content(mapper.writeValueAsString(newTask))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PENDING"));
        }
    }

    @Nested
    inner class Get {
        @Test
        fun `should get all tasks`() {
            every { taskService.getAllTasks() } returns listOf(taskEntity, taskEntity, taskEntity)

            mockMvc.perform(
                get("/tasks")
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].status").value("PENDING"))
                .andExpect(jsonPath("$[2].status").value("PENDING"))
        }

        @Test
        fun `should get task by ID`() {
            val taskId = 1L
            every { taskService.getTaskById(taskId) } returns taskEntity

            mockMvc.perform(
                get("/tasks/$taskId")
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PENDING"))
        }

        @Test
        fun `should return not found when task ID is not found`() {
            val taskId = 2L
            every { taskService.getTaskById(taskId) } returns null

            mockMvc.perform(
                get("/tasks/$taskId")
            )
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class Put {
        @Test
        fun `should update an existing task`() {
            val taskId = 1L
            val updatedTaskRequest = TaskRequest("Updated Task", "Updated Description", "2123-12-25T12:00:00Z", TaskStatus.IN_PROGRESS)
            val updatedTaskEntity = TaskResponse(
                taskId,
                "Updated Task",
                "Updated Description",
                DateTimeUtils.parseIso8601Utc("2123-12-25T12:00:00Z"),
                TaskStatus.IN_PROGRESS.toString()
            )

            every { taskService.updateTask(taskId, updatedTaskRequest) } returns updatedTaskEntity

            mockMvc.perform(
                put("/tasks/$taskId")
                    .content(mapper.writeValueAsString(updatedTaskRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
        }

        @Test
        fun `should return not found when updating a non-existent task`() {
            val taskId = 2L
            val updatedTaskRequest = TaskRequest("Updated Task", "Updated Description", "2023-12-25T12:00:00Z", TaskStatus.IN_PROGRESS)

            every { taskService.updateTask(taskId, updatedTaskRequest) } returns null

            mockMvc.perform(
                put("/tasks/$taskId")
                    .content(mapper.writeValueAsString(updatedTaskRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should delete an existing task`() {
            val taskId = 1L

            every { taskService.deleteTask(taskId) } returns Unit

            mockMvc.perform(
                delete("/tasks/$taskId")
            )
                .andExpect(status().isNoContent)
        }

        @Test
        fun `should return not found when deleting a non-existent task`() {
            val taskId = 2L

            every { taskService.deleteTask(taskId) } throws GeneralException(ErrorCode.TASK_NOT_FOUND)

            mockMvc.perform(
                delete("/tasks/$taskId")
            )
                .andExpect(status().is4xxClientError) //TODO: should expect 404 NOT FOUND
        }
    }

    @Test
    fun `should return bad request for invalid JSON`() {
        val invalidJson = "{ invalid json }"

        mockMvc.perform(
            post("/tasks").content(invalidJson)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isBadRequest)
    }
}
