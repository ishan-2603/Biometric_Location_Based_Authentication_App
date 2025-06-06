package com.example.pr_location_app1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.Toast
import android.hardware.biometrics.BiometricManager
import com.google.android.gms.location.*
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationResult


class LoggedInActivity : AppCompatActivity() {
    private lateinit var loggedInText: TextView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var savedLocation: Location


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val LOCATION_UPDATE_INTERVAL:  Long = 1
        private const val DISTANCE_THRESHOLD: Float = 20f // Threshold distance in meters
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.logged_in_activity)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastKnownLocation()
        loggedInText=findViewById<TextView>(R.id.loggedInText)
        loggedInText.text="Logged In"
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { location ->
                    checkDistance(location)
                }
            }
        }
        startLocationUpdates()
        requestLocationPermissions()


    }


    private fun getLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is granted, access last known location
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    savedLocation = it
                }
            }.addOnFailureListener { e ->
                // Handle failure to retrieve last known location
                Log.e("MainActivity", "Failed to get last known location: ${e.message}")
            }
        } else {
            // Permission is not granted, request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LoggedInActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permissions granted, start location updates
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = LoggedInActivity.LOCATION_UPDATE_INTERVAL
            fastestInterval = LoggedInActivity.LOCATION_UPDATE_INTERVAL / 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }
    private var isRedirected = false
    private fun checkDistance(currentLocation: Location) {
        val distance = savedLocation.distanceTo(currentLocation)
        if (distance >= LoggedInActivity.DISTANCE_THRESHOLD && !isRedirected) {
            // User moved away beyond threshold distance
            isRedirected=true;
            logoutUser()
        }
    }

    private fun logoutUser() {
        savedLocation = Location("")
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LoggedInActivity.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start location updates
                startLocationUpdates()
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onPause() {
//        super.onPause()
//        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onPause()
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::locationCallback.isInitialized) {
            startLocationUpdates()
        }
    }
}
