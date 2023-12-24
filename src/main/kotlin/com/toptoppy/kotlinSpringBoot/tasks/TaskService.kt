package com.toptoppy.kotlinSpringBoot.tasks

import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {
    private val logger = KotlinLogging.logger {}

    fun createNewTask(task: TaskRequest): TaskEntity {
        val dueDateInstant = DateTimeUtils.fromString(task.dueDate)

        return taskRepository.save(
            TaskEntity(
                title = task.title,
                description = task.description,
                dueDate = dueDateInstant,
                status = task.status.toString()
            )
        )
    }

    fun getAllTasks(): List<TaskEntity> {
        return taskRepository.findAll().also { logger.info { "Get all tasks" } }
    }

    fun getTaskById(taskId: Long): TaskEntity?
         = taskRepository.findByIdOrNull(taskId)
}