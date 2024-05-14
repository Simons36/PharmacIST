package pt.ulisboa.tecnico.cmov.pharmacist.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions

class MapFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private val MIN_TIME = 60000L; //60 seconds for updating location
    private val MIN_DISTANCE = 90f; //1000 meters for updating location

    private val FINE_PERMISSION_CODE = 1
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var lastKnownLocation: Location
    private var currentLocationMarker : Marker? = null

    // For updating location when the user moves
    private  lateinit var locationManager : LocationManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize the map fragment
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        Log.i("MapFragment", "MapFragment DEBUG")

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Initialize locationManager using getSystemService
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )

            onRequestPermissionsResult(FINE_PERMISSION_CODE, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), intArrayOf(PackageManager.PERMISSION_GRANTED))
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this)

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        setupMap()

    }

    private fun setupMap() {
        val taskLocation = getLastKnownLocation()

        taskLocation?.addOnSuccessListener { location ->
            if (location != null) {
                lastKnownLocation = location
                addMarkerToCurrentLocation()
                moveCameraToLocation(lastKnownLocation)

            }
        }
    }

    private fun getLastKnownLocation() : Task<Location>?{
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
            return null;
        }

        return fusedLocationProviderClient.lastLocation
    }

    private fun addMarkerToCurrentLocation() {
        val drawable =
            ResourcesCompat.getDrawable(resources, R.drawable.current_location_marker, null)
                ?: return

        // Resize the bitmap to desired dimensions
        val scaledBitmap = Bitmap.createScaledBitmap(UtilFunctions().drawableToBitmap(drawable), 80, 80, false)

        // Create a BitmapDescriptor from the resized bitmap
        val icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

        this.currentLocationMarker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude))
                .icon(icon)
                .title("Current Location")
        )
    }

    private fun moveCameraToLocation(location: Location){
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastKnownLocation()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        lastKnownLocation = location
        currentLocationMarker?.position = LatLng(location.latitude, location.longitude)
        moveCameraToLocation(lastKnownLocation)
    }
}