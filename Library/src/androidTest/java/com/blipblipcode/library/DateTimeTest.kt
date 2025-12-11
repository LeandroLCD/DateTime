package com.blipblipcode.library

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.blipblipcode.library.model.FormatType
import com.blipblipcode.library.throwable.InvalidFormatException
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test


class DateTimeAndroidTest {
    private val applicationContext =  getInstrumentation().targetContext

    @Before
    fun setUp() {
        DateTime.init(applicationContext)
    }

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

}