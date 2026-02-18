package com.example.bmicalculator

import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then
import org.junit.Assert.assertEquals

class BmiStepDefinitions {

    private var bmiValue: Int = 0
    private var bmiCategory: String = ""

    @Given("BMI value is {int}")
    fun bmi_value_is(value: Int) {
        bmiValue = value
    }

    @When("BMI result is calculated")
    fun bmi_result_is_calculated() {
        bmiCategory = getBmiResult(bmiValue.toDouble()).first
    }

    @Then("BMI category should be {string}")
    fun bmi_category_should_be(expected: String) {
        assertEquals(expected, bmiCategory)
    }
}
