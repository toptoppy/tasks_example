package com.toptoppy.kotlinSpringBoot.tasks

import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskRequest
import com.toptoppy.kotlinSpringBoot.tasks.dto.TaskResponse
import com.toptoppy.kotlinSpringBoot.tasks.error.ErrorCode
import com.toptoppy.kotlinSpringBoot.tasks.error.GeneralException
import com.toptoppy.kotlinSpringBoot.tasks.utils.DateTimeUtils
import mu.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {
    private val logger = KotlinLogging.logger {}

    fun createNewTask(task: TaskRequest): TaskResponse {
        val dueDateInstant = DateTimeUtils.parseIso8601Utc(task.dueDate)
        try {
            val savedTask = taskRepository.save(
                TaskEntity(
                    title = task.title,
                    description = task.description,
                    dueDate = dueDateInstant,
                    status = task.status.toString()
                )
            )
            return TaskResponse.fromTaskEntity(savedTask)
        } catch (ex: Exception) {
            logger.error(ex) { "Failed to create a new task" }
            throw GeneralException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to create a new task")
        }
    }

    fun getAllTasks(): List<TaskResponse> {
        logger.info { "Get all tasks" }
        return taskRepository.findAll().map { TaskResponse.fromTaskEntity(it) }
    }

    fun getTaskById(taskId: Long): TaskResponse? =
        taskRepository.findByIdOrNull(taskId)?.let { TaskResponse.fromTaskEntity(it) }

    fun updateTask(taskId: Long, request: TaskRequest): TaskResponse? {
        val taskEntity = taskRepository.findByIdOrNull(taskId)
        return taskEntity?.let {
            val entity = taskRepository.save(
                TaskEntity(
                    id = taskId,
                    title = request.title,
                    description = request.description,
                    dueDate = DateTimeUtils.parseIso8601Utc(request.dueDate),
                    status = request.status.toString(),
                    createAt = it.createAt
                )
            )
            TaskResponse.fromTaskEntity(entity)
        } ?: run {
            logger.info { "Task with ID $taskId not found" }
            null
        }
    }

    fun deleteTask(taskId: Long) {
        if (taskRepository.existsById(taskId)) {
            taskRepository.deleteById(taskId)
        } else {
            logger.info { "Task with ID $taskId not found" }
            throw GeneralException(ErrorCode.TASK_NOT_FOUND, "Task with ID $taskId not found")
        }
    }
}