package com.toptoppy.kotlinSpringBoot.tasks

import java.time.Instant
import java.time.format.DateTimeFormatter

object DateTimeUtils {

    fun parseIso8601Utc(dateString: String): Instant {
        return Instant.from(DateTimeFormatter.ISO_DATE_TIME.parse(dateString))
    }

}