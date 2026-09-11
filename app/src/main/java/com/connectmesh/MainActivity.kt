package com.connectmesh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.connectmesh.ui.ConnectMeshApp
import com.connectmesh.ui.theme.ConnectMeshTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ConnectMeshTheme { ConnectMeshApp() } }
    }
}
