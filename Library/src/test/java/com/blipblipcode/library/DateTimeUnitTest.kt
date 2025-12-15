package com.blipblipcode.library

import com.blipblipcode.library.model.DateTimeRange
import com.blipblipcode.library.model.FormatType
import com.blipblipcode.library.throwable.InvalidFormatException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test


class DateTimeUnitTest {

    @Test
    fun shouldCreateDateTimeFromValidStringFormatYyyyMmDd() {
        /**GIVEN**/
        val dateString = "2023-12-25"

        /**WHEN**/
        val dateTime = DateTime.fromString(dateString)

        /**THEN**/
        assertEquals(2023, dateTime.year)
        assertEquals(12, dateTime.month)
        assertEquals(25, dateTime.day)
    }

    @Test
    fun shouldCreateDateTimeFromValidStringFormatDdMmYyyy() {
        /**GIVEN**/
        val dateString = "25-12-2023"

        /**WHEN**/
        val dateTime = DateTime.fromString(dateString)

        /**THEN**/
        assertEquals(2023, dateTime.year)
        assertEquals(12, dateTime.month)
        assertEquals(25, dateTime.day)
    }
    @Test
    fun shouldCreateDateTimeFromValidStringFormatDdMmYy() {
        /**GIVEN**/
        val dateString = "25-12-23"

        /**WHEN**/
        val dateTime = DateTime.fromString(dateString)

        /**THEN**/
        assertEquals(2023, dateTime.year)
        assertEquals(12, dateTime.month)
        assertEquals(25, dateTime.day)
    }

    @Test
    fun shouldThrowInvalidFormatExceptionForInvalidDateString() {
        /**GIVEN**/
        val invalidDate = "invalid-date"

        /**WHEN**/ /**THEN**/
        assertThrows(InvalidFormatException::class.java) {
            DateTime.fromString(invalidDate)
        }
    }

    @Test
    fun shouldAddDaysCorrectly() {
        /**GIVEN**/
        val date = DateTime.fromString("2023-01-01")

        /**WHEN**/
        val result = date.addDays(10)

        /**THEN**/
        assertEquals(11, result.day)
        assertEquals(1, result.month)
        assertEquals(2023, result.year)
    }

    @Test
    fun shouldAddMonthsCorrectly() {
        /**GIVEN**/
        val date = DateTime.fromString("2023-01-15")

        /**WHEN**/
        val result = date.addMonths(2)

        /**THEN**/
        assertEquals(15, result.day)
        assertEquals(3, result.month)
        assertEquals(2023, result.year)
    }

    @Test
    fun shouldCalculateFirstAndLastDayOfMonth() {
        /**GIVEN**/
        val date = DateTime.fromString("2023-02-15")

        /**WHEN**/
        val firstDay = date.firstDayOfMonth()
        val lastDay = date.lastDayOfMonth()

        /**THEN**/
        assertEquals(1, firstDay.day)
        assertEquals(28, lastDay.day) // 2023 no es año bisiesto
    }

    @Test
    fun shouldDetectLeapYearFebruary() {
        /**GIVEN**/
        val leapYearDate = DateTime.fromString("2024-02-10")

        /**WHEN**/
        val lastDayOfMonth = leapYearDate.lastDayOfMonth()

        /**THEN**/
        assertEquals(29, lastDayOfMonth.day) // 2024 sí es bisiesto
    }

    @Test
    fun shouldConvertToMillisAndBack() {
        /**GIVEN**/
        val date = DateTime.fromString("2023-10-04")

        /**WHEN**/
        val millis = date.toMillis()
        val fromMillis = DateTime.fromMillis(millis)

        /**THEN**/
        assertEquals(date.year, fromMillis.year)
        assertEquals(date.month, fromMillis.month)
        assertEquals(date.day, fromMillis.day)
    }

    @Test
    fun shouldFormatUsingFormatTypeLarge() {
        /**GIVEN**/
        val date = DateTime.fromString("2023-10-04")

        /**WHEN**/
        val formatted = date.format(FormatType.Large(delimiter = '-'))

        /**THEN**/
        assert(formatted.contains("2023"))
        assert(formatted.contains("04-10"))
    }

    @Test
    fun shouldCalculateTimespanBetweenTwoDates() {
        /**GIVEN**/
        val start = DateTime.fromString("2020-01-01")
        val end = DateTime.fromString("2023-01-01")

        /**WHEN**/
        val diff = end.timeSpan(start)

        /**THEN**/
        assertEquals(3, diff.years)
        assertEquals(0, diff.months)
        assertEquals(0, diff.days)
    }

    @Test
    fun shouldBuildDateWithBuilder() {
        /**GIVEN**/
        val builder = DateTime.Builder()

        /**WHEN**/
        val date = builder.setYear(2025).setMonth(12).setDay(31).build()

        /**THEN**/
        assertEquals(2025, date.year)
        assertEquals(12, date.month)
        assertEquals(31, date.day)
    }

    @Test
    fun shouldCalculateSpanBetweenStartAndEnd() {
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
    fun shouldCheckIfDateIsContainedInRange() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-01")
        val end = DateTime.fromString("2023-01-31")
        val range = DateTimeRange.builder().from(start).to(end).build()
        val dateInside = DateTime.fromString("2023-01-15")
        val dateOutside = DateTime.fromString("2023-02-01")

        /**WHEN**/
        val isInside = range.contains(dateInside)
        val isOutside = range.contains(dateOutside)

        /**THEN**/
        assertEquals(true, isInside)
        assertEquals(false, isOutside)
    }

    @Test
    fun shouldCheckIfRangesOverlap() {
        /**GIVEN**/
        val range1 = DateTimeRange.builder().from(DateTime.fromString("2023-01-01")).to(DateTime.fromString("2023-01-15")).build()
        val range2 = DateTimeRange.builder().from(DateTime.fromString("2023-01-10")).to(DateTime.fromString("2023-01-20")).build()
        val range3 = DateTimeRange.builder().from(DateTime.fromString("2023-02-01")).to(DateTime.fromString("2023-02-10")).build()

        /**WHEN**/
        val overlaps1 = range1.overlaps(range2)
        val overlaps2 = range1.overlaps(range3)

        /**THEN**/
        assertEquals(true, overlaps1)
        assertEquals(false, overlaps2)
    }

    @Test
    fun shouldBuildRangeWithBuilder() {
        /**GIVEN**/
        val start = DateTime.fromString("2025-01-01")
        val end = DateTime.fromString("2025-01-31")

        /**WHEN**/
        val range = DateTimeRange.builder().from(start).to(end).build()

        /**THEN**/
        assertEquals(start.toString(), range.start.toString())
        assertEquals(end.toString(), range.end.toString())
    }

    @Test
    fun shouldThrowExceptionForInvalidRange() {
        /**GIVEN**/
        val start = DateTime.fromString("2023-01-31")
        val end = DateTime.fromString("2023-01-01")

        /**WHEN**/ /**THEN**/
        assertThrows(IllegalArgumentException::class.java) {
            DateTimeRange.builder().from(start).to(end).build()
        }
    }

}
