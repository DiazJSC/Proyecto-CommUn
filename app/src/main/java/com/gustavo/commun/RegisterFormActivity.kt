package com.gustavo.commun

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterFormActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userType = intent.getStringExtra("userType") ?: "Usuario"
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        setContent {
            RegisterFormScreen(userType, auth, firestore) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}

@Composable
fun RegisterFormScreen(userType: String, auth: FirebaseAuth, firestore: FirebaseFirestore, onSuccess: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var documento by remember { mutableStateOf("") }

    var modeloVehiculo by remember { mutableStateOf("") }
    var anoVehiculo by remember { mutableStateOf("") }
    var placaVehiculo by remember { mutableStateOf("") }
    var colorVehiculo by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(false) }
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
                val intent = Intent(context, UserTypeSelectionActivity::class.java)
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


        Text(text = "Registro como $userType")
        Spacer(modifier = Modifier.height(12.dp))

        TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellido") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(value = email, onValueChange = { email = it }, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = celular,
            onValueChange = { if (it.all { char -> char.isDigit() }) celular = it },
            label = { Text("Número de celular") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = documento,
            onValueChange = { if (it.all { char -> char.isDigit() }) documento = it },
            label = { Text("Número de documento") },
            modifier = Modifier.fillMaxWidth()
        )

        if (userType == "Conductor") {
            Spacer(modifier = Modifier.height(12.dp))
            TextField(value = modeloVehiculo, onValueChange = { modeloVehiculo = it }, label = { Text("Modelo del Vehículo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            TextField(value = anoVehiculo, onValueChange = { anoVehiculo = it }, label = { Text("Año del Vehículo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            TextField(value = placaVehiculo, onValueChange = { placaVehiculo = it }, label = { Text("Placa del Vehículo") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            TextField(value = colorVehiculo, onValueChange = { colorVehiculo = it }, label = { Text("Color del Vehículo") }, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || password.isEmpty() ||
                celular.isEmpty() || documento.isEmpty() ||
                (userType == "Conductor" && (modeloVehiculo.isEmpty() || anoVehiculo.isEmpty() || placaVehiculo.isEmpty() || colorVehiculo.isEmpty()))
            ) {
                Toast.makeText(context, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                return@Button
            }

            if (userType == "Conductor" && anoVehiculo.toIntOrNull() == null) {
                Toast.makeText(context, "El año del vehículo debe ser un número", Toast.LENGTH_SHORT).show()
                return@Button
            }

            loading = true

            // Verificar si el correo ya existe en la colección "usuarios"
            firestore.collection("usuarios")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        loading = false
                        Toast.makeText(context, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                    } else {
                        // Continuar con el registro
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                loading = false
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val userData = hashMapOf(
                                        "nombre" to nombre,
                                        "apellido" to apellido,
                                        "email" to email,
                                        "userType" to userType,
                                        "celular" to celular,
                                        "documento" to documento
                                    )

                                    // Guardar datos adicionales si es conductor
                                    if (userType == "Conductor") {
                                        userData["modeloVehiculo"] = modeloVehiculo
                                        userData["anoVehiculo"] = anoVehiculo
                                        userData["placaVehiculo"] = placaVehiculo
                                        userData["colorVehiculo"] = colorVehiculo
                                    }

                                    // Determinar la colección según el tipo de usuario
                                    val coleccion = if (userType == "Conductor") "Conductor" else "usuarios"

                                    user?.let {
                                        firestore.collection(coleccion).document(it.uid)
                                            .set(userData)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Usuario registrado como $userType", Toast.LENGTH_SHORT).show()
                                                onSuccess()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(context, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                            }
                                    }
                                } else {
                                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .addOnFailureListener { e ->
                    loading = false
                    Toast.makeText(context, "Error al verificar el correo: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF25518B),
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()) {
            Text("Registrar")
        }

        if (loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
