package com.example.bmicalculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RewardsManagerTest {

    @Test
    fun `calculateRewardForCategory returns Healthy Living Star for Normal BMI`() {
        val reward = RewardsManager.calculateRewardForCategory("Normal", 22.0)
        assertEquals("Healthy Living Star", reward.title)
        assertEquals(100, reward.points)
        assertEquals("Normal", reward.bmiCategory)
        assertEquals(22.0, reward.bmiValue, 0.001)
    }

    @Test
    fun `calculateRewardForCategory returns Journey to Health for Underweight BMI`() {
        val reward = RewardsManager.calculateRewardForCategory("Underweight", 16.0)
        assertEquals("Journey to Health", reward.title)
        assertEquals(50, reward.points)
        assertEquals("Underweight", reward.bmiCategory)
    }

    @Test
    fun `calculateRewardForCategory returns Fitness Challenger for Overweight BMI`() {
        val reward = RewardsManager.calculateRewardForCategory("Overweight", 27.5)
        assertEquals("Fitness Challenger", reward.title)
        assertEquals(50, reward.points)
        assertEquals("Overweight", reward.bmiCategory)
    }

    @Test
    fun `calculateRewardForCategory returns Wellness Warrior for Obese BMI`() {
        val reward = RewardsManager.calculateRewardForCategory("Obese", 35.0)
        assertEquals("Wellness Warrior", reward.title)
        assertEquals(50, reward.points)
        assertEquals("Obese", reward.bmiCategory)
    }

    @Test
    fun `calculateRewardForCategory returns BMI Tracker for unknown category`() {
        val reward = RewardsManager.calculateRewardForCategory("Unknown", 0.0)
        assertEquals("BMI Tracker", reward.title)
        assertEquals(25, reward.points)
    }

    @Test
    fun `calculateRewardForCategory reward has non-blank id and badge`() {
        val reward = RewardsManager.calculateRewardForCategory("Normal", 22.0)
        assertTrue(reward.id.isNotBlank())
        assertTrue(reward.badge.isNotBlank())
    }

    @Test
    fun `calculateRewardForCategory reward stores correct bmiValue`() {
        val bmiValue = 24.9
        val reward = RewardsManager.calculateRewardForCategory("Normal", bmiValue)
        assertEquals(bmiValue, reward.bmiValue, 0.001)
    }

    @Test
    fun `calculateRewardForCategory rewards for non-normal categories have 50 points`() {
        listOf("Underweight", "Overweight", "Obese").forEach { category ->
            val reward = RewardsManager.calculateRewardForCategory(category, 20.0)
            assertEquals("Expected 50 points for $category", 50, reward.points)
        }
    }
}
