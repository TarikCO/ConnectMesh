package com.connectmesh.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.connectmesh.domain.model.AuthUser
import com.connectmesh.domain.repository.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(val user: AuthUser? = null, val loading: Boolean = true, val error: String? = null)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    val state: StateFlow<AuthUiState> = repository.currentUser
        .map { AuthUiState(user = it, loading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AuthUiState())

    fun submit(email: String, password: String, register: Boolean) = viewModelScope.launch {
        val result = if (register) repository.register(email, password) else repository.signIn(email, password)
        result.exceptionOrNull()?.let { /* Local repository emits errors synchronously; UI validates too. */ }
    }
    fun signOut() = viewModelScope.launch { repository.signOut() }

    companion object { fun factory(repository: AuthRepository) = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST") override fun <T : ViewModel> create(modelClass: Class<T>) = AuthViewModel(repository) as T
    } }
}
