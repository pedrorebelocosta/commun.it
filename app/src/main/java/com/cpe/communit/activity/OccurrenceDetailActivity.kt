package com.cpe.communit.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cpe.communit.R
import com.cpe.communit.data.Occurrence
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_occurrence_detail.*

class OccurrenceDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var occurrence: Occurrence
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_occurrence_detail)
        intent.getParcelableExtra<Occurrence>(MapsActivity.OCCURRENCE_EXTRA)?.let {
            occurrence = it
            occurrence_detail_description.text = occurrence.description
            Picasso.get().load(occurrence.photo_url).into(occurrence_detail_img_preview)
            occurrence_detail_is_road_problem.isChecked = occurrence.is_road_problem
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.occurrence_detail_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        marker_detail_back_btn.setOnClickListener { onBackPressed() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val location = LatLng(occurrence.lat, occurrence.lng)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f))
        mMap.addMarker(MarkerOptions().position(location))
    }
}