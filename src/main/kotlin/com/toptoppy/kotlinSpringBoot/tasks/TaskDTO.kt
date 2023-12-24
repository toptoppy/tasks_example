package com.toptoppy.kotlinSpringBoot.tasks

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