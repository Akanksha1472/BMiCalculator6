package com.example.bmicalculator

class BmiService {

    fun getCategory(bmi: Double): String {
        return getBmiResult(bmi).first
    }
}
