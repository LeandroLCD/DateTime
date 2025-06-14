package com.blipblipcode.library

import android.content.Context
import com.blipblipcode.library.model.FormatType
import com.blipblipcode.library.model.TimeSpan
import com.blipblipcode.library.throwable.InvalidFormatException
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

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


        fun init(context: Context) {
            AndroidThreeTen.init(context)
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
                        org.threeten.bp.LocalDate.parse(dateString, formatter).atStartOfDay()
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
        val thisDateTime = LocalDateTime.of(year, month, day, hour, minute, second)
        val otherDateTime = LocalDateTime.of(
            other.year,
            other.month,
            other.day,
            other.hour,
            other.minute,
            other.second
        )

        val years = ChronoUnit.YEARS.between(otherDateTime, thisDateTime)
        val months = ChronoUnit.MONTHS.between(otherDateTime, thisDateTime) % 12
        val days = ChronoUnit.DAYS.between(otherDateTime, thisDateTime) % 30
        val hours = ChronoUnit.HOURS.between(otherDateTime, thisDateTime) % 24
        val minutes = ChronoUnit.MINUTES.between(otherDateTime, thisDateTime) % 60
        val seconds = ChronoUnit.SECONDS.between(otherDateTime, thisDateTime) % 60

        return TimeSpan(
            years.toInt(),
            months.toInt(),
            days.toInt(),
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

