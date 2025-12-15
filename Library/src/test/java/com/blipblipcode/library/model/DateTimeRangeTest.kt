package com.blipblipcode.library.model

import com.blipblipcode.library.DateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DateTimeRangeTest {

    @Test
    fun `span with positive duration`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2024-01-01")
        val range = DateTimeRange.builder().from(start).to(end).build()

        /**WHEN**/
        val span = range.span()

        /**THEN**/
        assertEquals(1, span.years)
        assertEquals(0, span.months)
        assertEquals(0, span.days)
    }

    @Test
    fun `span with zero duration`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val range = DateTimeRange.builder().from(start).to(start).build()

        /**WHEN**/
        val span = range.span()

        /**THEN**/
        assertEquals(0, span.years)
        assertEquals(0, span.months)
        assertEquals(0, span.days)
        assertEquals(0, span.hours)
        assertEquals(0, span.minutes)
        assertEquals(0, span.seconds)
    }

    @Test
    fun `contains with date inside range`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")
        val range = DateTimeRange.builder().from(start).to(end).build()
        val dateInside = DateTime.fromString("2023-01-15")

        /**WHEN**/
        val result = range.contains(dateInside)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `contains with date on start boundary`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")
        val range = DateTimeRange.builder().from(start).to(end).build()

        /**WHEN**/
        val result = range.contains(start)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `contains with date on end boundary`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")
        val range = DateTimeRange.builder().from(start).to(end).build()

        /**WHEN**/
        val result = range.contains(end)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `contains with date before range`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")
        val range = DateTimeRange.builder().from(start).to(end).build()
        val dateBefore = DateTime.fromString("2022-12-31")

        /**WHEN**/
        val result = range.contains(dateBefore)

        /**THEN**/
        assertFalse(result)
    }

    @Test
    fun `contains with date after range`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")
        val range = DateTimeRange.builder().from(start).to(end).build()
        val dateAfter = DateTime.fromString("2023-02-01")

        /**WHEN**/
        val result = range.contains(dateAfter)

        /**THEN**/
        assertFalse(result)
    }

    @Test
    fun `contains with different timezones`() {
        /**GIVEN**/
        val startUTC = DateTime.fromMillis(1672531200000L, "UTC") // 2023-01-01 00:00:00 UTC
        val endUTC = DateTime.fromMillis(1675123200000L, "UTC")   // 2023-01-31 00:00:00 UTC
        val range = DateTimeRange.builder().from(startUTC).to(endUTC).build()
        val dateInEST = DateTime.fromMillis(1673758800000L, "America/New_York") // 2023-01-15 00:00:00 EST which is inside the range

        /**WHEN**/
        val result = range.contains(dateInEST)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `overlaps with fully contained range`() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-01")).to(DateTime.fromString("2023-01-31")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-10")).to(DateTime.fromString("2023-01-20")).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `overlaps with partially overlapping range at the end`() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-10")).to(DateTime.fromString("2023-01-20")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-15")).to(DateTime.fromString("2023-01-25")).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `overlaps with partially overlapping range at the start`() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-10")).to(DateTime.fromString("2023-01-20")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-05")).to(DateTime.fromString("2023-01-15")).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `overlaps with encompassing range`() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-10")).to(DateTime.fromString("2023-01-20")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-01")).to(DateTime.fromString("2023-01-31")).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `overlaps with adjacent range touching at end`() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-01")).to(DateTime.fromString("2023-01-10")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-10")).to(DateTime.fromString("2023-01-20")).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `overlaps with adjacent range touching at start`() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-10")).to(DateTime.fromString("2023-01-20")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-01")).to(DateTime.fromString("2023-01-10")).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `overlaps with non overlapping range after`() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-01")).to(DateTime.fromString("2023-01-10")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-11")).to(DateTime.fromString("2023-01-20")).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertFalse(result)
    }

    @Test
    fun `overlaps with non overlapping range before`() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-11")).to(DateTime.fromString("2023-01-20")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-01")).to(DateTime.fromString("2023-01-10")).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertFalse(result)
    }

    @Test
    fun `overlaps with identical range`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-10")
        val range1 = DateTimeRange.builder().from(start).to(end).build()
        val range2 = DateTimeRange.builder().from(start).to(end).build()

        /**WHEN**/
        val result = range1.overlaps(range2)

        /**THEN**/
        assertTrue(result)
    }

    @Test
    fun `toString format verification`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01 10:00:00")
        val end = DateTime.fromString("2023-01-31 20:00:00")
        val range = DateTimeRange.builder().from(start).to(end).build()

        /**WHEN**/
        val result = range.toString()

        /**THEN**/
        assertTrue(result.startsWith("["))
        assertTrue(result.endsWith("]"))
        assertTrue(result.contains(" - "))
    }

    @Test
    fun `getStart returns correct value`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")
        val range = DateTimeRange.builder().from(start).to(end).build()

        /**WHEN**/
        val result = range.start

        /**THEN**/
        assertEquals(start.toMillis(), result.toMillis())
    }

    @Test
    fun `getEnd returns correct value`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")
        val range = DateTimeRange.builder().from(start).to(end).build()

        /**WHEN**/
        val result = range.end

        /**THEN**/
        assertEquals(end.toMillis(), result.toMillis())
    }

    @Test
    fun `Builder build with from and to`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")

        /**WHEN**/
        val range = DateTimeRange.builder().from(start).to(end).build()

        /**THEN**/
        assertEquals(start.toMillis(), range.start.toMillis())
        assertEquals(end.toMillis(), range.end.toMillis())
    }

    @Test
    fun `Constructor illegal argument check`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-31")
        val end = DateTime.fromString("2023-01-01")

        /**WHEN/THEN**/
        assertThrows(IllegalArgumentException::class.java) {
            DateTimeRange.builder().from(start).to(end).build()
        }
    }

    @Test
    fun `Constructor with equal start and end dates`() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")

        /**WHEN**/
        val range = DateTimeRange.builder().from(start).to(start).build()

        /**THEN**/
        assertNotNull(range)
        assertEquals(range.start.toMillis(), range.end.toMillis())
    }

    @Test
    fun `Edge case with leap year dates`() {
        /**GIVEN**/
        val start = DateTime.fromString("2024-02-28")
        val end = DateTime.fromString("2024-03-01")
        val range = DateTimeRange.builder().from(start).to(end).build()
        val leapDay = DateTime.fromString("2024-02-29")

        /**WHEN**/
        val containsLeapDay = range.contains(leapDay)
        val span = range.span()

        /**THEN**/
        assertTrue(containsLeapDay)
        assertEquals(2, span.days) // 28 a 29 (1), 29 a 1 (1) = 2
    }

}