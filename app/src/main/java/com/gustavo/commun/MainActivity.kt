package com.gustavo.commun

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontStyle

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            LoginAndRegisterScreen(auth = auth) {
                // Navega a HomeActivity cuando el login sea exitoso
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
    }
}

@Composable
fun LoginAndRegisterScreen(auth: FirebaseAuth, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    )

    {
        Image (
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(330.dp)
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .align(Alignment.CenterHorizontally)
        )

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Por favor ingresa todos los campos", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                loading = true
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        loading = false
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Inicio exitoso", Toast.LENGTH_SHORT).show()
                            onLoginSuccess()
                        } else {
                            // Validación para usuario no registrado o contraseña incorrecta
                            if (task.exception?.message?.contains("There is no user record corresponding to this identifier") == true) {
                                Toast.makeText(context, "Usuario no registrado", Toast.LENGTH_SHORT).show()
                            } else if (task.exception?.message?.contains("The password is invalid") == true) {
                                Toast.makeText(context, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "¡El usuario no esta creado!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25518B),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                // Aquí corregimos el nombre correcto de la actividad de selección de tipo de usuario
                val intent = Intent(context, UserTypeSelectionActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25518B),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }

        if (loading) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Versión BETA",
            fontStyle = FontStyle.Italic,
            modifier = Modifier
                .height(20.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}
