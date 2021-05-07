package com.cpe.communit.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.cpe.communit.R
import com.cpe.communit.SessionManager
import com.cpe.communit.adapter.MarkerInfoWindowAdapter
import com.cpe.communit.data.JWTPayload
import com.cpe.communit.data.Occurrence
import com.cpe.communit.service.EndPoints
import com.cpe.communit.service.ServiceBuilder
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var location: Location
    private lateinit var mMap: GoogleMap
    private val markerList = ArrayList<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        map_back_button.setOnClickListener { onBackPressed() }
        add_occurrence_btn.setOnClickListener {
            startActivity(Intent(this, AddOccurrenceActivity::class.java))
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                location = locationResult.lastLocation
            }
        }

        is_road_problem_filter.setOnCheckedChangeListener { _, isChecked ->
            for (marker in markerList) {
                val occurrence = marker.tag as Occurrence
                if (marker.isVisible) {
                    marker.isVisible = !(isChecked && !occurrence.is_road_problem)
                } else if (!marker.isVisible && !isChecked) {
                    when (distance_filters.checkedRadioButtonId) {
                        R.id.radio_show_all -> {
                            marker.isVisible = true
                        }
                        R.id.radio_show_1km -> {
                            if (calculateDistance(location, marker.position) <= ONE_KM) {
                                marker.isVisible = true
                            }
                        }
                        R.id.radio_show_5km -> {
                            if (calculateDistance(location, marker.position) <= FIVE_KM) {
                                marker.isVisible = true
                            }
                        }
                        R.id.radio_show_15km -> {
                            if (calculateDistance(location, marker.position) <= FIFTEEN_KM) {
                                marker.isVisible = true
                            }
                        }
                    }
                }
            }
        }

        distance_filters.setOnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.radio_show_all -> {
                    for (marker in markerList) {
                        is_road_problem_filter.isChecked = false
                        marker.isVisible = true
                    }
                }
                R.id.radio_show_1km -> {
                    for (marker in markerList) {
                        is_road_problem_filter.isChecked = false
                        marker.isVisible = calculateDistance(location, marker.position) <= ONE_KM
                    }
                }
                R.id.radio_show_5km -> {
                    for (marker in markerList) {
                        is_road_problem_filter.isChecked = false
                        marker.isVisible = calculateDistance(location, marker.position) <= FIVE_KM
                    }
                }
                R.id.radio_show_15km -> {
                    for (marker in markerList) {
                        is_road_problem_filter.isChecked = false
                        marker.isVisible = calculateDistance(location, marker.position) <= FIFTEEN_KM
                    }
                }
            }
        }
    }

    private fun calculateDistance(location: Location, point2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            location.latitude, location.longitude,
            point2.latitude, point2.longitude,
            results
        )
        Log.d("Distance", results[0].toString())
        return results[0]
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
        mMap.setOnInfoWindowClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = false
        val portugal = LatLng(40.146212804514626, -8.095561077725725)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(portugal, 7.0f))

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getOccurrences()

        call.enqueue(object: Callback<List<Occurrence>> {
            override fun onResponse(call: Call<List<Occurrence>>, response: Response<List<Occurrence>>) {
                if (response.isSuccessful) {
                    val jwtPayload: JWTPayload? = SessionManager.getJWTPayload()
                    for (occurrence in response.body()!!) {
                        val markerOptions = MarkerOptions().position(LatLng(occurrence.lat, occurrence.lng))
                        if (jwtPayload?.user_id == occurrence.user_id) {
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        }
                        val marker = mMap.addMarker(markerOptions)
                        marker!!.tag = occurrence
                        markerList.add(marker)
                    }
                }
            }

            override fun onFailure(call: Call<List<Occurrence>>, t: Throwable) {
                Log.e("MapsActivity: ", t.message.toString())
            }
        })
    }

    override fun onInfoWindowClick(marker: Marker) {
        val occurrence = marker.tag as Occurrence
        val intent = Intent(this@MapsActivity, OccurrenceDetailActivity::class.java)
        intent.putExtra(OCCURRENCE_EXTRA, occurrence)
        startActivity(intent)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    companion object {
        const val OCCURRENCE_EXTRA = "com.cpe.communit.activity.OccurrenceDetailActivity.OCCURRENCE_EXTRA"
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val ONE_KM = 1000
        private const val FIVE_KM = 5000
        private const val FIFTEEN_KM = 15000
    }
}