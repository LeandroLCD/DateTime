package com.blipblipcode.library

import android.content.Context
import android.util.Log
import com.blipblipcode.library.model.FormatType
import com.blipblipcode.library.model.TimeSpan
import com.blipblipcode.library.throwable.InvalidFormatException
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DateTime private constructor(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val timeZone: String
) {
    private constructor(
        zoneDateTime: ZonedDateTime
    ) : this(
        zoneDateTime.year,
        zoneDateTime.monthValue,
        zoneDateTime.dayOfMonth,
        zoneDateTime.hour,
        zoneDateTime.minute,
        zoneDateTime.second,
        zoneDateTime.zone.id
    )

    val daysInMonth = daysInMonth(this.year, this.month)

    init {
        require(month in 1..12) { "Invalid month: $month" }
        require(day in 1..daysInMonth(year, month)) { "Invalid day: $day" }
    }

    companion object {
        private val PATTERNS: List<String> = listOf(
            "dd/MM/yyyy",
            "dd-MM-yyyy",
            "dd-MM-yy",
            "yyyy-MM-dd",
            "d-M-yyyy",
            "yyyy-M-d",
            "dd-MM-yyyy HH:mm:ss",
            "dd-MM-yyyy HH:mm",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )


        @Deprecated(
            message = "Initialization is no longer required. The library now uses native java.time.",
            replaceWith = ReplaceWith(""),
            level = DeprecationLevel.WARNING
        )
        fun init(@Suppress("UNUSED_PARAMETER") context: Context) {
            Log.i("DateTime", "Initialization is no longer required. The library now uses native java.time.")
        }

        fun now(): DateTime {
            val zoneId = ZoneId.systemDefault()
            return DateTime(ZonedDateTime.now(zoneId))
        }
        fun now(timeZone: String): DateTime {
            val zoneId = ZoneId.of(timeZone)
            return DateTime(ZonedDateTime.now(zoneId))
        }

        private fun isLeapYear(year: Int): Boolean {
            return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)
        }

        private fun daysInMonth(year: Int, month: Int): Int {
            return when (month) {
                2 -> if (isLeapYear(year)) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
            }
        }

        fun fromString(dateString: String): DateTime {
            val parsedDateTime = PATTERNS.firstNotNullOfOrNull { pattern ->
                runCatching {
                    val formatter = DateTimeFormatter.ofPattern(pattern)
                    // Si el patrón contiene hora, minutos o segundos, parseamos como LocalDateTime
                    if (pattern.contains("H") || pattern.contains("m") || pattern.contains("s")) {
                        LocalDateTime.parse(dateString, formatter)
                    } else {
                        // Si el patrón solo tiene fecha, parseamos como LocalDate y convertimos a LocalDateTime (inicio del día)
                        LocalDate.parse(dateString, formatter).atStartOfDay()
                    }
                }.getOrNull()
            } ?: throw InvalidFormatException(dateString = dateString)

            return DateTime(ZonedDateTime.of(parsedDateTime, ZoneId.systemDefault()))
        }

        fun fromMillis(millis: Long): DateTime {
            val zoneDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
            return DateTime(zoneDateTime)
        }

        fun fromMillis(millis: Long, timeZone:String): DateTime {
            try {
                val zoneDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.of(timeZone))
                return DateTime(zoneDateTime)
            }catch (e:Exception){
                throw InvalidFormatException(dateString = "$millis - $timeZone", cause = e)
            }
        }
    }


    fun firstDayOfMonth(): DateTime {
        return DateTime(
            year = this.year,
            month = this.month,
            day = 1,
            hour = this.hour,
            minute = this.minute,
            second = this.second,
            timeZone = this.timeZone
        )
    }

    fun lastDayOfMonth(): DateTime {
        val lastDay = daysInMonth(this.year, this.month)
        return DateTime(
            year = this.year,
            month = this.month,
            day = lastDay,
            hour = this.hour,
            minute = this.minute,
            second = this.second,
            timeZone = this.timeZone
        )
    }

    fun toMillis(): Long {
        val zonedDateTime = ZonedDateTime.of(
            year, month, day, hour, minute, second, 0, ZoneId.of(timeZone)
        )
        return zonedDateTime.toInstant().toEpochMilli()
    }

    fun toMillis(zone: String): Long {
        val zonedDateTime = ZonedDateTime.of(
            year, month, day, hour, minute, second, 0, ZoneId.of(zone)
        )
        return zonedDateTime.toInstant().toEpochMilli()
    }
    fun toMillisUTC(): Long {
        val zonedDateTime = ZonedDateTime.of(
            year, month, day, hour, minute, second, 0, ZoneId.of("UTC")
        )
        return zonedDateTime.toInstant().toEpochMilli()
    }

    fun addDays(days: Long): DateTime {
        val updatedDate =
            LocalDateTime.of(year, month, day, hour, minute, second).plusDays(days)
        return copy(updatedDate)
    }
    fun addMonths(months: Long): DateTime {
        val updatedDate =
            LocalDateTime.of(year, month, day, hour, minute, second).plusMonths(months)
        return copy(updatedDate)
    }

    fun addYears(years: Long): DateTime {
        val updatedDate =
            LocalDateTime.of(year, month, day, hour, minute, second).plusYears(years)
        return copy(updatedDate)
    }

    fun addMinutes(minutes: Long): DateTime {
        val updatedDate =
            LocalDateTime.of(year, month, day, hour, minute, second).plusMinutes(minutes)
        return copy(updatedDate)
    }

    fun addSeconds(seconds: Long): DateTime {
        val updatedDate =
            LocalDateTime.of(year, month, day, hour, minute, second).plusSeconds(seconds)
        return copy(updatedDate)
    }

    fun timeSpan(other: DateTime): TimeSpan {
        var startLdt = LocalDateTime.of(other.year, other.month, other.day, other.hour, other.minute, other.second)
        var endLdt = LocalDateTime.of(year, month, day, hour, minute, second)

        val isNegative = startLdt.isAfter(endLdt)

        if (isNegative) {
            val temp = startLdt
            startLdt = endLdt
            endLdt = temp
        }

        // Calculate Period for date components
        val period = Period.between(startLdt.toLocalDate(), endLdt.toLocalDate())

        var years = period.years
        var months = period.months
        var days = period.days

        // Now calculate time components.
        // If end time is before start time (considering only time of day),
        // we need to subtract one day and then calculate time difference.
        var hours: Long
        var minutes: Long
        var seconds: Long

        if (startLdt.toLocalTime().isAfter(endLdt.toLocalTime())) {
            days-- // Decrement a day
            // Calculate duration from start time to end time + 24 hours
            val duration = Duration.between(startLdt.toLocalTime(), endLdt.toLocalTime().plusHours(24))
            hours = duration.toHours()
            minutes = duration.toMinutes() % 60
            seconds = duration.getSeconds() % 60
        } else {
            val duration = Duration.between(startLdt.toLocalTime(), endLdt.toLocalTime())
            hours = duration.toHours()
            minutes = duration.toMinutes() % 60
            seconds = duration.getSeconds() % 60
        }

        // Handle negative result if original order was reversed
        if (isNegative) {
            years = -years
            months = -months
            days = -days
            hours = -hours
            minutes = -minutes
            seconds = -seconds
        }

        return TimeSpan(
            years,
            months,
            days,
            hours.toInt(),
            minutes.toInt(),
            seconds.toInt()
        )
    }


    override fun toString(): String {
        return "$year-${"%02d".format(month)}-${"%02d".format(day)} $hour:$minute:$second $timeZone"
    }

    fun format(formatType: FormatType): String {
        return when (formatType) {
            is FormatType.Large -> {
                "${"%02d".format(day)}${formatType.delimiter}${"%02d".format(month)}${formatType.delimiter}$year $hour:$minute:$second $timeZone"
            }

            is FormatType.Short -> {
                "${"%02d".format(day)}${formatType.delimiter}${"%02d".format(month)}${formatType.delimiter}$year"
            }
        }
    }

    fun format(pattern: String): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return kotlin.runCatching {
            formatter.format(this.toZonedDateTime())
        }.getOrElse {
            throw InvalidFormatException(dateString = pattern)
        }
    }

    fun toLocalDateTime(): LocalDateTime {
        return LocalDateTime.of(year, month, day, hour, minute, second)
    }

    fun toZonedDateTime(): ZonedDateTime {
        return ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneId.of(timeZone))
    }

    private fun copy(updatedDate: LocalDateTime): DateTime {
        return DateTime(
            year = updatedDate.year,
            month = updatedDate.monthValue,
            day = updatedDate.dayOfMonth,
            hour = updatedDate.hour,
            minute = updatedDate.minute,
            second = updatedDate.second,
            timeZone = timeZone
        )
    }

    class Builder {
        private var year: Int? = null
        private var month: Int? = null
        private var day: Int? = null

        fun setYear(year: Int) = apply { this.year = year }
        fun setMonth(month: Int) = apply { this.month = month }
        fun setDay(day: Int) = apply { this.day = day }

        fun build(): DateTime {
            val now = LocalDateTime.now()
            val timeZone = ZoneId.systemDefault().id

            val finalYear = year ?: now.year
            val finalMonth = month ?: now.monthValue
            val finalDay = day ?: now.dayOfMonth

            return DateTime(
                year = finalYear,
                month = finalMonth,
                day = finalDay,
                hour = now.hour,
                minute = now.minute,
                second = now.second,
                timeZone = timeZone
            )
        }
    }
}

