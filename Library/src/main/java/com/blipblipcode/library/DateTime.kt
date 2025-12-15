@file:Suppress("NewApi")

package com.blipblipcode.library

import android.content.Context
import android.util.Log
import com.blipblipcode.library.model.FormatType
import com.blipblipcode.library.model.TimeSpan

@Suppress("unused")
class DateTime internal constructor(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val timeZone: String
) {

    val daysInMonth = daysInMonth(this.year, this.month)

    init {
        require(month in 1..12) { "Invalid month: $month" }
        require(day in 1..daysInMonth(year, month)) { "Invalid day: $day" }
    }

    companion object {
        @Deprecated(
            message = "Initialization is no longer required. The library now uses native APIs.",
            replaceWith = ReplaceWith(""),
            level = DeprecationLevel.WARNING
        )
        fun init(@Suppress("UNUSED_PARAMETER") context: Context) {
            Log.i("DateTime", "Initialization is no longer required. The library now uses native APIs.")
        }

        private fun useModernImpl(): Boolean {
            return try {
                Class.forName("java.time.LocalDate")
                true
            } catch (e: Throwable) {
                false
            }
        }

        fun now(): DateTime {
            return if (useModernImpl()) {
                ModernDateTimeImpl.now()
            } else {
                LegacyDateTimeImpl.now()
            }
        }

        fun now(timeZone: String): DateTime {
            return if (useModernImpl()) {
                ModernDateTimeImpl.now(timeZone)
            } else {
                LegacyDateTimeImpl.now(timeZone)
            }
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
            return if (useModernImpl()) {
                ModernDateTimeImpl.fromString(dateString)
            } else {
                LegacyDateTimeImpl.fromString(dateString)
            }
        }

        fun fromMillis(millis: Long): DateTime {
            return if (useModernImpl()) {
                ModernDateTimeImpl.fromMillis(millis)
            } else {
                LegacyDateTimeImpl.fromMillis(millis)
            }
        }

        fun fromMillis(millis: Long, timeZone: String): DateTime {
            return if (useModernImpl()) {
                ModernDateTimeImpl.fromMillis(millis, timeZone)
            } else {
                LegacyDateTimeImpl.fromMillis(millis, timeZone)
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
        return if (useModernImpl()) {
            ModernDateTimeImpl.toMillis(this)
        } else {
            LegacyDateTimeImpl.toMillis(this)
        }
    }

    fun toMillis(zone: String): Long {
        return if (useModernImpl()) {
            ModernDateTimeImpl.toMillis(this, zone)
        } else {
            LegacyDateTimeImpl.toMillis(this, zone)
        }
    }

    fun toMillisUTC(): Long {
        return if (useModernImpl()) {
            ModernDateTimeImpl.toMillisUTC(this)
        } else {
            LegacyDateTimeImpl.toMillisUTC(this)
        }
    }

    fun addDays(days: Long): DateTime {
        return if (useModernImpl()) {
            ModernDateTimeImpl.addDays(this, days)
        } else {
            LegacyDateTimeImpl.addDays(this, days)
        }
    }

    fun addMonths(months: Long): DateTime {
        return if (useModernImpl()) {
            ModernDateTimeImpl.addMonths(this, months)
        } else {
            LegacyDateTimeImpl.addMonths(this, months)
        }
    }

    fun addYears(years: Long): DateTime {
        return if (useModernImpl()) {
            ModernDateTimeImpl.addYears(this, years)
        } else {
            LegacyDateTimeImpl.addYears(this, years)
        }
    }

    fun addMinutes(minutes: Long): DateTime {
        return if (useModernImpl()) {
            ModernDateTimeImpl.addMinutes(this, minutes)
        } else {
            LegacyDateTimeImpl.addMinutes(this, minutes)
        }
    }

    fun addSeconds(seconds: Long): DateTime {
        return if (useModernImpl()) {
            ModernDateTimeImpl.addSeconds(this, seconds)
        } else {
            LegacyDateTimeImpl.addSeconds(this, seconds)
        }
    }

    fun timeSpan(other: DateTime): TimeSpan {
        return if (useModernImpl()) {
            ModernDateTimeImpl.timeSpan(this, other)
        } else {
            LegacyDateTimeImpl.timeSpan(this, other)
        }
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
        return if (useModernImpl()) {
            ModernDateTimeImpl.format(this, pattern)
        } else {
            LegacyDateTimeImpl.format(this, pattern)
        }
    }

    @androidx.annotation.RequiresApi(26)
    fun toLocalDateTime(): java.time.LocalDateTime {
        return ModernDateTimeImpl.toLocalDateTime(this)

    }

    @androidx.annotation.RequiresApi(26)
    fun toZonedDateTime(): java.time.ZonedDateTime {
        return ModernDateTimeImpl.toZonedDateTime(this)
    }

    class Builder {
        private var year: Int? = null
        private var month: Int? = null
        private var day: Int? = null

        fun setYear(year: Int) = apply { this.year = year }
        fun setMonth(month: Int) = apply { this.month = month }
        fun setDay(day: Int) = apply { this.day = day }

        fun build(): DateTime {
            val now = now()
            return DateTime(
                year = year ?: now.year,
                month = month ?: now.month,
                day = day ?: now.day,
                hour = now.hour,
                minute = now.minute,
                second = now.second,
                timeZone = now.timeZone
            )
        }
    }
}
