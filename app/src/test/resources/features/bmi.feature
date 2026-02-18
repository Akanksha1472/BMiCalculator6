Feature: BMI Category Calculation

  Scenario: Underweight BMI
    Given BMI value is 17
    When BMI result is calculated
    Then BMI category should be "Underweight"

  Scenario: Normal BMI
    Given BMI value is 22
    When BMI result is calculated
    Then BMI category should be "Normal"

  Scenario: Overweight BMI
    Given BMI value is 27
    When BMI result is calculated
    Then BMI category should be "Overweight"

  Scenario: Obese BMI
    Given BMI value is 32
    When BMI result is calculated
    Then BMI category should be "Obese"
