package com.blipblipcode.library

import com.blipblipcode.library.model.FormatType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName

/**
 * Test unitario para DateTime usando java.time nativo
 * Ya no requiere inicialización con AndroidThreeTen.init(context)
 * Ahora es más fácil de testear sin necesidad de contexto de Android
 */
class DateTimeJavaTimeTest {

    @Test
    @DisplayName("Crear DateTime sin contexto - ahora() funciona sin inicialización")
    fun `should create DateTime now without context`() {
        // GIVEN - No se requiere inicialización

        // WHEN
        val now = DateTime.now()

        // THEN
        assertNotNull(now)
        assertTrue(now.year >= 2024)
        assertTrue(now.month in 1..12)
        assertTrue(now.day in 1..31)
    }

    @Test
    @DisplayName("Crear DateTime desde millis sin contexto")
    fun `should create DateTime from millis without context`() {
        // GIVEN
        val millis = 1609459200000L // 2021-01-01 00:00:00 UTC

        // WHEN
        val dateTime = DateTime.fromMillis(millis)

        // THEN
        assertNotNull(dateTime)
        assertEquals(2021, dateTime.year)
        assertEquals(1, dateTime.month)
        assertEquals(1, dateTime.day)
    }

    @Test
    @DisplayName("Crear DateTime desde string sin contexto")
    fun `should create DateTime from string without context`() {
        // GIVEN
        val dateString = "25-12-2023"

        // WHEN
        val dateTime = DateTime.fromString(dateString)

        // THEN
        assertEquals(2023, dateTime.year)
        assertEquals(12, dateTime.month)
        assertEquals(25, dateTime.day)
    }

    @Test
    @DisplayName("Formatear DateTime sin contexto")
    fun `should format DateTime without context`() {
        // GIVEN
        val dateTime = DateTime.fromString("2023-12-25")

        // WHEN
        val formatted = dateTime.format(FormatType.Short('-'))

        // THEN
        assertEquals("25-12-2023", formatted)
    }

    @Test
    @DisplayName("Agregar días sin contexto")
    fun `should add days without context`() {
        // GIVEN
        val dateTime = DateTime.fromString("2023-12-25")

        // WHEN
        val newDateTime = dateTime.addDays(7)

        // THEN
        assertEquals(2024, newDateTime.year)
        assertEquals(1, newDateTime.month)
        assertEquals(1, newDateTime.day)
    }

    @Test
    @DisplayName("Convertir a millis sin contexto")
    fun `should convert to millis without context`() {
        // GIVEN
        val dateTime = DateTime.fromString("2023-01-01")

        // WHEN
        val millis = dateTime.toMillis()

        // THEN
        assertTrue(millis > 0)
    }

    @Test
    @DisplayName("Builder sin contexto")
    fun `should build DateTime without context`() {
        // WHEN
        val dateTime = DateTime.Builder()
            .setYear(2023)
            .setMonth(12)
            .setDay(25)
            .build()

        // THEN
        assertEquals(2023, dateTime.year)
        assertEquals(12, dateTime.month)
        assertEquals(25, dateTime.day)
    }

    @Test
    @DisplayName("Primer día del mes sin contexto")
    fun `should get first day of month without context`() {
        // GIVEN
        val dateTime = DateTime.fromString("2023-12-25")

        // WHEN
        val firstDay = dateTime.firstDayOfMonth()

        // THEN
        assertEquals(1, firstDay.day)
        assertEquals(12, firstDay.month)
        assertEquals(2023, firstDay.year)
    }

    @Test
    @DisplayName("Último día del mes sin contexto")
    fun `should get last day of month without context`() {
        // GIVEN
        val dateTime = DateTime.fromString("2023-12-15")

        // WHEN
        val lastDay = dateTime.lastDayOfMonth()

        // THEN
        assertEquals(31, lastDay.day)
        assertEquals(12, lastDay.month)
        assertEquals(2023, lastDay.year)
    }
}

