package com.toptoppy.kotlinSpringBoot.tasks.error

import mu.KotlinLogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.format.DateTimeParseException

private val logger = KotlinLogging.logger {}

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class ExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(DateTimeParseException::class)
    fun handleDateTimeParseException(ex: DateTimeParseException): ResponseEntity<*> {
        val errorBody = ErrorBody(ErrorCode.VALIDATION_ERROR, "Unable to parse ${ex.parsedString}")
        logger.error {errorBody}
        return ResponseEntity.badRequest().body(ErrorResponse(errorBody))
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(GeneralException::class)
    fun handleConflict(ex: GeneralException): ResponseEntity<*> {
        val errorBody = ErrorBody(ex.errorCode, ex.errorMessage ?: ex.errorCode.errorMessage)
        return ResponseEntity.badRequest().body(ErrorResponse(errorBody))
    }


}





