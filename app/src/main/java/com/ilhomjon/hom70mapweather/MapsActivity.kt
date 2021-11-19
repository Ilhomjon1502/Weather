package com.ilhomjon.hom70mapweather

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.florent37.runtimepermission.kotlin.askPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.ilhomjon.hom70mapweather.databinding.ActivityMapsBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.maps.CameraUpdate








//link openWeather: https://openweathermap.org/current
// my API key: 47fc9b7e325547dd55e54f0952a99319
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

//    api.openweathermap.org/data/2.5/weather?lat=-34.0&lon=151.0&appid=47fc9b7e325547dd55e54f0952a99319

    private val TAG = "MapsActivity"
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    var lastKnownLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        getDeviceLocation()
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
    var marker:MarkerOptions? = null
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnMapClickListener {
            mMap.clear()
            marker = MarkerOptions().position(it)
            mMap.addMarker(marker!!)

            val current = CameraUpdateFactory.newLatLngZoom(it, 15f)
            mMap.animateCamera(current)
        }

        mMap.setOnMarkerClickListener { 
            val latLng = it.position
            Toast.makeText(this, "$latLng", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("lat", latLng.latitude)
            intent.putExtra("long", latLng.longitude)
            startActivity(intent)
            true
        }
    }

    private fun getDeviceLocation() {
        try {

            askPermission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION) {
                //all permissions already granted or just granted

                fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this,
                        OnSuccessListener<Location?> { location ->
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location.latitude,
                                        location.longitude
                                    ), 17.0f
                                )
                            )
                                Toast.makeText(this, "Location aniqlandi", Toast.LENGTH_SHORT).show()
                            }
                         })
                    .addOnFailureListener{
                        Toast.makeText(this, "Location aniqlab bo'lmadi", Toast.LENGTH_SHORT).show()
                    }


//                val locationResult = fusedLocationProviderClient.lastLocation
//                locationResult.addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        // Set the map's camera position to the current location of the device.
//                        lastKnownLocation = task.result
//                        if (lastKnownLocation != null) {
//                            mMap?.moveCamera(
//                                CameraUpdateFactory.newLatLngZoom(
//                                    LatLng(
//                                        lastKnownLocation!!.latitude,
//                                        lastKnownLocation!!.longitude
//                                    ), 17.0f
//                                )
//                            )
//                        }
//
//                    }
//                }


//                val fusedLocationProviderClient =
//                    LocationServices.getFusedLocationProviderClient(this)
//                val locationTask: Task<Location> = fusedLocationProviderClient.lastLocation
//                locationTask.addOnSuccessListener { it: Location ->
//                    if (it != null) {
//                        //We have a location
//                        mMap?.moveCamera(
//                            CameraUpdateFactory.newLatLngZoom(
//                                LatLng(
//                                    it.latitude,
//                                    it.longitude
//                                ), 17.0f
//                            )
//                        )
//                    } else {
//                        Log.d(
//                            TAG,
//                            "getLastLocation: location was null,,,,,,,,,,,,,,,,,,,..............."
//                        )
//                    }
//                }
//                locationTask.addOnFailureListener {
//                    Log.d(TAG, "getLastLocation: ${it.message}")
//                }

            }.onDeclined { e ->
                if (e.hasDenied()) {

                    AlertDialog.Builder(this)
                        .setMessage("Please accept our permissions")
                        .setPositiveButton("yes") { dialog, which ->
                            e.askAgain();
                        } //ask again
                        .setNegativeButton("no") { dialog, which ->
                            dialog.dismiss();
                        }
                        .show();
                }

                if (e.hasForeverDenied()) {
                    //the list of forever denied permissions, user has check 'never ask again'

                    // you need to open setting manually if you really need it
                    e.goToSettings();
                }
            }


        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

}