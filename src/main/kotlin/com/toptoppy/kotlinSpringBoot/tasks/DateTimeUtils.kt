package com.toptoppy.kotlinSpringBoot.tasks

import java.time.Instant
import java.time.format.DateTimeFormatter

object DateTimeUtils {
    fun fromString(dateString: String): Instant {
        val dueDateInstant = Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(dateString))

        if (dueDateInstant.isBefore(Instant.now())) {
            throw Exception("Due date must be in the future")
        }

        return dueDateInstant
    }

}