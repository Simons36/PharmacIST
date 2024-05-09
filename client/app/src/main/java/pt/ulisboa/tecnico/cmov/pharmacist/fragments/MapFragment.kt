package pt.ulisboa.tecnico.cmov.pharmacist.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions

class MapFragment : Fragment(), OnMapReadyCallback {

    private val FINE_PERMISSION_CODE = 1
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize the map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()

        return view
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return
        }

        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                // Handle location found
                currentLocation = location
                // Log location
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                addMarkerToCurrentLocation(latLng)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
    }

    private fun addMarkerToCurrentLocation(latLng: LatLng) {
        // Load the custom bitmap for the marker (blue dot)
        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.current_location_marker, null)
                ?: return

        // Resize the bitmap to desired dimensions
        val scaledBitmap = Bitmap.createScaledBitmap(UtilFunctions().drawableToBitmap(drawable), 64, 64, false)

        // Create a BitmapDescriptor from the resized bitmap
        val icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

        // Add marker to the map with the custom icon
        googleMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(icon)
                .title("Current Location")
        )
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // You can customize the map settings here
        googleMap.uiSettings.isZoomControlsEnabled = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
