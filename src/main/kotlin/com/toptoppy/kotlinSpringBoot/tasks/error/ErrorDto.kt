package com.toptoppy.kotlinSpringBoot.tasks.error


data class ErrorResponse(
    val error: ErrorBody
)
data class ErrorBody(
    val errorCode: ErrorCode,
    val message: String
)

