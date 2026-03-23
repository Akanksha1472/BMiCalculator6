package com.example.bmicalculator

/**
 * Data model representing a single reward earned by the user.
 *
 * Sequence diagram role: persisted to Firebase (Firestore/Realtime Database)
 * and retrieved to display the rewards history.
 */
data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val points: Int,
    val bmiCategory: String,
    val timestamp: Long
)
