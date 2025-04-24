package com.gustavo.commun

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

class UserTypeSelectionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { // Usa el tema por defecto
                Surface(modifier = Modifier.fillMaxSize()) {
                    UserTypeSelectionScreen { selectedType ->
                        val intent = Intent(this, RegisterFormActivity::class.java)
                        intent.putExtra("userType", selectedType)
                        startActivity(intent)
                    }
                }
            }
        }
    }
}

@Composable
fun UserTypeSelectionScreen(onUserTypeSelected: (String) -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25518B),
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(130.dp)
                .height(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Regresar",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Volver")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "¿Qué tipo de usuario serás?", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(24.dp))

        Image (
            painter = painterResource(id = R.drawable.condu),
            contentDescription = "Logo",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .align(Alignment.CenterHorizontally)
        )

        Button(onClick = { onUserTypeSelected("Conductor") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25518B),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()) {
            Text("Conductor")
        }
        Spacer(modifier = Modifier.height(12.dp))

        Image (
            painter = painterResource(id = R.drawable.usuario),
            contentDescription = "Logo",
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .align(Alignment.CenterHorizontally)
        )

        Button(onClick = { onUserTypeSelected("Usuario") },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25518B),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()) {
            Text("Usuario")
        }
    }
}
