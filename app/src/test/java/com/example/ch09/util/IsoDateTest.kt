package com.example.ch09.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IsoDateTest {
    // 2025-05-01T08:00:00Z dalam epoch millis
    private val expected = 1_746_086_400_000L

    @Test fun `format NewsAPI`() = assertEquals(expected, parseIsoInstant("2025-05-01T08:00:00Z"))

    @Test fun `dengan pecahan detik`() = assertEquals(expected, parseIsoInstant("2025-05-01T08:00:00.000Z"))

    @Test fun `teks kosong atau rusak`() {
        assertNull(parseIsoInstant(""))
        assertNull(parseIsoInstant("kemarin"))
    }
}
