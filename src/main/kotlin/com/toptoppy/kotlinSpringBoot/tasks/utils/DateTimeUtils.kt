package com.toptoppy.kotlinSpringBoot.tasks.utils

import com.toptoppy.kotlinSpringBoot.tasks.error.GeneralException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object DateTimeUtils {
    fun parseIso8601Utc(dateString: String): Instant {
        try {
            val dueDateInstant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(dateString))

            if (dueDateInstant.isBefore(Instant.now())) {
                throw Exception("Due date must be in the future")
            }

            return dueDateInstant
        } catch (ex: DateTimeParseException) {
            throw ex
        }
    }
}