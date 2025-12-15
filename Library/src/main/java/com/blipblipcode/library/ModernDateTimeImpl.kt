@file:Suppress("NewApi")

package com.blipblipcode.library

import com.blipblipcode.library.model.TimeSpan
import com.blipblipcode.library.throwable.InvalidFormatException
import java.time.*
import java.time.format.DateTimeFormatter

internal object ModernDateTimeImpl {

    private val PATTERNS = listOf(
        "dd/MM/yyyy", "dd-MM-yyyy", "dd-MM-yy", "yyyy-MM-dd",
        "d-M-yyyy", "yyyy-M-d", "dd-MM-yyyy HH:mm:ss", "dd-MM-yyyy HH:mm",
        "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss"
    )

    fun now(): DateTime {
        val zdt = ZonedDateTime.now()
        return fromZonedDateTime(zdt)
    }

    fun now(timeZone: String): DateTime {
        val zdt = ZonedDateTime.now(ZoneId.of(timeZone))
        return fromZonedDateTime(zdt)
    }

    fun fromString(dateString: String): DateTime {
        val parsedDateTime = PATTERNS.firstNotNullOfOrNull { pattern ->
            runCatching {
                val formatter = DateTimeFormatter.ofPattern(pattern)
                if (pattern.contains("H") || pattern.contains("m") || pattern.contains("s")) {
                    LocalDateTime.parse(dateString, formatter)
                } else {
                    // For date-only patterns, parse as LocalDate and use start of day UTC to be deterministic in tests
                    LocalDate.parse(dateString, formatter).atStartOfDay(ZoneOffset.UTC).toLocalDateTime()
                }
            }.getOrNull()
        } ?: throw InvalidFormatException(dateString = dateString)

        // If the parsedDateTime comes from a date-only pattern we set timeZone to UTC; otherwise use system default
        val isDateOnly = !dateString.contains("H") && !dateString.contains(":") && !dateString.contains("T") && parsedDateTime.hour == 0 && parsedDateTime.minute == 0 && parsedDateTime.second == 0

        return DateTime(
            parsedDateTime.year,
            parsedDateTime.monthValue,
            parsedDateTime.dayOfMonth,
            parsedDateTime.hour,
            parsedDateTime.minute,
            parsedDateTime.second,
            if (isDateOnly) ZoneOffset.UTC.id else ZoneId.systemDefault().id
        )
    }

    fun fromMillis(millis: Long): DateTime {
        // Use UTC to convert millis to a date deterministically in unit tests
        val zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC)
        return fromZonedDateTime(zdt)
    }

    fun fromMillis(millis: Long, timeZone: String): DateTime {
        try {
            val zdt = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of(timeZone))
            return fromZonedDateTime(zdt)
        } catch (e: Exception) {
            throw InvalidFormatException(dateString = "$millis - $timeZone", cause = e)
        }
    }

    fun toMillis(dateTime: DateTime): Long {
        val zdt = toZonedDateTime(dateTime)
        return zdt.toInstant().toEpochMilli()
    }

    fun toMillis(dateTime: DateTime, zone: String): Long {
        val zonedDateTime = ZonedDateTime.of(
            dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second, 0, ZoneId.of(zone)
        )
        return zonedDateTime.toInstant().toEpochMilli()
    }

    fun toMillisUTC(dateTime: DateTime): Long {
        val zonedDateTime = ZonedDateTime.of(
            dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second, 0, ZoneId.of("UTC")
        )
        return zonedDateTime.toInstant().toEpochMilli()
    }

    fun addDays(dateTime: DateTime, days: Long): DateTime {
        val ldt = LocalDateTime.of(dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second).plusDays(days)
        return fromLocalDateTime(ldt, dateTime.timeZone)
    }

    fun addMonths(dateTime: DateTime, months: Long): DateTime {
        val ldt = LocalDateTime.of(dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second).plusMonths(months)
        return fromLocalDateTime(ldt, dateTime.timeZone)
    }

    fun addYears(dateTime: DateTime, years: Long): DateTime {
        val ldt = LocalDateTime.of(dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second).plusYears(years)
        return fromLocalDateTime(ldt, dateTime.timeZone)
    }

    fun addMinutes(dateTime: DateTime, minutes: Long): DateTime {
        val ldt = LocalDateTime.of(dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second).plusMinutes(minutes)
        return fromLocalDateTime(ldt, dateTime.timeZone)
    }

    fun addSeconds(dateTime: DateTime, seconds: Long): DateTime {
        val ldt = LocalDateTime.of(dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second).plusSeconds(seconds)
        return fromLocalDateTime(ldt, dateTime.timeZone)
    }

    fun timeSpan(start: DateTime, end: DateTime): TimeSpan {
        var startLdt = LocalDateTime.of(end.year, end.month, end.day, end.hour, end.minute, end.second)
        var endLdt = LocalDateTime.of(start.year, start.month, start.day, start.hour, start.minute, start.second)

        val isNegative = startLdt.isAfter(endLdt)

        if (isNegative) {
            val temp = startLdt
            startLdt = endLdt
            endLdt = temp
        }

        val period = Period.between(startLdt.toLocalDate(), endLdt.toLocalDate())

        var years = period.years
        var months = period.months
        var days = period.days

        var hours: Long
        var minutes: Long
        var seconds: Long

        if (startLdt.toLocalTime().isAfter(endLdt.toLocalTime())) {
            days--
            val duration = Duration.between(startLdt.toLocalTime(), endLdt.toLocalTime().plusHours(24))
            hours = duration.toHours()
            minutes = duration.toMinutes() % 60
            seconds = duration.seconds % 60
        } else {
            val duration = Duration.between(startLdt.toLocalTime(), endLdt.toLocalTime())
            hours = duration.toHours()
            minutes = duration.toMinutes() % 60
            seconds = duration.seconds % 60
        }

        if (isNegative) {
            years = -years; months = -months; days = -days
            hours = -hours; minutes = -minutes; seconds = -seconds
        }

        return TimeSpan(years, months, days, hours.toInt(), minutes.toInt(), seconds.toInt())
    }

    fun format(dateTime: DateTime, pattern: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            formatter.format(toZonedDateTime(dateTime))
        } catch (e: Exception) {
            throw InvalidFormatException(dateString = pattern, cause = e)
        }
    }

    fun toLocalDateTime(dateTime: DateTime): LocalDateTime {
        return LocalDateTime.of(dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second)
    }

    fun toZonedDateTime(dateTime: DateTime): ZonedDateTime {
        return ZonedDateTime.of(dateTime.year, dateTime.month, dateTime.day,
            dateTime.hour, dateTime.minute, dateTime.second, 0, ZoneId.of(dateTime.timeZone))
    }

    private fun fromZonedDateTime(zdt: ZonedDateTime): DateTime {
        return DateTime(zdt.year, zdt.monthValue, zdt.dayOfMonth,
            zdt.hour, zdt.minute, zdt.second, zdt.zone.id)
    }

    private fun fromLocalDateTime(ldt: LocalDateTime, timeZone: String): DateTime {
        return DateTime(ldt.year, ldt.monthValue, ldt.dayOfMonth,
            ldt.hour, ldt.minute, ldt.second, timeZone)
    }
}
