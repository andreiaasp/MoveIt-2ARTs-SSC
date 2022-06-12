package com.ipleiria.moveit.activity

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.GeoApiContext
import com.google.maps.PlacesApi
import com.google.maps.model.PlaceType
import com.google.maps.model.PlacesSearchResponse
import com.ipleiria.moveit.R
import com.ipleiria.moveit.constants.ProjectConstant
import com.ipleiria.moveit.databinding.MapViewBinding
import com.ipleiria.moveit.models.GooglePlace
import com.ipleiria.moveit.models.PhotoGooglePlace
import java.io.IOException
import java.net.URL


class Map: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapBinding: MapViewBinding
    private lateinit var googlePlaceList: ArrayList<GooglePlace>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isLocationPermissionOk = false
    private lateinit var supportMap : SupportMapFragment
    private var mGoogleMap: GoogleMap? = null
    private var permissionToRequest = mutableListOf<String>()
    private lateinit var currentLocation: Location
    private lateinit var duration: Number
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mapBinding = MapViewBinding.inflate(layoutInflater)

        setContentView(mapBinding.root)

        val bundle :Bundle? = intent.extras

        firebaseAuth = Firebase.auth
        googlePlaceList = ArrayList()

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionOk =
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                            && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true

                if (isLocationPermissionOk)
                    setUpGoogleMap()
                else
                    Snackbar.make(mapBinding.root, "Location Permission Denied", Snackbar.LENGTH_LONG)
                        .show()

            }

        supportMap = supportFragmentManager.findFragmentById(R.id.homeMap) as SupportMapFragment
        supportMap.getMapAsync(this)

        for (place in ProjectConstant.placesName) {
            val chip = Chip(this)
            chip.text = place.name
            chip.id = place.id
            chip.setPadding(8, 8, 8, 8)
            chip.setTextColor(resources.getColor(R.color.white, null))
            chip.chipBackgroundColor = resources.getColorStateList(R.color.primary, null)
            chip.chipIcon = ResourcesCompat.getDrawable(resources, place.drawableId, null)
            chip.isCheckable = true
            chip.isCheckedIconVisible = false
            mapBinding.placesGroup.addView(chip)
        }

        mapBinding.placesGroup.setOnCheckedChangeListener { _, checkedId ->

            if (checkedId != -1) {
                val place = ProjectConstant.placesName[checkedId - 1]
                getNearByPlace(place.placeType)
            }
        }
        //TODO setUpRecyclerView()

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        when {
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                isLocationPermissionOk = true
                setUpGoogleMap()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission")
                    .setMessage("2ARTs MoveIt required location permission to access your location")
                    .setPositiveButton("Ok") { _, _ ->
                        requestLocation()
                    }.create().show()
            }

            else -> {
                requestLocation()
            }
        }
    }

    private fun requestLocation() {
        permissionToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    private fun setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        mGoogleMap?.isMyLocationEnabled = true
        mGoogleMap?.uiSettings?.isTiltGesturesEnabled = true
        //mGoogleMap?.setOnMarkerClickListener(this)

        setUpLocationUpdate()
    }

    private fun setUpLocationUpdate() {

        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)

                for (location in locationResult?.locations!!) {
                    Log.d("TAG", "onLocationResult: ${location.longitude} ${location.latitude}")
                }
            }
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Location update start", Toast.LENGTH_SHORT).show()
            }
        }

        getCurrentLocation()
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        } else {
            fusedLocationProviderClient?.lastLocation?.addOnCompleteListener {
                currentLocation = it.result
                if (currentLocation != null) {
                    val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    mGoogleMap?.addMarker(
                        MarkerOptions().position(latLng)
                            .title("You are here!"))
                    val update = CameraUpdateFactory.newLatLngZoom(latLng, 16.0f)
                    mGoogleMap?.moveCamera(update)
                } else {
                    Log.e(TAG, "No location found")
                }
            }
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
        Log.d("TAG", "stopLocationUpdates: Location Update Stop")
    }

    private fun getNearByPlace(placeType: String) {

        var request = PlacesSearchResponse()
        val context: GeoApiContext = GeoApiContext.Builder()
            .apiKey("AIzaSyB0z8IJvOz5S_uEAF6EldQbJ88GJzGO1QI")
            .build()
        val location = LatLng(currentLocation.latitude, currentLocation.longitude)

        try {
            request = PlacesApi.nearbySearchQuery(context, convertCoordType(location))
                .radius(2000)
                .type(placeType as PlaceType)
                .await()

            if (request.results != null && request.results.size > 0) {
                googlePlaceList.clear()
                mGoogleMap?.clear()

                for (r in request.results) {
                    for (i in request.results.indices){
                        val details = PlacesApi.placeDetails(context, r.placeId).await()
                        val name = details.name
                        val icon: URL = details.icon
                        val lat = details.geometry.location.lat
                        val lng = details.geometry.location.lng
                        val photos = details.photos as List<PhotoGooglePlace>
                        val vicinity = details.vicinity
                        val placeId = details.placeId
                        val rating = details.rating
                        val scope = details.scope.toString()
                        val types = details.types as List<String>

                        val place = GooglePlace(lat,lng,icon.toString(),name,photos, placeId,rating,scope,types,vicinity)
                        googlePlaceList.add(place)
                        addMarker(place, i)
                    }

                }
            }else {
                mGoogleMap?.clear()
                googlePlaceList.clear()

            }
        } catch (e: ApiException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            Log.d("TAG", googlePlaceList.toString())
        }

    }

    private fun addMarker(googlePlaceModel: GooglePlace, position: Int) {
        val markerOptions = MarkerOptions()
            .position(
                LatLng(
                    googlePlaceModel.lat,
                    googlePlaceModel.lng
                )
            )
            .title(googlePlaceModel.name)
            .snippet(googlePlaceModel.vicinity)

        //markerOptions.icon(getCustomIcon())
        mGoogleMap?.addMarker(markerOptions)?.tag = position

    }

    fun convertCoordType(list: LatLng): com.google.maps.model.LatLng? {
        val result = com.google.maps.model.LatLng(list.latitude, list.longitude)
        return result
    }


}