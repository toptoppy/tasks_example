package com.toptoppy.kotlinSpringBoot.tasks

import org.springframework.stereotype.Service

@Service
class TaskService(
    private val taskRepository: TaskRepository
) {

    fun createNewTask(task: TaskRequest): TaskEntity {
        val dueDateInstant = DateTimeUtils.parseIso8601Utc(task.date)

        return taskRepository.save(TaskEntity(
            title = task.title,
            description = task.description,
            date = dueDateInstant,
            status = task.status.toString()
        ))
    }
}