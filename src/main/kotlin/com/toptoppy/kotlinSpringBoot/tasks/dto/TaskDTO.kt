package com.toptoppy.kotlinSpringBoot.tasks.dto

import com.toptoppy.kotlinSpringBoot.tasks.TaskEntity
import java.time.Instant

data class TaskRequest(
    var title: String,
    var description: String,
    var dueDate: String,
    var status: TaskStatus,
)

enum class TaskStatus{
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String,
    val dueDate: Instant,
    val status: String
) {
    companion object{
        fun fromTaskEntity(taskEntity: TaskEntity): TaskResponse {
            return TaskResponse(
                id = taskEntity.id!!,
                title = taskEntity.title,
                description = taskEntity.description,
                dueDate = taskEntity.dueDate,
                status = taskEntity.status
            )
        }
    }
}