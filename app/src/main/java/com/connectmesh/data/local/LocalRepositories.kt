package com.connectmesh.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.connectmesh.domain.model.AuthUser
import com.connectmesh.domain.model.UserProfile
import com.connectmesh.domain.repository.AuthRepository
import com.connectmesh.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.connectMeshStore by preferencesDataStore(name = "connectmesh")
private val userIdKey = stringPreferencesKey("session_user_id")
private val emailKey = stringPreferencesKey("session_email")
private val profilePrefix = "profile_"

class LocalAuthRepository(private val context: Context) : AuthRepository {
    override val currentUser: Flow<AuthUser?> = context.connectMeshStore.data.map { prefs ->
        val id = prefs[userIdKey] ?: return@map null
        AuthUser(id, prefs[emailKey].orEmpty())
    }

    override suspend fun signIn(email: String, password: String): Result<AuthUser> = createSession(email, password)
    override suspend fun register(email: String, password: String): Result<AuthUser> = createSession(email, password)

    private suspend fun createSession(email: String, password: String): Result<AuthUser> {
        if (!email.contains('@')) return Result.failure(IllegalArgumentException("Enter a valid email address."))
        if (password.length < 6) return Result.failure(IllegalArgumentException("Password must be at least 6 characters."))
        val user = AuthUser(UUID.nameUUIDFromBytes(email.lowercase().toByteArray()).toString(), email.trim())
        context.connectMeshStore.edit { it[userIdKey] = user.id; it[emailKey] = user.email }
        return Result.success(user)
    }

    override suspend fun signOut() { context.connectMeshStore.edit { it.remove(userIdKey); it.remove(emailKey) } }
}

class LocalProfileRepository(private val context: Context) : ProfileRepository {
    override fun observeProfile(userId: String): Flow<UserProfile?> = context.connectMeshStore.data.map { prefs ->
        prefs[stringPreferencesKey("$profilePrefix$userId")]?.let(::decode)
    }
    override suspend fun saveProfile(profile: UserProfile) {
        context.connectMeshStore.edit { it[stringPreferencesKey("$profilePrefix${profile.id}")] = encode(profile) }
    }
    private fun encode(profile: UserProfile) = listOf(profile.id, profile.name, profile.headline, profile.bio, profile.skills.joinToString("|"), profile.interests.joinToString("|"), profile.lookingFor.joinToString("|")).joinToString("\u001F") { it.replace("\u001F", "") }
    private fun decode(raw: String): UserProfile? {
        val p = raw.split("\u001F")
        if (p.size != 7) return null
        fun tags(value: String) = value.split("|").filter(String::isNotBlank)
        return UserProfile(p[0], p[1], p[2], p[3], tags(p[4]), tags(p[5]), tags(p[6]))
    }
}
