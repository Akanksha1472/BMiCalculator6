package com.example.bmicalculator

data class Reward(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val badge: String = "",
    val points: Int = 0,
    val bmiCategory: String = "",
    val bmiValue: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
