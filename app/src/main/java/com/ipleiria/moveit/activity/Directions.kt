package com.ipleiria.moveit.activity

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.errors.ApiException
import com.google.maps.model.DirectionsLeg
import com.google.maps.model.TravelMode
import com.ipleiria.moveit.R
import com.ipleiria.moveit.databinding.DirectionsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class Directions: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: DirectionsBinding
    private var mGoogleMap: GoogleMap? = null
    private var isLocationPermissionOk = false
    private var endLat: Double? = null
    private var endLng: Double? = null
    private lateinit var placeId: String
    private var currentLocation: Location? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var permissionToRequest = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DirectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.apply {
            endLat = getDoubleExtra("lat", 0.0)
            endLng = getDoubleExtra("lng", 0.0)
            placeId = getStringExtra("placeId")!!
            currentLocation = getParcelableExtra("currentLocation")
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.directionMap) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        when {
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                isLocationPermissionOk = true
                setUpGoogleMap()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission")
                    .setMessage("2ARTS MoveIt! required location permission to access your location")
                    .setPositiveButton("Ok") { _, _ ->
                        requestLocation()
                    }.create().show()
            }

            else -> {
                requestLocation()
            }
        }
    }

    private fun setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        mGoogleMap?.isMyLocationEnabled = true
        mGoogleMap?.uiSettings?.isTiltGesturesEnabled = true
        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
        mGoogleMap?.uiSettings?.isCompassEnabled = false

       getDirection()

    }

    private fun requestLocation() {
        permissionToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)

        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    private fun getDirection() {
        var legModel: DirectionsLeg? = null
        var summary: String? = null
        if (isLocationPermissionOk) {
            val context: GeoApiContext = GeoApiContext.Builder()
                .apiKey(getString(R.string.API_KEY))
                .build()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val request = DirectionsApi.getDirections(
                        context,
                        currentLocation!!.latitude.toString() + "," + currentLocation!!.longitude.toString(),
                        "place_id:" + placeId
                    )
                        .mode(TravelMode.WALKING).await()

                    for (r in request.routes) {
                        legModel = r.legs?.get(0)
                        summary = r.summary
                    }
                    supportActionBar!!.title = summary
                    binding.apply {
                        if(legModel!=null){
                            txtStartLocation.text = legModel!!.startAddress
                            txtEndLocation.text = legModel!!.endAddress
                        } else {
                            txtStartLocation.text = "ERRO"
                            txtEndLocation.text = "ERRO"
                        }

                    }
                    val stepList: MutableList<LatLng> = ArrayList()

                    val options = PolylineOptions().apply {
                        width(25f)
                        color(Color.BLUE)
                        geodesic(true)
                        clickable(true)
                        visible(true)
                    }

                    val pattern: List<PatternItem> = listOf(
                        Dot(), Gap(10f)
                    )
                    options.jointType(JointType.ROUND)
                    options.pattern(pattern)

                    for (stepModel in legModel!!.steps) {
                        val decodedList = stepModel.polyline.decodePath()
                        for (latLng in decodedList) {
                            stepList.add(
                                LatLng(
                                    latLng.lat,
                                    latLng.lng
                                )
                            )
                        }
                    }
                    options.addAll(stepList)
                    println(options)
                    mGoogleMap?.addPolyline(options)
                    val startLocation = LatLng(
                        legModel!!.startLocation.lat,
                        legModel!!.startLocation.lng
                    )

                    val endLocation = LatLng(
                        legModel!!.endLocation?.lat!!,
                        legModel!!.endLocation.lng
                    )

                    mGoogleMap?.addMarker(
                        MarkerOptions()
                            .position(endLocation)
                            .title("Destino")
                    )

                    mGoogleMap?.addMarker(
                        MarkerOptions()
                            .position(startLocation)
                            .title("Você está aqui!")
                    )

                    val builder = LatLngBounds.builder()
                    builder.include(endLocation).include(startLocation)
                    val latLngBounds = builder.build()


                    mGoogleMap!!.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            latLngBounds, 0
                        )
                    )


                } catch (e: ApiException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                }
            }

        }
    }
}