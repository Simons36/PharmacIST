package pt.ulisboa.tecnico.cmov.pharmacist.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import pt.ulisboa.tecnico.cmov.pharmacist.AddPharmacyActivity
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.map.LocationService
import pt.ulisboa.tecnico.cmov.pharmacist.map.MapHelper

class MapFragment : Fragment(), OnMapReadyCallback{

    private val MIN_TIME = 60000L; //60 seconds for updating location
    private val MIN_DISTANCE = 90f; //1000 meters for updating location
    private val FINE_PERMISSION_CODE = 1;

    private lateinit var googleMap: GoogleMap

    private lateinit var locationService : LocationService;
    private lateinit var mapHelper : MapHelper;

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

        setupCenterCameraButton(view);
        setupAddPharmacyButton(view);

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        mapHelper = MapHelper(requireContext(), googleMap, resources)
        locationService = LocationService(requireActivity(), mapHelper, this)

        setupMap()

    }

    private fun setupMap() {
        val taskLocation = locationService.fetchLastKnownLocation()

        taskLocation?.addOnSuccessListener { location ->
            if (location != null) {
                locationService.setLastKnownLocation(location)
                mapHelper.addCurrentLocationMarker(LatLng(location.latitude, location.longitude), "Current Location", R.drawable.current_location_marker)
                mapHelper.moveCamera(location, 15f)

            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMap()
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        locationService.stopLocationUpdates()
    }

    private fun setupCenterCameraButton(view: View){
        val centerCurrentLocationButton = view.findViewById<FloatingActionButton>(R.id.centerCurrentLocationButton)
        centerCurrentLocationButton.setOnClickListener {
            if(locationService.getLastKnownLocation() == null){
                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mapHelper.moveCamera(locationService.getLastKnownLocation()!!, 15f)
        }
    }

    private fun setupAddPharmacyButton(view : View){
        val addPharmacyButton = view.findViewById<Button>(R.id.btnAddPharmacy)
        addPharmacyButton.setOnClickListener {
            // Navigate to AddPharmacyActivity
            val intent = Intent(requireContext(), AddPharmacyActivity::class.java)
            startActivity(intent)
        }
    }
}