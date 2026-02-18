package com.example.bmicalculator

import org.junit.Test
import org.junit.Assert.assertEquals

class BmiCalculatorTest {

    // ✅ Test BMI calculation formula
    @Test
    fun testBmiCalculation() {
        val heightCm = 170
        val weightKg = 65

        val bmi = weightKg / ((heightCm / 100.0) * (heightCm / 100.0))

        assertEquals(22.49, bmi, 0.01)
    }

    // ✅ Underweight category test
    @Test
    fun testUnderweightCategory() {
        val result = getBmiResult(17.0)
        assertEquals("Underweight", result.first)
    }

    // ✅ Normal category test
    @Test
    fun testNormalCategory() {
        val result = getBmiResult(22.0)
        assertEquals("Normal", result.first)
    }

    // ✅ Overweight category test
    @Test
    fun testOverweightCategory() {
        val result = getBmiResult(27.0)
        assertEquals("Overweight", result.first)
    }

    // ✅ Obese category test
    @Test
    fun testObeseCategory() {
        val result = getBmiResult(32.0)
        assertEquals("Obese", result.first)
    }
}
