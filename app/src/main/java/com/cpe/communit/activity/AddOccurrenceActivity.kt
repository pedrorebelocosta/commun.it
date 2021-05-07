package com.cpe.communit.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.cpe.communit.R
import com.cpe.communit.data.Occurrence
import com.cpe.communit.data.UploadResponse
import com.cpe.communit.service.EndPoints
import com.cpe.communit.service.ServiceBuilder
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_add_note.*
import kotlinx.android.synthetic.main.activity_add_occurrence.*
import kotlinx.android.synthetic.main.activity_edit_note.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

class AddOccurrenceActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private lateinit var currentPhotoUrl: String
    private lateinit var mMap: GoogleMap

    private var currentPhotoPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_occurrence)
        add_occurrence_back_button.setOnClickListener { onBackPressed() }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.add_occurrence_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        locationCallback = object: LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val lastLocation = locationResult.lastLocation
                val loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                // val markerOptions = MarkerOptions().position(loc)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
            }
        }
        add_occurrence_save_btn.setOnClickListener {
            if (TextUtils.isEmpty(add_occurrence_description.text)) {
                add_occurrence_description_layout.error = getString(R.string.add_occurrence_error_label)
            }
            if (!TextUtils.isEmpty(add_occurrence_description.text) && currentPhotoPath != null) {
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val location = mMap.cameraPosition.target
                val newOccurrence = Occurrence(
                    description = add_occurrence_description.text.toString(),
                    lat = location.latitude,
                    lng = location.longitude,
                    photo_url = currentPhotoUrl,
                    is_road_problem = add_occurrence_is_road_problem.isChecked
                )
                val call = request.createOccurrence(newOccurrence)
                call.enqueue(object: Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(this@AddOccurrenceActivity, "Successfully inserted a new occurrence", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("AddOccurrenceActivity", "Failed to create new occurrence ${t.message}")
                    }
                })
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val portugal = LatLng(40.146212804514626, -8.095561077725725)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(portugal))
        mMap.isMyLocationEnabled = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            updateImageView()
            uploadPhoto()
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create()
        locationRequest.interval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun uploadPhoto() {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        var photo: MultipartBody.Part? = null
        if (currentPhotoPath != null) {
            val file: File = File(this.currentPhotoPath!!)
            val body: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
            photo = MultipartBody.Part.createFormData("photo", file.name, body)
            val call = request.upload(photo)
            call.enqueue(object: Callback<UploadResponse> {
                override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                    if (response.isSuccessful) {
                        currentPhotoUrl = response.body()!!.photo_url
                        add_occurrence_save_btn.isEnabled = true
                    }
                }
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    Log.e("AddOccurrenceActivity", "Failed to upload image, ${t.message}")
                }
            })
        }
    }

    fun takePhoto(view: View) {
        dispatchTakePictureIntent()
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun updateImageView() {
        // Get the dimensions of the View
        val targetW: Int = occurrence_img_preview.width
        val targetH: Int = occurrence_img_preview.height

        // Get the dimensions of the bitmap
        val bmOptions = BitmapFactory.Options()
        bmOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        val photoW = bmOptions.outWidth
        val photoH = bmOptions.outHeight

        // Determine how much to scale down the image
        val scaleFactor = max(1, min(photoW / targetW, photoH / targetH))

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false
        bmOptions.inSampleSize = scaleFactor
        bmOptions.inPurgeable = true
        val bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions)
        occurrence_img_preview.setImageBitmap(bitmap)
        add_occurrence_photo_btn.visibility = View.GONE
        add_photo_prompt_warning.visibility = View.GONE
    }

    private companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
        const val REQUEST_IMAGE_CAPTURE = 2
    }
}