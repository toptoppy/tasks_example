package com.toptoppy.kotlinSpringBoot.tasks

import com.toptoppy.kotlinSpringBoot.tasks.error.ErrorCode
import com.toptoppy.kotlinSpringBoot.tasks.error.GeneralException
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
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

    fun getTaskById(taskId: Long): TaskEntity? = taskRepository.findByIdOrNull(taskId)

    fun updateTask(taskId: Long, request: TaskRequest): TaskEntity? {
        if (taskRepository.existsById(taskId)) {
            return taskRepository.save(
                TaskEntity(
                    id = taskId,
                    title = request.title,
                    description = request.description,
                    dueDate = DateTimeUtils.fromString(request.dueDate),
                    status = request.status.toString(),
                )
            )
        } else {
            logger.info { "Task with ID $taskId not found" }
            return null
        }
    }

    fun deleteTask(taskId: Long) {
        try {
            if (taskRepository.existsById(taskId)) {
                taskRepository.deleteById(taskId)
            }
        }catch (ex: Exception){
            logger.info { "Task with ID $taskId not found" }
            throw GeneralException(ErrorCode.TASK_NOT_FOUND, "Task with ID $taskId not found")
        }

    }
}