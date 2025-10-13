package com.example.a29_app_sg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.a29_app_sg.ui.theme._29_app_sgTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import android.util.Log

//Plugin FCM
import com.example.a29_app_sg.push_not.MyFirebaseMessagingService
//Plugin FCM
//Permisos FCM
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
//Permisos FCM

class MainActivity : ComponentActivity() {
    //Permisos FCM
    companion object {
        private const val TAG = "MainActivity"
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { 
        isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "✅ Permiso de notificaciones concedido")
        } else {
            Log.w(TAG, "❌ Permiso de notificaciones denegado")
        }
        initializeFCM()
    }
    //Permisos FCM
    override fun onCreate(savedInstanceState: Bundle ?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissions()
        //MyFirebaseMessagingService().initializeFCMToken(this)
        setContent {
            _29_app_sgTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    //Permisos FCM
    private fun requestNotificationPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d(TAG, "🔔 Permiso de notificaciones ya concedido")
                    initializeFCM()
                }
                else -> {
                    Log.d(TAG, "🔔 Solicitando permiso de notificaciones")
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            Log.d(TAG, "🔔 No se requieren permisos para notificaciones en esta versión de Android")
            initializeFCM()
        }
    }

    private fun initializeFCM() {
        MyFirebaseMessagingService.initializeFCMToken(this)
    }

    fun requestPushPermissions() {
        Log.d(TAG, "🔔 Solicitando permisos desde Service...")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
    //Permisos FCM
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hello $name!",
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // ✅ BOTÓN 1: Solicitar permisos push
        Button(
            onClick = {
                if (context is MainActivity) {
                    context.requestPushPermissions()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("🔔 Solicitar Permisos Push")
        }
        
        // ✅ BOTÓN 2: Verificar permisos push
        Button(
            onClick = {
                if (context is MainActivity) {
                    val hasPermission = context.hasNotificationPermission()
                    val status = if (hasPermission) "✅ CONCEDIDOS" else "❌ DENEGADOS"
                    Log.d("MainActivity", "📱 Estado permisos: $status")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔍 Verificar Permisos Push")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _29_app_sgTheme {
        Greeting("Android")
    }
}