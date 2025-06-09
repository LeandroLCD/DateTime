package com.blipblipcode.library.model

import com.blipblipcode.library.DateTime

class DateTimeRange private constructor(
    val start: DateTime,
    val end: DateTime
) {
    init {
        require(start.toMillis() <= end.toMillis()) {
            "The start date must be prior or equal to the final date"
        }
    }

    fun span(): TimeSpan = end.timeSpan(start)

    fun contains(date: DateTime): Boolean {
        val dateMillis = date.toMillis()
        return dateMillis in start.toMillis()..end.toMillis()
    }

    fun overlaps(other: DateTimeRange): Boolean {
        return !(end.toMillis() < other.start.toMillis() ||
                start.toMillis() > other.end.toMillis())
    }

    override fun toString(): String = "[$start - $end]"

    class Builder {
        private var start: DateTime? = null
        private var end: DateTime? = null

        fun from(start: DateTime) = apply { this.start = start }
        fun to(end: DateTime) = apply { this.end = end }
        fun startingNow() = apply { this.start = DateTime.now() }
        fun endingNow() = apply { this.end = DateTime.now() }

        fun build(): DateTimeRange {
            val finalStart = start ?: DateTime.now()
            val finalEnd = end ?: finalStart.addDays(1)

            return DateTimeRange(finalStart, finalEnd)
        }
    }

    companion object {
        fun builder() = Builder()
    }
}