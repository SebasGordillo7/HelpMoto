package com.helpmoto.helpmotomaps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.net.URI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat



class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap

    //Declara una instancia de FirebaseAuth.
    private lateinit var auth: FirebaseAuth


    private lateinit var database: FirebaseDatabase
    private lateinit var dbReference: DatabaseReference
    private lateinit var usuario: FirebaseUser
    private lateinit var txtNumero: TextView

    //Variables para enviar a google maps
    private lateinit var latitud: String
    private lateinit var longitud: String


    //Variable perezoza "lateinit" solo se inicializa cuando es necesario
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Actividad que nos dara la ultima ubicacion del usuario
    private lateinit var lastLocation: Location

    companion object {
        //constante que servira para identificar el permiso para acceder a la localizacion
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    //Metodo que realizaba todos los controles del mapa
    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        txtNumero = findViewById(R.id.txtNumero)
        //Instancia para la base de datos
        database = FirebaseDatabase.getInstance()

        //Instancia para la autenticacion
        auth = FirebaseAuth.getInstance()

        //Se obtiene el usuario
        usuario = auth.getCurrentUser()!!

        //Referencia para leer o escribir en una ubicacion
        dbReference = database.getReference("User").child(usuario.uid)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //añade un marcador en la localizacion sydney
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Ubicacion con latitud y lon exacta de syndey
        /*val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).tittle("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/


        //Le estamos diciendo que la clase MapsActivity tendra el control
        mMap.setOnMarkerClickListener(this)

        //activa los controles de zoom en el mapa
        mMap.uiSettings.isZoomControlsEnabled = true

        //llamar al metodo para dar permisos
        setUpMap()
    }

    //Metodo que posiciona un marcador en la latitud y longitud que se le pasa como parametro
    private fun placeMarker(location: LatLng) {

        val markerOptions = MarkerOptions().position(location)

        mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marcador_casco)))

        /* mMap.addMarker(MarkerOptions().
         icon(bitmapDescriptorFromVector(this, R.mipmap.ic_marcador_casco_round)))*/
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        var vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable!!.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        var bitmap = Bitmap.createBitmap(
            vectorDrawable.getIntrinsicWidth(),
            vectorDrawable.getIntrinsicHeight(),
            Bitmap.Config.ARGB_8888
        );
        var canvas = Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    //Metodo que pregunta por los permisos de ubicacion
    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        //Codigo para desplegar nuestra ubicacion en tiempo real
        //Añade una capa encima del mapa donde esta el punto azul de la ubicacion del usuario
        mMap.isMyLocationEnabled = true

        //Esta llamada se ejecuta cuando obtengamos la localizacion del usuario
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->

            //Si la localizacion es diferente a null
            if (location != null) {
                lastLocation = location

                //Latitud y longitud actual se guardan en currentLatLng
                val currentLatLng = LatLng(location.latitude, location.longitude)

                //Variables que se guarda la ubicacion actual coordenadas de latitud / longitud
                latitud = lastLocation.latitude.toString()
                longitud = lastLocation.longitude.toString()
                //  localizacion = LatLng(location.latitude, location.longitude).toString()
                //Llamamos al metodo
                //placeMarker(currentLatLng)

                mMap.addMarker(
                    MarkerOptions()
                        .position(currentLatLng)
                        .icon(bitmapDescriptorFromVector(this, R.drawable.ic_marcador_casco_foreground))
                )


                //Para que el mapa se mueva a la ubicacion del usuario con zoom de 13 (0-20, 0 lejos y 20 mas cerca)
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13f))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17f))

                //Adicionar marcador
                /*mMap.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.
                fromResource(R.mipmap.ic_marcador_casco_round)).anchor(0.0f, 1.0f).position(currentLatLng))*/


            }
        }

    }

    //Metodo onClick (Del boton)
    fun accidente(view: View) {
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var nombre = snapshot.child("Name").getValue().toString()
                    var apellido = snapshot.child("lastName").getValue().toString()
                    var telefono = snapshot.child("phone1").getValue().toString()
                    var telefono2 = snapshot.child("phone2").getValue().toString()

                    //Se lanza a wp
                    whatsapp(view, telefono)
                    /* txtNumero.setText(
                         "Nombre: " + nombre + " Telefono: " + telefono +
                                 " Apellido: " + apellido + " Otro telefono: " + telefono2 + " Posicion: " + localizacion
                     )*/
                    txtNumero.setText(" Posicion: " + latitud + "," + longitud)
                } else {
                    //Si el usuario no existe
                }

            }

            override fun onCancelled(error: DatabaseError) {
                print("Error ningun telefono")
            }


        })
        //Llamamos al boton cuando se presione el boton accidente
        //createNewAcoount()
    }


    fun whatsapp(view: View, tel: String) {
        //Si no guardo un telefono lo manda a la aplicacion de wp para elegir el contacto
        if (tel.isEmpty()) {

            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_SEND)
            sendIntent.putExtra(
                Intent.EXTRA_TEXT, "He sufrido un accidente, esta es mi localización: " +
                        "http://maps.google.com/maps?q=loc:" + latitud + "," + longitud
            )
            sendIntent.setType("text/plain")
            sendIntent.setPackage("com.whatsapp")
            startActivity(sendIntent)
        } else {
            //Si se tiene el celular

            val sendIntent = Intent()
            sendIntent.setAction(Intent.ACTION_VIEW)
            var uri = "whatsapp://send?phone=+57" + tel +
                    "&text=He sufrido un accidente, esta es mi localización: http://maps.google.com/maps?q=loc:" +
                    latitud + "," + longitud
            /*"&text=He sufrido un accidente, esta es mi localización:" +
            "https://www.google.com/maps/search/?api=1" + "&query=4.6062383,-74.0695426"*/
            sendIntent.setData(Uri.parse(uri))
            startActivity(sendIntent)
        }
    }


    fun llamadacall(view: View) {
        dbReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    var telefono = snapshot.child("phone1").getValue().toString()
                    //Se lanza la llamada al numero registrado
                    val call = Intent(Intent.ACTION_CALL)
                    call.data = Uri.parse("tel:"+telefono)
                    if (ActivityCompat.checkSelfPermission(this@MapsActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    }
                    startActivity(call)
                } else {
                    //Si el usuario no existe
                }

            }

            override fun onCancelled(error: DatabaseError) {
                print("Error ningun telefono")
            }




        }
        )}
}


