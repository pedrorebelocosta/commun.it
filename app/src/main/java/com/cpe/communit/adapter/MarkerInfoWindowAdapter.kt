package com.cpe.communit.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.cpe.communit.R
import com.cpe.communit.data.Occurrence
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception


class MarkerInfoWindowAdapter(val context: Activity): GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    @SuppressLint("InflateParams")
    override fun getInfoContents(marker: Marker): View? {
        val view = context.layoutInflater.inflate(R.layout.marker_info_window, null)
        // Kotlin Synthetics not working here?
        val titleTxtView = view.findViewById<TextView>(R.id.window_occurrence_title)
        val imgView = view.findViewById<ImageView>(R.id.window_occurrence_image)
        val occurrence = marker.tag as Occurrence
        titleTxtView.text = occurrence.description
        Picasso.get().load(occurrence.photo_url)
                     .placeholder(R.drawable.ic_baseline_downloading_24)
                     .into(imgView, MarkerCallback(marker))
        return view
    }

    class MarkerCallback(private val marker: Marker) : Callback {
        override fun onSuccess() {
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
                marker.showInfoWindow()
            }
        }

        override fun onError(e: Exception?) {
            Log.e("MarkerCallback: ", "Failed to load image from URL")
            e?.printStackTrace()
        }
    }
}