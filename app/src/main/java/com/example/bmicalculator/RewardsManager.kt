package com.example.bmicalculator

import java.util.UUID

/**
 * Core rewards business-logic module.
 *
 * Sequence diagram flow implemented here:
 *  1. Kid completes a BMI task  →  caller invokes [onTaskCompleted].
 *  2. [calculateReward] determines points and title from predefined rules.
 *  3. The reward is persisted via [repository] (Firebase Firestore / local).
 *  4. [NotificationHelper.sendRewardNotification] is called on success so the
 *     kid is notified immediately.
 *  5. The optional [onParentNotify] callback lets the caller (e.g. an Activity)
 *     trigger a parent-facing notification / UI update.
 *  6. Error handling: [onError] is invoked when the repository is unreachable
 *     (mirrors the Firebase-unreachable error path in the sequence diagram).
 */
class RewardsManager(
    private val repository: RewardsRepository,
    private val notificationHelper: NotificationHelper
) {

    /**
     * Called when the kid completes the BMI calculation task.
     *
     * @param bmiCategory  e.g. "Normal", "Underweight", …
     * @param onSuccess    invoked with the created [Reward] after it is saved
     * @param onError      invoked with the exception if save fails
     * @param onParentNotify optional callback for parent-facing notification
     */
    fun onTaskCompleted(
        bmiCategory: String,
        onSuccess: (Reward) -> Unit = {},
        onError: (Exception) -> Unit = {},
        onParentNotify: ((Reward) -> Unit)? = null
    ) {
        val reward = calculateReward(bmiCategory)

        repository.saveReward(
            reward = reward,
            onSuccess = {
                // Notify the kid about their new reward
                notificationHelper.sendRewardNotification(reward)
                onSuccess(reward)
                // Optionally notify parent
                onParentNotify?.invoke(reward)
            },
            onError = { e ->
                onError(e)
            }
        )
    }

    /**
     * Retrieves all stored rewards for the rewards-history screen.
     */
    fun getRewardsHistory(
        onSuccess: (List<Reward>) -> Unit,
        onError: (Exception) -> Unit = {}
    ) {
        repository.getRewards(onSuccess, onError)
    }

    /**
     * Pure function: maps a BMI category to a [Reward] using predefined rules.
     * Extracted so it can be tested independently without any I/O.
     */
    internal fun calculateReward(bmiCategory: String): Reward {
        val (title, description, points) = when (bmiCategory) {
            "Normal" -> Triple(
                "Healthy Hero 🌟",
                "Great job! Your BMI is in the healthy range. Keep it up!",
                100
            )
            "Underweight" -> Triple(
                "Growing Star ⭐",
                "You completed your health check! Talk to a doctor about your nutrition.",
                50
            )
            "Overweight" -> Triple(
                "Wellness Warrior 💪",
                "You completed your health check! Small steps lead to big changes.",
                50
            )
            "Obese" -> Triple(
                "Brave Beginner 🎯",
                "You took the first step towards a healthier life. Keep going!",
                50
            )
            else -> Triple(
                "Health Tracker 🏅",
                "You completed a health check. Well done!",
                25
            )
        }

        return Reward(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            points = points,
            bmiCategory = bmiCategory,
            timestamp = System.currentTimeMillis()
        )
    }
}
