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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class RegisterActivity : AppCompatActivity() {

    //Declaracion de todas las variables
    private lateinit var txtName:EditText
    private lateinit var txtLastName:EditText
    private lateinit var txtPhone1:EditText
    private lateinit var txtPhone2:EditText
    private lateinit var txtEmail:EditText
    private lateinit var txtPassword:EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var dbReference:DatabaseReference
    private lateinit var database:FirebaseDatabase
    //Declara una instancia de FirebaseAuth.
    private lateinit var auth:FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //Llamar nuestras vistas
        txtName=findViewById(R.id.txtName)
        txtLastName=findViewById(R.id.txtLastName)
        txtPhone1=findViewById(R.id.txtPhone1)
        txtPhone2=findViewById(R.id.txtPhone2)
        txtEmail=findViewById(R.id.txtEmail)
        txtPassword=findViewById(R.id.txtPassword)

        //Se llama al progressbar
        progressBar=findViewById(R.id.progressBar)

        //Instancia para la base de datos
        database= FirebaseDatabase.getInstance()

        //Instancia para la autenticacion
        auth= FirebaseAuth.getInstance()

        //Referencia para leer o escribir en una ubicacion
        dbReference=database.reference.child("User")
    }

    //Metodo onClick (Del boton)
    fun register(view:View){
        //Llamamos al boton cuando se presione el boton registar
        createNewAcoount()
    }

  private fun createNewAcoount(){
      //Obtener los valores de la caja de texto
      val name:String=txtName.text.toString()
      val lastname:String=txtLastName.text.toString()
      val phone1:String=txtPhone1.text.toString()
      val phone2:String=txtPhone2.text.toString()
      val email:String=txtEmail.text.toString()
      val password:String=txtPassword.text.toString()

      //Mirar que no esten vacions
      if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(lastname) && !TextUtils.isEmpty(email)
          && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(phone1) && !TextUtils.isEmpty(phone2)){

          //Mostrar pogress bar
          progressBar.visibility=View.VISIBLE

          //Dar de alta el usuario y contraseña
          auth.createUserWithEmailAndPassword(email, password)
              .addOnCompleteListener(this){

                  //Verificar que el registro sea exitoso
                  task ->
                  if (task.isComplete){
                      //Obtener el usuario registrado
                      val user: FirebaseUser? = auth.currentUser

                      //Enviar un email al usuario que se registro correctamente
                        verifyEmail(user)

                      //Ya el usuario tiene creada su cuenta
                      //Se va a dar de alta los otros datos en la base de datos Aca sale error
                      val userBD=dbReference.child(user?.uid.toString())
                      //Agregar el nombre
                      userBD.child("Name").setValue(name)
                      userBD.child("lastName").setValue(lastname)
                      userBD.child("phone1").setValue(phone1)
                      userBD.child("phone2").setValue(phone2)
                      action()
                     // Toast.makeText(this,"Registro correcto", Toast.LENGTH_LONG).show()
                  }else{
                      Toast.makeText(this,"No es correcta la tarea", Toast.LENGTH_LONG).show()
                  }
              }
      }else{
          Toast.makeText(this,"Debe diligenciar todos los datos", Toast.LENGTH_SHORT).show()

      }
    }

    //Se va a realizar cuando todo salga correctamente
    private fun action(){
        //Mandar al usuario a la vista login
        startActivity(Intent(this, LoginActivity::class.java))
    }
    private fun verifyEmail(user:FirebaseUser?){
        user?.sendEmailVerification()
            ?.addOnCompleteListener(this){
            task ->
            if (task.isComplete){
                Toast.makeText(this,"E-mail enviado,por favor autorice para iniciar sesión", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this,"Error al enviar el e-mail", Toast.LENGTH_LONG).show()
            }
        }
    }
}
