package com.example.apotekonline.mapslistener

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.apotekonline.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.net.URL
import kotlin.math.*

import android.location.Geocoder
import android.location.Address
import android.util.Log
import android.widget.EditText
import java.util.*

class MapsActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var recenterButton: Button
    private lateinit var backButton: Button
    private lateinit var deviceLocationEditText: EditText
    private lateinit var closestApotekEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Initialize OSM Configuration
        Configuration.getInstance().userAgentValue = packageName

        // Initialize the MapView
        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true)

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize buttons and EditTexts
        recenterButton = findViewById(R.id.recenterButton)
        backButton = findViewById(R.id.backButton)
        deviceLocationEditText = findViewById(R.id.deviceLocationEditText)
        closestApotekEditText = findViewById(R.id.closestApotekEditText)

        // Check and request location permissions
        if (!isLocationPermissionGranted()) {
            requestLocationPermission()
        } else {
            updateMapWithCurrentLocation()
        }

        // Set recenter button click listener
        recenterButton.setOnClickListener {
            updateMapWithCurrentLocation()
        }

        backButton.setOnClickListener {
            finish()
        }
        updateMapWithCurrentLocation()
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun updateMapWithCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val deviceLocation = GeoPoint(location.latitude, location.longitude)

                // Center map to device location
                mapView.controller.setCenter(deviceLocation)

                // Add marker for device location with custom logo (it will not be removed)
                addDeviceLocationMarker(deviceLocation)

                // Reverse geocode to get the road name
                reverseGeocodeLocation(deviceLocation, true)

                // Zoom in to an appropriate level
                mapView.controller.setZoom(16.0) // Adjust this zoom level if necessary

                // Search and display nearby pharmacies
                searchNearbyPharmacies(deviceLocation)
            } else {
                Log.e("LocationError", "Failed to get current location. Location is null.")
                Toast.makeText(this, "Failed to get current location.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("LocationError", "Error getting current location: ${exception.message}")
            Toast.makeText(this, "Failed to get current location.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addDeviceLocationMarker(location: GeoPoint) {
        // Add device location marker (if not already added)
        val deviceLocationMarker = Marker(mapView)
        deviceLocationMarker.position = location
        deviceLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        deviceLocationMarker.title = "Your Location"

        // Set custom icon for device location
        val deviceLocationIcon = ContextCompat.getDrawable(this, R.drawable.device_location_icon)
        deviceLocationMarker.icon = deviceLocationIcon

        // Only add if not already added to prevent duplicate markers
        if (!mapView.overlays.contains(deviceLocationMarker)) {
            mapView.overlays.add(deviceLocationMarker)
        }
    }

    private fun reverseGeocodeLocation(location: GeoPoint, isDeviceLocation: Boolean) {
        val geocoder = Geocoder(this, Locale.getDefault())
        val latitude = location.latitude
        val longitude = location.longitude

        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val roadName = address.getAddressLine(0) // This returns the full address

                if (isDeviceLocation) {
                    deviceLocationEditText.setText(roadName) // Show in device location EditText
                } else {
                    closestApotekEditText.setText(roadName) // Show in closest apotek EditText
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to get address", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchNearbyPharmacies(deviceLocation: GeoPoint) {
        val lat = deviceLocation.latitude
        val lon = deviceLocation.longitude
        val searchTerms = listOf("apotek", "apotik", "K24", "Kimia Farma")

        // Use Coroutine to fetch data for each search term
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val markers = mutableListOf<Marker>()
                val pharmacyResults = mutableListOf<Pharmacy>()

                for (searchTerm in searchTerms) {
                    val url = "https://nominatim.openstreetmap.org/search?q=$searchTerm&format=json&addressdetails=1&limit=10&viewbox=${lon - 0.05},${lat + 0.05},${lon + 0.05},${lat - 0.05}&bounded=1"
                    val response = URL(url).readText()
                    val jsonArray = JSONArray(response)

                    if (jsonArray.length() > 0) {
                        for (i in 0 until jsonArray.length()) {
                            val obj = jsonArray.getJSONObject(i)
                            val latitude = obj.getString("lat").toDouble()
                            val longitude = obj.getString("lon").toDouble()
                            val displayName = obj.getString("display_name")

                            // Calculate the distance between the device and the pharmacy
                            val distance = calculateDistance(lat, lon, latitude, longitude)

                            // Create Pharmacy object
                            val pharmacy = Pharmacy(displayName, latitude, longitude, distance)

                            pharmacyResults.add(pharmacy)
                        }
                    }
                }

                // Sort pharmacies by distance
                pharmacyResults.sortBy { it.distance }

                // Reverse geocode to get the address of the closest pharmacy
                if (pharmacyResults.isNotEmpty()) {
                    val closestPharmacy = pharmacyResults[0]
                    val pharmacyLocation = GeoPoint(closestPharmacy.latitude, closestPharmacy.longitude)

                    // Reverse geocode to get the road name of the pharmacy
                    reverseGeocodeLocation(pharmacyLocation, false)
                }

                // Create markers for the sorted pharmacies
                for (pharmacy in pharmacyResults) {
                    val marker = Marker(mapView).apply {
                        position = GeoPoint(pharmacy.latitude, pharmacy.longitude)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = pharmacy.name

                        // Set custom icon for pharmacy markers
                        val pharmacyIcon = ContextCompat.getDrawable(this@MapsActivity, R.drawable.default_marker_icon)
                        icon = pharmacyIcon
                    }
                    markers.add(marker)
                }

                withContext(Dispatchers.Main) {
                    if (markers.isNotEmpty()) {
                        // Remove old pharmacy markers without removing device location marker
                        mapView.overlays.removeAll { it !is Marker } // Remove non-device location markers
                        mapView.overlays.addAll(markers)
                    } else {
                        Toast.makeText(this@MapsActivity, "No pharmacies found nearby!", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("Data", "ERROR")
            }
        }
    }

    // Calculate distance between two coordinates (Haversine formula)
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371.0 // Radius of the Earth in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return radius * c // Distance in kilometers
    }

    data class Pharmacy(val name: String, val latitude: Double, val longitude: Double, val distance: Double)

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateMapWithCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
