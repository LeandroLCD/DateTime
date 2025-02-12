package com.blipblipcode.library.model

data class TimeSpan(
    val years: Int,
    val months: Int,
    val days: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int
) {

    fun totalDays(): Long {
        return years * 365L + months * 30L + days
    }

    fun totalHours(): Long {
        return totalDays() * 24 + hours
    }

    fun totalMinutes(): Long {
        return totalHours() * 60 + minutes
    }

    fun totalSeconds(): Long {
        return totalMinutes() * 60 + seconds
    }

    override fun toString(): String {
        return "${years}y ${months}m ${days}d ${hours}h ${minutes}m ${seconds}s"
    }

    operator fun plus(other: TimeSpan): TimeSpan {
        return TimeSpan(
            years + other.years,
            months + other.months,
            days + other.days,
            hours + other.hours,
            minutes + other.minutes,
            seconds + other.seconds
        )
    }

    operator fun minus(other: TimeSpan): TimeSpan {
        return TimeSpan(
            years - other.years,
            months - other.months,
            days - other.days,
            hours - other.hours,
            minutes - other.minutes,
            seconds - other.seconds
        )
    }
}
