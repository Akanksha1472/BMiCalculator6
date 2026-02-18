package com.example.bmicalculator

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
    // Unit test for BMI calculation logic
    @Test
    fun testBmiCalculation() {
        val heightCm = 170
        val weightKg = 65
        val expectedBmi = 22.49 // BMI = weight (kg) / (height (m))^2
        val calculatedBmi = weightKg / ((heightCm / 100.0) * (heightCm / 100.0))
        assertEquals(expectedBmi, calculatedBmi, 0.01)
    }

    //Unit test for Underweight category
    @Test
    fun testUnderweightCategory() {
        val bmi = 17.5
        val (category, advice) = getBmiResult(bmi)
        assertEquals("Underweight", category)
        assertEquals(
            "Diet and Nutrition\n" +
                    "\n" +
                    "Eat More Calories:\n" +
                    "• Consume high-calorie foods like nuts, avocados, and healthy oils.\n" +
                    "• Increase protein sizes during meals.\n" +
                    "\n" +
                    "Choose Nutrient-Rich Foods:\n" +
                    "• Focus on foods rich in protein (lean meats, fish, eggs, legumes).\n" +
                    "• Include complex carbohydrates (whole grains, sweet potatoes).\n" +
                    "\n" +
                    "Eat plenty of fruits and vegetables for overall health.",
            advice
        )
    }

    //Unit test for Normal weight category
    @Test
    fun testNormalWeightCategory() {
        val bmi = 22.0
        val (category, advice) = getBmiResult(bmi)
        assertEquals("Normal", category)
        assertEquals(
            "Diet and Nutrition\n" +
                    "\n" +
                    "Maintain a Balanced Diet:\n" +
                    "• Eat a variety of fruits, vegetables, whole grains, and lean proteins.\n" +
                    "• Maintain regular meal timings.\n" +
                    "\n" +
                    "Stay Hydrated:\n" +
                    "• Drink plenty of water throughout the day.\n" +
                    "\n" +
                    "Continue regular physical activity to stay healthy.",
            advice
        )
    }

    //Unit test for Overweight category
    @Test
    fun testOverweightCategory() {
        val bmi = 27.0
        val (category, advice) = getBmiResult(bmi)
        assertEquals("Overweight", category)
        assertEquals(
            "Diet and Nutrition\n" +
                    "\n" +
                    "Control Portion Sizes:\n" +
                    "• Reduce high-calorie and sugary foods.\n" +
                    "• Focus on portion control.\n" +
                    "\n" +
                    "Increase Fiber Intake:\n" +
                    "• Eat more fruits, vegetables, and whole grains.\n" +
                    "\n" +
                    "Engage in regular physical activity to manage weight.",
            advice
        )
    }

    //Unit test for Obesity category
    @Test
    fun testObesityCategory() {
        val bmi = 32.0
        val (category, advice) = getBmiResult(bmi)
        assertEquals("Obese", category)
        assertEquals(
            "Diet and Nutrition\n" +
                    "\n" +
                    "Adopt a Low-Calorie Diet:\n" +
                    "• Avoid processed foods and sugary drinks.\n" +
                    "• Choose lean proteins and vegetables.\n" +
                    "\n" +
                    "Increase Physical Activity:\n" +
                    "• Include daily exercise like walking or cycling.\n" +
                    "\n" +
                    "Consult a healthcare professional for personalized guidance.",
            advice
        )
    }

    //
}