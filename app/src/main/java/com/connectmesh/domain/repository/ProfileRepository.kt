package com.connectmesh.domain.repository

import com.connectmesh.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

/** Replace LocalProfileRepository with a Firestore-backed implementation later. */
interface ProfileRepository {
    fun observeProfile(userId: String): Flow<UserProfile?>
    suspend fun saveProfile(profile: UserProfile)
}
