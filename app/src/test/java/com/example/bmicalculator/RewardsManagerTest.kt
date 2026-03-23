package com.example.bmicalculator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Unit tests for [RewardsManager] covering:
 *  - Reward calculation rules for every BMI category
 *  - Task completion flow (save → notify → callback)
 *  - Error handling when the repository fails
 *  - Rewards history retrieval
 */
class RewardsManagerTest {

    private lateinit var mockRepository: RewardsRepository
    private lateinit var mockNotificationHelper: NotificationHelper
    private lateinit var rewardsManager: RewardsManager

    @Before
    fun setUp() {
        mockRepository = mock()
        mockNotificationHelper = mock()
        rewardsManager = RewardsManager(mockRepository, mockNotificationHelper)
    }

    // ── calculateReward ────────────────────────────────────────────────────────

    @Test
    fun `calculateReward - Normal category awards 100 points`() {
        val reward = rewardsManager.calculateReward("Normal")
        assertEquals(100, reward.points)
        assertEquals("Normal", reward.bmiCategory)
        assertTrue(reward.title.isNotEmpty())
    }

    @Test
    fun `calculateReward - Underweight category awards 50 points`() {
        val reward = rewardsManager.calculateReward("Underweight")
        assertEquals(50, reward.points)
        assertEquals("Underweight", reward.bmiCategory)
    }

    @Test
    fun `calculateReward - Overweight category awards 50 points`() {
        val reward = rewardsManager.calculateReward("Overweight")
        assertEquals(50, reward.points)
        assertEquals("Overweight", reward.bmiCategory)
    }

    @Test
    fun `calculateReward - Obese category awards 50 points`() {
        val reward = rewardsManager.calculateReward("Obese")
        assertEquals(50, reward.points)
        assertEquals("Obese", reward.bmiCategory)
    }

    @Test
    fun `calculateReward - unknown category awards 25 points`() {
        val reward = rewardsManager.calculateReward("Unknown")
        assertEquals(25, reward.points)
        assertEquals("Unknown", reward.bmiCategory)
    }

    @Test
    fun `calculateReward - reward has non-empty id and title`() {
        val reward = rewardsManager.calculateReward("Normal")
        assertTrue(reward.id.isNotEmpty())
        assertTrue(reward.title.isNotEmpty())
        assertTrue(reward.description.isNotEmpty())
        assertTrue(reward.timestamp > 0)
    }

    // ── onTaskCompleted ────────────────────────────────────────────────────────

    @Test
    fun `onTaskCompleted - calls saveReward on repository`() {
        val rewardCaptor = argumentCaptor<Reward>()
        val successCaptor = argumentCaptor<() -> Unit>()

        rewardsManager.onTaskCompleted("Normal")

        verify(mockRepository).saveReward(
            rewardCaptor.capture(),
            successCaptor.capture(),
            any()
        )

        assertEquals("Normal", rewardCaptor.firstValue.bmiCategory)
    }

    @Test
    fun `onTaskCompleted success - invokes notification and onSuccess callback`() {
        var capturedReward: Reward? = null
        val successCaptor = argumentCaptor<() -> Unit>()
        val rewardCaptor = argumentCaptor<Reward>()

        // Arrange: make repository call onSuccess immediately
        org.mockito.kotlin.doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(mockRepository).saveReward(any(), any(), any())

        // Act
        rewardsManager.onTaskCompleted(
            bmiCategory = "Normal",
            onSuccess = { reward -> capturedReward = reward }
        )

        // Assert
        verify(mockNotificationHelper).sendRewardNotification(any())
        assertEquals("Normal", capturedReward?.bmiCategory)
    }

    @Test
    fun `onTaskCompleted failure - invokes onError callback`() {
        val expectedException = RuntimeException("Firebase unreachable")
        var receivedException: Exception? = null

        // Arrange: make repository call onError immediately
        org.mockito.kotlin.doAnswer { invocation ->
            val onError = invocation.getArgument<(Exception) -> Unit>(2)
            onError(expectedException)
            null
        }.`when`(mockRepository).saveReward(any(), any(), any())

        // Act
        rewardsManager.onTaskCompleted(
            bmiCategory = "Normal",
            onError = { e -> receivedException = e }
        )

        // Assert
        assertEquals("Firebase unreachable", receivedException?.message)
    }

    @Test
    fun `onTaskCompleted - parent notification callback is invoked on success`() {
        var parentNotified = false

        org.mockito.kotlin.doAnswer { invocation ->
            val onSuccess = invocation.getArgument<() -> Unit>(1)
            onSuccess()
            null
        }.`when`(mockRepository).saveReward(any(), any(), any())

        rewardsManager.onTaskCompleted(
            bmiCategory = "Normal",
            onParentNotify = { parentNotified = true }
        )

        assertTrue(parentNotified)
    }

    // ── getRewardsHistory ──────────────────────────────────────────────────────

    @Test
    fun `getRewardsHistory - delegates to repository`() {
        val onSuccess = argumentCaptor<(List<Reward>) -> Unit>()
        val onError = argumentCaptor<(Exception) -> Unit>()

        rewardsManager.getRewardsHistory({}, {})

        verify(mockRepository).getRewards(onSuccess.capture(), onError.capture())
    }

    @Test
    fun `getRewardsHistory - returns list from repository`() {
        val fakeReward = Reward("1", "Test", "Desc", 100, "Normal", System.currentTimeMillis())
        var received: List<Reward> = emptyList()

        org.mockito.kotlin.doAnswer { invocation ->
            val onSuccess = invocation.getArgument<(List<Reward>) -> Unit>(0)
            onSuccess(listOf(fakeReward))
            null
        }.`when`(mockRepository).getRewards(any(), any())

        rewardsManager.getRewardsHistory(onSuccess = { received = it })

        assertEquals(1, received.size)
        assertEquals("Test", received[0].title)
    }
}
