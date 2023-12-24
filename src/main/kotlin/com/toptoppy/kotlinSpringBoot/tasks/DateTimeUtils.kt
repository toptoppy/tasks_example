package com.toptoppy.kotlinSpringBoot.tasks

import com.toptoppy.kotlinSpringBoot.tasks.error.ErrorCode
import com.toptoppy.kotlinSpringBoot.tasks.error.GeneralException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateTimeUtils {
    fun fromString(dateString: String): Instant {
        try {
            val dueDateInstant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(dateString))
            return dueDateInstant
        } catch (ex: DateTimeParseException) {
            throw ex
        }
    }
}