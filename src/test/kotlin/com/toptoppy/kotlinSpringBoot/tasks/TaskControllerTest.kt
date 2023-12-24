package com.toptoppy.kotlinSpringBoot.tasks

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*


@WebMvcTest
class TaskControllerTest(
    @Autowired val mockMvc: MockMvc
) {

    @MockkBean
    lateinit var taskService: TaskService
    val mapper = jacksonObjectMapper().findAndRegisterModules()

    @Test
    fun `should create a new task`() {
        val newTask = TaskRequest("New Task", "New Description", "2023-12-24T15:30:45Z", TaskStatus.PENDING)
        val createdTask = TaskEntity(
            1,
            "New Task",
            "New Description",
            DateTimeUtils.fromString("2023-12-24T15:30:45Z"),
            TaskStatus.PENDING.toString()
        )

        every { taskService.createNewTask(newTask) } returns createdTask;

        mockMvc.perform(
            post("/tasks").content(mapper.writeValueAsString(newTask))
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
