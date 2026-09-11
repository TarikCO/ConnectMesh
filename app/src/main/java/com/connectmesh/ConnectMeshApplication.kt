package com.connectmesh

import android.app.Application
import com.connectmesh.data.local.LocalAuthRepository
import com.connectmesh.data.local.LocalProfileRepository
import com.connectmesh.domain.repository.AuthRepository
import com.connectmesh.domain.repository.ProfileRepository

/** Central dependency container. Replace the local implementations here after Firebase is wired. */
class ConnectMeshApplication : Application() {
    lateinit var authRepository: AuthRepository
        private set
    lateinit var profileRepository: ProfileRepository
        private set

    override fun onCreate() {
        super.onCreate()
        authRepository = LocalAuthRepository(this)
        profileRepository = LocalProfileRepository(this)
    }
}
