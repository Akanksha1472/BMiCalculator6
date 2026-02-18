package com.example.bmicalculator

import org.junit.Test
import org.junit.Assert.assertEquals
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class MockitoSimpleTest {

    @Test
    fun testMockedCategory() {
        val bmiService = mock<BmiService>()

        whenever(bmiService.getCategory(22.0)).thenReturn("Normal")

        val result = bmiService.getCategory(22.0)

        assertEquals("Normal", result)
    }
}
