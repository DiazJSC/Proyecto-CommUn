package com.gustavo.commun

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        setContent {
            currentUser?.let {
                HomeScreen(
                    email = it.email ?: "Usuario",
                    onDeleteAccount = {
                        it.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Error al eliminar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    onSignOut = {
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            } ?: run {
                Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}

@Composable
fun HomeScreen(email: String, onDeleteAccount: () -> Unit, onSignOut: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Bienvenido: $email")
        Spacer(modifier = Modifier.height(24.dp))

        // Botón para eliminar cuenta
        Button(onClick = { onDeleteAccount() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25518B),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()) {
            Text("Eliminar Cuenta")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para cerrar sesión
        Button(onClick = { onSignOut() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25518B),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar Sesión")
        }
    }
}
