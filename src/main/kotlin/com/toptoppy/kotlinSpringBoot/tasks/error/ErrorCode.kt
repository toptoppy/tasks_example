package com.toptoppy.kotlinSpringBoot.tasks.error

enum class ErrorCode(val errorMessage: String){
    VALIDATION_ERROR("Validation error"),
    TASK_NOT_FOUND("Task not found"),
}