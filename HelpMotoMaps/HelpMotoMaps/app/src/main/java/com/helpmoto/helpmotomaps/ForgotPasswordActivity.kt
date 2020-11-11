package com.helpmoto.helpmotomaps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var txtEmail: EditText
    private lateinit var auth:FirebaseAuth
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        txtEmail=findViewById(R.id.txtEmail)

        //Se llama al progressbar
        progressBar=findViewById(R.id.progressBar)

        auth= FirebaseAuth.getInstance()
    }

    //Funcion para onclick
    fun send(view:View){
          val email=txtEmail.text.toString()
        if (!TextUtils.isEmpty(email)){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this){
                    task ->
                    //Todo salio bien
                    if (task.isSuccessful){
                        //Hacer visible el progressbar
                        progressBar.visibility=View.VISIBLE
                        Toast.makeText(this,"Se envió e-mail de restablecimiento de contraseña", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    }else{
                        Toast.makeText(this,"Error al enviar e-mail", Toast.LENGTH_LONG).show()
                                            }
                }
        }else{
            Toast.makeText(this,"Debe diligenciar todos los datos", Toast.LENGTH_SHORT).show()
        }
    }
}