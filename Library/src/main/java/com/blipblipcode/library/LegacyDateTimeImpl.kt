package com.blipblipcode.library

import com.blipblipcode.library.model.TimeSpan
import com.blipblipcode.library.throwable.InvalidFormatException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

internal object LegacyDateTimeImpl {

    private val PATTERNS = listOf(
        "dd/MM/yyyy", "dd-MM-yyyy", "dd-MM-yy", "yyyy-MM-dd",
        "d-M-yyyy", "yyyy-M-d", "dd-MM-yyyy HH:mm:ss", "dd-MM-yyyy HH:mm",
        "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        "yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss"
    )

    fun now(): DateTime {
        val calendar = Calendar.getInstance()
        return fromCalendar(calendar)
    }

    fun now(timeZone: String): DateTime {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone))
        return fromCalendar(calendar)
    }

    fun fromString(dateString: String): DateTime {
        for (pattern in PATTERNS) {
            try {
                val formatter = SimpleDateFormat(pattern, Locale.getDefault())
                val date = formatter.parse(dateString)
                if (date != null) {
                    val calendar = Calendar.getInstance().apply { time = date }
                    return fromCalendar(calendar)
                }
            } catch (_: Exception) {
                // Continue with next pattern
            }
        }
        throw InvalidFormatException(dateString = dateString)
    }

    fun fromMillis(millis: Long): DateTime {
        val calendar = Calendar.getInstance().apply { timeInMillis = millis }
        return fromCalendar(calendar)
    }

    fun fromMillis(millis: Long, timeZone: String): DateTime {
        try {
            val calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone)).apply {
                timeInMillis = millis
            }
            return fromCalendar(calendar)
        } catch (e: Exception) {
            throw InvalidFormatException(dateString = "$millis - $timeZone", cause = e)
        }
    }

    fun toMillis(dateTime: DateTime): Long {
        val calendar = toCalendar(dateTime)
        return calendar.timeInMillis
    }

    fun toMillis(dateTime: DateTime, zone: String): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(zone)).apply {
            set(dateTime.year, dateTime.month - 1, dateTime.day,
                dateTime.hour, dateTime.minute, dateTime.second)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun toMillisUTC(dateTime: DateTime): Long {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            set(dateTime.year, dateTime.month - 1, dateTime.day,
                dateTime.hour, dateTime.minute, dateTime.second)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    fun addDays(dateTime: DateTime, days: Long): DateTime {
        val calendar = toCalendar(dateTime).apply {
            add(Calendar.DAY_OF_MONTH, days.toInt())
        }
        return fromCalendar(calendar)
    }

    fun addMonths(dateTime: DateTime, months: Long): DateTime {
        val calendar = toCalendar(dateTime).apply {
            add(Calendar.MONTH, months.toInt())
        }
        return fromCalendar(calendar)
    }

    fun addYears(dateTime: DateTime, years: Long): DateTime {
        val calendar = toCalendar(dateTime).apply {
            add(Calendar.YEAR, years.toInt())
        }
        return fromCalendar(calendar)
    }

    fun addMinutes(dateTime: DateTime, minutes: Long): DateTime {
        val calendar = toCalendar(dateTime).apply {
            add(Calendar.MINUTE, minutes.toInt())
        }
        return fromCalendar(calendar)
    }

    fun addSeconds(dateTime: DateTime, seconds: Long): DateTime {
        val calendar = toCalendar(dateTime).apply {
            add(Calendar.SECOND, seconds.toInt())
        }
        return fromCalendar(calendar)
    }

    fun timeSpan(start: DateTime, end: DateTime): TimeSpan {
        val startMillis = toMillis(start)
        val endMillis = toMillis(end)
        val diffMillis = endMillis - startMillis

        val isNegative = diffMillis < 0
        val absDiffMillis = abs(diffMillis)

        // Cálculo simplificado para timespan usando milisegundos
        val totalDays = absDiffMillis / (24 * 60 * 60 * 1000L)
        val remainingMillis = absDiffMillis % (24 * 60 * 60 * 1000L)
        val hours = remainingMillis / (60 * 60 * 1000L)
        val remainingMillis2 = remainingMillis % (60 * 60 * 1000L)
        val minutes = remainingMillis2 / (60 * 1000L)
        val seconds = (remainingMillis2 % (60 * 1000L)) / 1000L

        // Para una aproximación simple, convertimos días a años y meses
        val years = (totalDays / 365).toInt()
        val months = ((totalDays % 365) / 30).toInt()
        val days = ((totalDays % 365) % 30).toInt()

        return if (isNegative) {
            TimeSpan(-years, -months, -days, -hours.toInt(), -minutes.toInt(), -seconds.toInt())
        } else {
            TimeSpan(years, months, days, hours.toInt(), minutes.toInt(), seconds.toInt())
        }
    }

    fun format(dateTime: DateTime, pattern: String): String {
        return try {
            val formatter = SimpleDateFormat(pattern, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone(dateTime.timeZone)
            val calendar = toCalendar(dateTime)
            formatter.format(calendar.time)
        } catch (e: Exception) {
            throw InvalidFormatException(dateString = pattern, cause = e)
        }
    }

    private fun fromCalendar(calendar: Calendar): DateTime {
        return DateTime(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            calendar.get(Calendar.SECOND),
            calendar.timeZone.id
        )
    }

    private fun toCalendar(dateTime: DateTime): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone(dateTime.timeZone)).apply {
            set(dateTime.year, dateTime.month - 1, dateTime.day,
                dateTime.hour, dateTime.minute, dateTime.second)
            set(Calendar.MILLISECOND, 0)
        }
    }
}
