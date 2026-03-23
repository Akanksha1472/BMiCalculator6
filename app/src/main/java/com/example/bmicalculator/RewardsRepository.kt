package com.example.bmicalculator

/**
 * Abstraction layer for reward persistence.
 *
 * Sequence diagram role: decouples the RewardsManager from a concrete
 * backend so that a Firebase Firestore (or Realtime Database) implementation
 * can be swapped in without changing the business logic.
 *
 * Production swap-in:
 *   Replace LocalRewardsRepository with a FirebaseRewardsRepository that calls
 *   FirebaseFirestore.getInstance().collection("rewards").add(reward) on save
 *   and .get() on load, then propagates the results through the same callbacks.
 */
interface RewardsRepository {
    /** Persist a reward. [onSuccess] is called when saved; [onError] on failure. */
    fun saveReward(reward: Reward, onSuccess: () -> Unit, onError: (Exception) -> Unit)

    /** Fetch all stored rewards for the current user, ordered newest-first. */
    fun getRewards(onSuccess: (List<Reward>) -> Unit, onError: (Exception) -> Unit)
}
