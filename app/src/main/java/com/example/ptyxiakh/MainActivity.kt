package com.example.ptyxiakh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ptyxiakh.ui.NavigationScreen
import com.example.ptyxiakh.ui.theme.PtyxiakhTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectAll() // Detect all thread-related issues
                    .penaltyLog() // Log violations to Logcat
                    .build()
            )

            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectAll() // Detect all VM-related issues
                    .penaltyLog() // Log violations to Logcat
                    .detectLeakedClosableObjects() // Detect unclosed Closeable objects
                    .detectLeakedRegistrationObjects() // Detect unregistered receivers, services, etc.
                    .build()
            )
        }*/
        enableEdgeToEdge()
        setContent {
            PtyxiakhTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    NavigationScreen()
                }
            }
        }
    }
}