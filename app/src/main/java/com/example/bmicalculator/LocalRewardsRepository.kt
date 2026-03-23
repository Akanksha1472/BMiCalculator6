package com.example.bmicalculator

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local implementation of [RewardsRepository] backed by SharedPreferences.
 *
 * Sequence diagram role: acts as the persistence layer when Firebase is
 * unreachable or not yet configured (error-handling path).  In a production
 * build this class would be replaced by (or supplemented with) a Firebase
 * Firestore / Realtime Database implementation that mirrors the same
 * saveReward / getRewards contract.
 *
 * Firebase swap-in sketch (production):
 * ```
 * val db = FirebaseFirestore.getInstance()
 * db.collection("rewards")
 *   .add(reward.toMap())
 *   .addOnSuccessListener { onSuccess() }
 *   .addOnFailureListener { e -> onError(e) }
 * ```
 */
class LocalRewardsRepository(context: Context) : RewardsRepository {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    override fun saveReward(
        reward: Reward,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val existing = loadRawList()
            val obj = JSONObject().apply {
                put(KEY_ID, reward.id)
                put(KEY_TITLE, reward.title)
                put(KEY_DESCRIPTION, reward.description)
                put(KEY_POINTS, reward.points)
                put(KEY_BMI_CATEGORY, reward.bmiCategory)
                put(KEY_TIMESTAMP, reward.timestamp)
            }
            existing.put(obj)
            prefs.edit().putString(KEY_REWARDS, existing.toString()).commit()
            onSuccess()
        } catch (e: Exception) {
            onError(e)
        }
    }

    override fun getRewards(
        onSuccess: (List<Reward>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            val array = loadRawList()
            val rewards = mutableListOf<Reward>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                rewards.add(
                    Reward(
                        id = obj.getString(KEY_ID),
                        title = obj.getString(KEY_TITLE),
                        description = obj.getString(KEY_DESCRIPTION),
                        points = obj.getInt(KEY_POINTS),
                        bmiCategory = obj.getString(KEY_BMI_CATEGORY),
                        timestamp = obj.getLong(KEY_TIMESTAMP)
                    )
                )
            }
            // newest first
            onSuccess(rewards.sortedByDescending { it.timestamp })
        } catch (e: Exception) {
            onError(e)
        }
    }

    private fun loadRawList(): JSONArray {
        val raw = prefs.getString(KEY_REWARDS, null) ?: return JSONArray()
        return JSONArray(raw)
    }

    companion object {
        private const val PREFS_NAME = "rewards_prefs"
        private const val KEY_REWARDS = "rewards"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_POINTS = "points"
        private const val KEY_BMI_CATEGORY = "bmiCategory"
        private const val KEY_TIMESTAMP = "timestamp"
    }
}
