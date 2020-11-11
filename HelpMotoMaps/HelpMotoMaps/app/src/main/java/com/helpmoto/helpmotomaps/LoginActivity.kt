package com.helpmoto.helpmotomaps

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {

    //Declaracion de todas las variables
    private lateinit var txtUser: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtNumero: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var dbReference: DatabaseReference
    private lateinit var usuario: FirebaseUser


    // [START declare_database_ref]
    //private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Llamar nuestras vistas
        txtUser = findViewById(R.id.txtUser)
        txtPassword = findViewById(R.id.txtPassword)
//      txtNumero = findViewById(R.id.txtNumero)


        //Se llama al progressbar
        progressBar = findViewById(R.id.progressBar)

        //Instancia para la base de datos
        database = FirebaseDatabase.getInstance()

        //Instancia para la autenticacion
        auth = FirebaseAuth.getInstance()

        //Se obtiene el usuario
//        usuario = auth.getCurrentUser()!!

        //Referencia para leer o escribir en una ubicacion
//        dbReference = database.getReference("User").child(usuario.uid)


    }

    //Tres funciones para el click
    fun forgotPassword(view: View) {
        //Enviarlo a la ventana de restablecimiento
        startActivity(Intent(this, ForgotPasswordActivity::class.java))
    }

    fun register(view: View) {
        //Enviarlo a la ventana de registro
        startActivity(Intent(this, RegisterActivity::class.java))
    }

    fun login(view: View) {
        loginUser()
    }

    private fun loginUser() {
        //Variable inmutable
        val user: String = txtUser.text.toString()
        val password: String = txtPassword.text.toString()

        //Verificamos que las variables no esten vacias
        if (!TextUtils.isEmpty(user) && !TextUtils.isEmpty(password)) {

            //Realizar el inicio de sesion
            auth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(this) {
                    //Se llama la tarea
                        task ->
                    if (task.isSuccessful) {
                        //Mostramos progressbar
                        progressBar.visibility = View.VISIBLE
                        //Mandamos al usuario a la vista principal
                         action()
                        //mostrarInfo()
                     
                    } else {
                        Toast.makeText(this, "Error en autenticación, inténtelo de nuevo", Toast.LENGTH_LONG).show()
                    }
                }
        } else {
            Toast.makeText(this, "Debe diligenciar todos los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun action() {
        startActivity(Intent(this, MapsActivity::class.java))
    }
}

