package com.toptoppy.kotlinSpringBoot.tasks

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

@Tag(name = "TASKS", description = "the Tasks Api") // this for modified swagger
@RestController
@RequestMapping("/tasks")
class TaskController(private val taskService: TaskService) {

    @PostMapping
    fun createTask(@RequestBody task: TaskRequest): ResponseEntity<TaskEntity> {
        val createdTask = taskService.createNewTask(task)
        return ResponseEntity(createdTask, HttpStatus.CREATED)
    }

    @Operation(
        summary = "Fetch all task",
        description = "fetches all task entities and their data from data source")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "successful operation")
        ])
    @GetMapping
    fun getAllTasks(): ResponseEntity<List<TaskEntity>> {
        val tasks = taskService.getAllTasks()
        return ResponseEntity(tasks, HttpStatus.OK)
    }

    @GetMapping("/{taskId}")
    fun getTaskById(@PathVariable taskId: Long): ResponseEntity<TaskEntity?> {
        val task = taskService.getTaskById(taskId)
        return if (task != null) {
            ResponseEntity(task, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/{taskId}")
    fun updateTask(@PathVariable taskId: Long, @RequestBody request: TaskRequest): ResponseEntity<TaskEntity?> {
        val updatedTask = taskService.updateTask(taskId, request)
        return if (updatedTask != null) {
            ResponseEntity(updatedTask, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @DeleteMapping("/{taskId}")
    fun deleteTask(@PathVariable taskId: Long): ResponseEntity<Unit> {
        taskService.deleteTask(taskId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
