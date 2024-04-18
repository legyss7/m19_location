package com.hw19.presentation.fragments.map

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hw19.R
import com.hw19.databinding.FragmentMapBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var latitudeMap = 0.0
    private var longitudeMap = 0.0
    private var zoomMap = 0.0f


    private lateinit var mapView: MapView
    private lateinit var userLocationLayer: UserLocationLayer
    private var followUserLocation = false
    private var startLocation = Point(0.0, 0.0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.map

        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(binding.map.mapWindow)
        userLocationLayer.isVisible = true

        binding.location.setOnClickListener {
            followUserLocation = true
            if (isLocationServiceEnabled()) {
                cameraUserPosition()
            } else {
                promptGpsActivation()
            }
        }

        binding.plus.setOnClickListener { changeZoom(+ZOOM_STEP) }
        binding.minus.setOnClickListener { changeZoom(-ZOOM_STEP) }

        if (savedInstanceState != null) {
            latitudeMap = savedInstanceState.getDouble(LATITUDE)
            longitudeMap = savedInstanceState.getDouble(LONGITUDE)
            zoomMap = savedInstanceState.getFloat(ZOOM)
            mapView.mapWindow.map.move(
                CameraPosition(
                    Point(latitudeMap, longitudeMap),
                    zoomMap,
                    0f,
                    0f
                )
            )
        }
    }

    private fun changeZoom(value: Float) {
        with(mapView.mapWindow.map.cameraPosition) {
            latitudeMap = target.latitude
            longitudeMap = target.longitude
            zoomMap = zoom + value
            mapView.mapWindow.map.move(
                CameraPosition(target, zoomMap, azimuth, tilt),
                SMOOTH_ANIMATION,
                null
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putDouble(LATITUDE, latitudeMap)
        outState.putDouble(LONGITUDE, longitudeMap)
        outState.putFloat(ZOOM, zoomMap)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun cameraUserPosition() {
        val cameraPosition = userLocationLayer.cameraPosition()
        if (cameraPosition != null && followUserLocation) {
            startLocation = cameraPosition.target
            latitudeMap = startLocation.latitude
            longitudeMap = startLocation.longitude
            zoomMap = 16f
            mapView.mapWindow.map.move(
                CameraPosition(startLocation, zoomMap, 0f, 0f),
                Animation(Animation.Type.SMOOTH, 1f),
                null
            )
        }
    }

    private fun promptGpsActivation() {
        if (!isLocationServiceEnabled()) {
            AlertDialog.Builder(requireContext())
                .setMessage(getString(R.string.ask_turn_GPS))
                .setPositiveButton(
                    R.string.open_location
                ) { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
                .setNegativeButton(R.string.exit) { _, _ ->
                    parentFragmentManager.popBackStack()
                }
                .show()
        }
    }

    private fun isLocationServiceEnabled(): Boolean {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
        }
        return gpsEnabled && networkEnabled
    }

    companion object {
        private const val LATITUDE = "latitude"
        private const val LONGITUDE = "longitude"
        private const val ZOOM = "zoom"
        private const val ZOOM_STEP = 1f
        private val SMOOTH_ANIMATION = Animation(Animation.Type.SMOOTH, 0.4f)
    }
}
