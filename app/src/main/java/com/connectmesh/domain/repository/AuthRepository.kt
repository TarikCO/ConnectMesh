package com.connectmesh.domain.repository

import com.connectmesh.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

/** FirebaseAuth can implement this contract without changing UI or ViewModels. */
interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    suspend fun signIn(email: String, password: String): Result<AuthUser>
    suspend fun register(email: String, password: String): Result<AuthUser>
    suspend fun signOut()
}
