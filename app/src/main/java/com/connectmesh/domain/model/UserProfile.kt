package com.connectmesh.domain.model

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val headline: String = "",
    val bio: String = "",
    val skills: List<String> = emptyList(),
    val interests: List<String> = emptyList(),
    val lookingFor: List<String> = emptyList(),
)

data class AuthUser(val id: String, val email: String)
