package com.toptoppy.kotlinSpringBoot.tasks.error

import java.lang.Exception

class GeneralException(
    val errorCode: ErrorCode,
    val errorMessage: String? = null
): Exception(errorCode.errorMessage)