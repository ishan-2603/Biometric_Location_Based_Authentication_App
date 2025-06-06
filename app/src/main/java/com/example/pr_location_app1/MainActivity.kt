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
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationResult

class MainActivity : AppCompatActivity() {
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: PromptInfo
    private lateinit var authButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authButton = findViewById(R.id.auth_finger_button)
        authButton.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }

        val biometricManager = androidx.biometric.BiometricManager.from(this)
        when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                displayMessage("Biometric authentication is available")
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                displayMessage("This device doesn't support biometric authentication")
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                displayMessage("Biometric authentication is currently unavailable")
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                displayMessage("No biometric credentials are enrolled")
            }
        }
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt =
            BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    displayMessage("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    displayMessage("Authentication succeeded")
                    val intent = Intent(
                        this@MainActivity,
                        com.example.pr_location_app1.LoggedInActivity::class.java
                    )
                    startActivity(intent);
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    displayMessage("Authentication failed")
                }
            })

        promptInfo = PromptInfo.Builder()
            .setTitle("Biometric Authentication")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Cancel")
            .build()

    }

    private fun displayMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}




//    private lateinit var locationCallback: LocationCallback
//    private lateinit var savedLocation: Location

//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
//        private const val LOCATION_UPDATE_INTERVAL: Long = 1
//        private const val DISTANCE_THRESHOLD: Float = 1f // Threshold distance in meters
//    }

//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        savedLocation = Location("default")
//        savedLocation.latitude = 0.0
//        savedLocation.longitude = 0.0



//    private fun getLastKnownLocation() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            // Permission is granted, access last known location
//            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
//                location?.let {
//                    savedLocation = it
//
//                    val intent = Intent(
//                        this@MainActivity,
//                        com.example.pr_location_app1.LoggedInActivity::class.java
//                    )
//                    intent.putExtra("savedLocation",savedLocation);
//                    startActivity(intent)
//                }
//            }.addOnFailureListener { e ->
//                // Handle failure to retrieve last known location
//                Log.e("MainActivity", "Failed to get last known location: ${e.message}")
//            }
//        } else {
//            // Permission is not granted, request location permission
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }

//        requestLocationPermissions()

//        authButton.setOnClickListener {
//            biometricPrompt.authenticate(promptInfo)
//        }

//    private fun requestLocationPermissions() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//
//        }
//    }

//}
//
//    @SuppressLint("MissingPermission")
//    private fun startLocationUpdates() {
//        val locationRequest = create().apply {
//            interval = LOCATION_UPDATE_INTERVAL
//            fastestInterval = LOCATION_UPDATE_INTERVAL / 2
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        }
//
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            null
//        )
//    }
//    private var isRedirected = false
//    private fun checkDistance(currentLocation: Location) {
//        val distance = savedLocation.distanceTo(currentLocation)
//        if (distance >= DISTANCE_THRESHOLD && !isRedirected) {
//            // User moved away beyond threshold distance
//            isRedirected=true;
//            logoutUser()
//        }
//    }
//
//    private fun logoutUser() {
////        val intent = Intent(this, MainActivity::class.java)
////        startActivity(intent)
////        finish()
//        authButton.setOnClickListener {
//            biometricPrompt.authenticate(promptInfo)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission granted, start location updates
//                startLocationUpdates()
//            } else {
//                // Permission denied, show a message or handle it accordingly
//                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        fusedLocationClient.removeLocationUpdates(locationCallback)
//    }
//
//    override fun onResume() {
//        super.onResume()
//        startLocationUpdates()
//    }
//
//}