package com.toptoppy.kotlinSpringBoot.tasks

import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskRequest
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "TASKS", description = "The Tasks Api") // this for modified swagger
@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {

    @Operation(
        summary = "Fetch all tasks",
        description = "Fetches all task entities and their data from the data source"
    )
    @ApiResponses(value = [ApiResponse(responseCode = "200", description = "Successful operation")])
    @GetMapping
    fun getAllTasks(): ResponseEntity<List<TaskResponse>> {
        val tasks = taskService.getAllTasks()
        return ResponseEntity.ok(tasks)
    }

    @PostMapping
    fun createTask(@RequestBody task: TaskRequest): ResponseEntity<TaskResponse> {
        val createdTask = taskService.createNewTask(task)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask)
    }

    @GetMapping("/{taskId}")
    fun getTaskById(@PathVariable taskId: Long): ResponseEntity<TaskResponse?> {
        val task = taskService.getTaskById(taskId)
        return task?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/{taskId}")
    fun updateTask(@PathVariable taskId: Long, @RequestBody request: TaskRequest): ResponseEntity<TaskResponse?> {
        val updatedTask = taskService.updateTask(taskId, request)
        return updatedTask?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(@PathVariable taskId: Long): ResponseEntity<Unit> {
        taskService.deleteTask(taskId)
        return ResponseEntity.noContent().build()
    }
}