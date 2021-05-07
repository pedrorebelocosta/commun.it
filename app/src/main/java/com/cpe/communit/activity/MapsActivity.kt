package com.cpe.communit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.cpe.communit.R
import com.cpe.communit.SessionManager
import com.cpe.communit.adapter.MarkerInfoWindowAdapter
import com.cpe.communit.data.JWTPayload
import com.cpe.communit.data.Occurrence
import com.cpe.communit.service.EndPoints
import com.cpe.communit.service.ServiceBuilder
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
    private lateinit var mMap: GoogleMap

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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))
        mMap.setOnInfoWindowClickListener(this)
        mMap.uiSettings.isZoomControlsEnabled = false
        val portugal = LatLng(40.146212804514626, -8.095561077725725)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(portugal, 6.0f))

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
                        marker?.tag = occurrence
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

    companion object {
        const val OCCURRENCE_EXTRA = "com.cpe.communit.activity.OccurrenceDetailActivity.OCCURRENCE_EXTRA"
    }
}