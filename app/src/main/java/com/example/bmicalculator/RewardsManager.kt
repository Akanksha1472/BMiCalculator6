package com.example.bmicalculator

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.UUID

class RewardsManager(private val context: Context) {

    companion object {
        private const val TAG = "RewardsManager"
        private const val COLLECTION_REWARDS = "rewards"

        /**
         * Pure function: calculates the reward for a given BMI category and value.
         * Kept in companion object so it can be unit-tested without Android/Firebase dependencies.
         */
        fun calculateRewardForCategory(bmiCategory: String, bmiValue: Double): Reward {
            return when (bmiCategory) {
                "Normal" -> Reward(
                    id = UUID.randomUUID().toString(),
                    title = "Healthy Living Star",
                    description = "Congratulations! Your BMI is in the normal range. Keep up the great work!",
                    badge = "\u2B50",
                    points = 100,
                    bmiCategory = bmiCategory,
                    bmiValue = bmiValue
                )
                "Underweight" -> Reward(
                    id = UUID.randomUUID().toString(),
                    title = "Journey to Health",
                    description = "You're on your way! Keep following a nutritious diet to reach a healthy weight.",
                    badge = "\uD83C\uDF31",
                    points = 50,
                    bmiCategory = bmiCategory,
                    bmiValue = bmiValue
                )
                "Overweight" -> Reward(
                    id = UUID.randomUUID().toString(),
                    title = "Fitness Challenger",
                    description = "You've taken the first step! Keep tracking your progress and stay active.",
                    badge = "\uD83D\uDCAA",
                    points = 50,
                    bmiCategory = bmiCategory,
                    bmiValue = bmiValue
                )
                "Obese" -> Reward(
                    id = UUID.randomUUID().toString(),
                    title = "Wellness Warrior",
                    description = "Every journey starts with a single step. You're here, and that matters!",
                    badge = "\uD83C\uDFC6",
                    points = 50,
                    bmiCategory = bmiCategory,
                    bmiValue = bmiValue
                )
                else -> Reward(
                    id = UUID.randomUUID().toString(),
                    title = "BMI Tracker",
                    description = "Thank you for tracking your BMI!",
                    badge = "\uD83D\uDCCA",
                    points = 25,
                    bmiCategory = bmiCategory,
                    bmiValue = bmiValue
                )
            }
        }
    }

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val crashlytics: FirebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }

    private val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            ?: UUID.randomUUID().toString()
    }

    /**
     * Persists [reward] to Firestore under the device's rewards collection.
     * Calls [onError] and logs to Crashlytics if Firebase is unreachable.
     */
    fun saveReward(reward: Reward, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        val rewardData = hashMapOf(
            "id" to reward.id,
            "title" to reward.title,
            "description" to reward.description,
            "badge" to reward.badge,
            "points" to reward.points,
            "bmiCategory" to reward.bmiCategory,
            "bmiValue" to reward.bmiValue,
            "timestamp" to reward.timestamp
        )

        firestore
            .collection(COLLECTION_REWARDS)
            .document(deviceId)
            .collection("entries")
            .document(reward.id)
            .set(rewardData)
            .addOnSuccessListener {
                Log.d(TAG, "Reward saved successfully: ${reward.title}")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to save reward", exception)
                crashlytics.recordException(exception)
                onError(exception)
            }
    }

    /**
     * Fetches the full rewards history for the current device from Firestore, newest first.
     * Calls [onError] and logs to Crashlytics if Firebase is unreachable.
     */
    fun getRewardsHistory(onSuccess: (List<Reward>) -> Unit, onError: (Exception) -> Unit) {
        firestore
            .collection(COLLECTION_REWARDS)
            .document(deviceId)
            .collection("entries")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val rewards = documents.map { doc ->
                    Reward(
                        id = doc.getString("id") ?: "",
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        badge = doc.getString("badge") ?: "",
                        points = (doc.getLong("points") ?: 0).toInt(),
                        bmiCategory = doc.getString("bmiCategory") ?: "",
                        bmiValue = doc.getDouble("bmiValue") ?: 0.0,
                        timestamp = doc.getLong("timestamp") ?: 0L
                    )
                }
                Log.d(TAG, "Retrieved ${rewards.size} rewards")
                onSuccess(rewards)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Failed to retrieve rewards history", exception)
                crashlytics.recordException(exception)
                onError(exception)
            }
    }
}
