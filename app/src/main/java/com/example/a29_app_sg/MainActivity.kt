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
import com.netsend.NetSend
//Plugin FCM

class MainActivity : ComponentActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle ?) {
        super.onCreate(savedInstanceState)
        NetSend.initializeNetSend(this, "YOUR_USER_KEY_HERE")
    }
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
        /* Button(
            onClick = {
                //val hasPermission = NetSend.hasNotificationPermission(context)
                //val status = if (hasPermission) "✅ CONCEDIDOS" else "❌ DENEGADOS"
                Log.d("MainActivity", "📱 Estado permisos")
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
                    // val hasPermission = context.hasNotificationPermission()
                    // val status = if (hasPermission) "✅ CONCEDIDOS" else "❌ DENEGADOS"
                    Log.d("MainActivity", "📱 Estado permisos")
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔍 Verificar Permisos Push")
        } */
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _29_app_sgTheme {
        Greeting("Android")
    }
}