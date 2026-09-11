package com.connectmesh.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.connectmesh.domain.model.UserProfile
import com.connectmesh.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val userId: String, private val repository: ProfileRepository) : ViewModel() {
    val profile: StateFlow<UserProfile?> = repository.observeProfile(userId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    fun save(profile: UserProfile) = viewModelScope.launch { repository.saveProfile(profile.copy(id = userId)) }
    companion object { fun factory(userId: String, repository: ProfileRepository) = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>) = ProfileViewModel(userId, repository) as T
    } }
}
