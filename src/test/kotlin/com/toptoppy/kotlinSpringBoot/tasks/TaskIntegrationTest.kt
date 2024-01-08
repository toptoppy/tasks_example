package com.toptoppy.kotlinSpringBoot.tasks

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskRequest
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskStatus
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.Instant
import java.time.temporal.ChronoUnit

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    private val mapper = jacksonObjectMapper().findAndRegisterModules()
    private val dueDateInstant = Instant.now().plus(30, ChronoUnit.DAYS)

    @Autowired
    private lateinit var taskRepository: TaskRepository


    @Nested
    inner class Post {
        @Test
        fun `should create a new task`() {
            val newTask = TaskRequest("New Task", "New Description", "2122-01-01T12:00:00Z", TaskStatus.PENDING)

            mockMvc.perform(
                MockMvcRequestBuilders.post("/tasks").content(mapper.writeValueAsString(newTask))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PENDING"))
        }

        @Test
        fun `should return bad request for invalid JSON`() {
            val invalidJson = "{ invalid json }"

            mockMvc.perform(
                MockMvcRequestBuilders.post("/tasks").content(invalidJson)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Get {
        @BeforeAll
        fun setUp() {
            taskRepository.deleteAll()
            taskRepository.saveAll(
                listOf(
                    TaskEntity(
                        title = "task 1",
                        description = "1",
                        dueDate = dueDateInstant,
                        status = TaskStatus.PENDING.toString()
                    ),
                    TaskEntity(
                        title = "task 2",
                        description = "2",
                        dueDate = dueDateInstant,
                        status = TaskStatus.IN_PROGRESS.toString()
                    ),
                    TaskEntity(
                        title = "task 3",
                        description = "3",
                        dueDate = dueDateInstant,
                        status = TaskStatus.COMPLETED.toString()
                    )
                )
            )
        }

        @Test
        fun `should get all tasks`() {


            mockMvc.perform(
                MockMvcRequestBuilders.get("/tasks")
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status").value("PENDING"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].status").value("IN_PROGRESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].status").value("COMPLETED"))
        }

        @Test
        fun `should get task by ID`() {
            val taskId = taskRepository.save(
                TaskEntity(
                    title = "task 4",
                    description = "4",
                    dueDate = dueDateInstant,
                    status = TaskStatus.COMPLETED.toString()
                )
            ).id

            mockMvc.perform(
                MockMvcRequestBuilders.get("/tasks/$taskId")
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("COMPLETED"))
        }

        @Test
        fun `should return not found when task ID is not found`() {
            val taskId = 99L

            mockMvc.perform(
                MockMvcRequestBuilders.get("/tasks/$taskId")
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }

    @Nested
    inner class Put {

        @Test
        fun `should update an existing task`() {
            val existsTask = taskRepository.save(
                TaskEntity(
                    title = "task 1",
                    description = "1",
                    dueDate = dueDateInstant,
                    status = TaskStatus.PENDING.toString()
                )
            )
            val taskId = existsTask.id
            val updatedTaskRequest =
                TaskRequest("Updated Task", "Updated Description", "2123-12-25T12:00:00Z", TaskStatus.IN_PROGRESS)


            mockMvc.perform(
                MockMvcRequestBuilders.put("/tasks/$taskId")
                    .content(mapper.writeValueAsString(updatedTaskRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("IN_PROGRESS"))
        }

        @Test
        fun `should return not found when updating a non-existent task`() {
            val taskId = 99L
            val updatedTaskRequest =
                TaskRequest("Updated Task", "Updated Description", "2100-12-25T12:00:00Z", TaskStatus.IN_PROGRESS)

            mockMvc.perform(
                MockMvcRequestBuilders.put("/tasks/$taskId")
                    .content(mapper.writeValueAsString(updatedTaskRequest))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }

    @Nested
    inner class Patch {
        @Test
        fun `should partially update an existing task`() {
            val existsTask = taskRepository.save(
                TaskEntity(
                    title = "task 1",
                    description = "1",
                    dueDate = dueDateInstant,
                    status = TaskStatus.PENDING.toString()
                )
            )
            val taskId = existsTask.id
            val partialUpdate = mapOf(
                "title" to "Updated Title",
                "description" to "Updated Description"
            )

            mockMvc.perform(
                MockMvcRequestBuilders.patch("/tasks/$taskId")
                    .content(mapper.writeValueAsString(partialUpdate))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Updated Title"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Updated Description"))
        }

        @Test
        fun `should return not found when patching a non-existent task`() {
            val taskId = 99L
            val partialUpdate = mapOf(
                "title" to "Updated Title",
                "description" to "Updated Description"
            )

            mockMvc.perform(
                MockMvcRequestBuilders.patch("/tasks/$taskId")
                    .content(mapper.writeValueAsString(partialUpdate))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(MockMvcResultMatchers.status().isNotFound)
        }
    }

    @Nested
    inner class Delete {
        @Test
        fun `should delete an existing task`() {
            val existsTask = taskRepository.save(
                TaskEntity(
                    title = "task 1",
                    description = "1",
                    dueDate = dueDateInstant,
                    status = TaskStatus.PENDING.toString()
                )
            )
            val taskId = existsTask.id

            mockMvc.perform(
                MockMvcRequestBuilders.delete("/tasks/$taskId")
            )
                .andExpect(MockMvcResultMatchers.status().isNoContent)
        }

        @Test
        fun `should return not found when deleting a non-existent task`() {
            val taskId = 99L

            mockMvc.perform(
                MockMvcRequestBuilders.delete("/tasks/$taskId")
            )
                .andExpect(MockMvcResultMatchers.status().is4xxClientError)
        }
    }
}
