package pt.ulisboa.tecnico.cmov.pharmacist.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.AddPharmacyActivity
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.map.LocationService
import pt.ulisboa.tecnico.cmov.pharmacist.map.MapHelper
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.helper.PharmacyInfoDbHelper
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.response.UpdatePharmaciesStatusResponse
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service.PharmacyServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.util.MapOpeningMode

class MapFragment : Fragment(), OnMapReadyCallback{

    private val MIN_TIME = 60000L; //60 seconds for updating location
    private val MIN_DISTANCE = 90f; //1000 meters for updating location
    private val FINE_PERMISSION_CODE = 1;

    // ID for the marker that will be placed when the user picks a location
    // We need this name to then remove the marker when user confirms or cancels operation
    private val PICK_LOCATION_MARKER_ID = "new_pharmacy_location";

    private lateinit var googleMap: GoogleMap

    private lateinit var locationService : LocationService;
    private lateinit var mapHelper : MapHelper;

    // Widgets
    private lateinit var centerCurrentLocationButton : FloatingActionButton
    private lateinit var addPharmacyButton : Button
    private lateinit var cancelButton : Button
    private lateinit var pickLocationButton : Button

    private var isForPickLocation = false;

    // This is a variable to check if the user has picked a location
    // (so he doesnt place more than one markers)
    private var hasPickedLocation = false;
    private var pickedLocation : LatLng? = null;

    // To communicate with cache
    private lateinit var pharmacyCache : PharmacyInfoDbHelper

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
        setupCancelButton(view);
        setupPickLocationButton(view);

        if(arguments != null){

            val mapMode = arguments?.getInt("mapMode")
            if(mapMode == MapOpeningMode.MAP_PICK_LOCATION_MODE){
                isForPickLocation = true
            }
        }

        pharmacyCache = PharmacyInfoDbHelper(requireContext())

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        mapHelper = MapHelper(requireContext(), googleMap, resources)
        locationService = LocationService(requireActivity(), mapHelper, this)

        setupMap()

        updatePharmacies()

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

        if(isForPickLocation){
            flipToPickLocationMode()
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
        centerCurrentLocationButton = view.findViewById<FloatingActionButton>(R.id.centerCurrentLocationButton)
        centerCurrentLocationButton.setOnClickListener {
            if(locationService.getLastKnownLocation() == null){
                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            mapHelper.moveCamera(locationService.getLastKnownLocation()!!, 15f)
        }
    }

    private fun setupAddPharmacyButton(view : View){
        addPharmacyButton = view.findViewById<Button>(R.id.btnAddPharmacy)
        addPharmacyButton.setOnClickListener {
            // Navigate to AddPharmacyActivity
            val intent = Intent(requireContext(), AddPharmacyActivity::class.java)
            intent.putExtra("lastKnownLocation", locationService.getLastKnownLocation())
            startActivity(intent)

        }
    }

    private fun setupCancelButton(view : View){
        cancelButton = view.findViewById<Button>(R.id.btnCancel)
        cancelButton.setOnClickListener {

        }
    }

    private fun setupPickLocationButton(view : View){
        pickLocationButton = view.findViewById(R.id.pickLocationButton)
        pickLocationButton.setOnClickListener {
            if(!pickLocationButton.isActivated){
                Toast.makeText(requireContext(), "Please click on the map to pick a location",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                pickedLocation?.let {
                    val data = Intent()
                    data.putExtra("latitude", pickedLocation!!.latitude)
                    data.putExtra("longitude", pickedLocation!!.longitude)
                    activity?.setResult(Activity.RESULT_OK, data)
                    activity?.finish()
                }
            }
        }
        checkIfPickLocationButtonShouldBeEnabled()
    }


    fun flipToPickLocationMode() {

        //centerCurrentLocationButton.visibility = View.GONE
        addPharmacyButton.visibility = View.GONE

        cancelButton.visibility = View.VISIBLE
        pickLocationButton.visibility = View.VISIBLE

        // When user clicks map, add a marker to the map
        googleMap.setOnMapClickListener { latLng ->
            if(!hasPickedLocation){
                mapHelper.addDefaultMarker(latLng, "New Pharmacy Location", PICK_LOCATION_MARKER_ID)
                hasPickedLocation = true
            }else{
                mapHelper.moveDefaultMarker(PICK_LOCATION_MARKER_ID, latLng)
            }

            pickedLocation = latLng

            checkIfPickLocationButtonShouldBeEnabled()
        }

        // When user clicks the cancel button, finish the activity
        cancelButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun checkIfPickLocationButtonShouldBeEnabled(){
        if(hasPickedLocation){
            pickLocationButton.isActivated = true
            pickLocationButton.backgroundTintList = resources.getColorStateList(R.color.colorPrimary)
        }else{
            pickLocationButton.isActivated = false
            pickLocationButton.backgroundTintList = resources.getColorStateList(android.R.color.darker_gray)
        }
    }

    private fun updatePharmacies(){
        var updatePharmacyInfoResponse : UpdatePharmaciesStatusResponse? = null

        lifecycleScope.launch {

            val syncJob = async {
                syncMapWithCache()
            }

            try {
                updatePharmacyInfoResponse = PharmacyServiceImpl.syncPharmacyInfo(
                    pharmacyCache.getLatestVersion(),
                    requireContext()
                )
            } catch (e: RuntimeException) {
                Toast.makeText(
                    requireContext(),
                    "Error updating pharmacies, you might be seeing outdated results",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
                return@launch
            }

            val removeList = updatePharmacyInfoResponse!!.remove
            val addList = updatePharmacyInfoResponse!!.add

            for(pharmacy in addList){
                Log.i("DEBUG", "Pharmacy to add: ${pharmacy.name}")
            }

            // Wait for the syncJob to finish
            syncJob.await()

            // Update map and cache
            for (pharmacy in removeList) {
                mapHelper.removeDefaultMarker(pharmacy.name)
                pharmacyCache.removePharmacyInfoFromCache(pharmacy)
            }

            for (pharmacy in addList) {
                mapHelper.addDefaultMarker(
                    LatLng(pharmacy.latitude, pharmacy.longitude),
                    pharmacy.name,
                    pharmacy.name
                )
                pharmacyCache.addPharmacyInfoToCache(pharmacy)
            }

            //Write version to database
            pharmacyCache.setLatestVersion(updatePharmacyInfoResponse!!.version)
        }


    }

    private fun syncMapWithCache(){
        val pharmacies = pharmacyCache.getCachedPharmaciesInfo()
        for(pharmacy in pharmacies){
            mapHelper.addDefaultMarker(LatLng(pharmacy.latitude, pharmacy.longitude), pharmacy.name, pharmacy.name)
        }
    }

    companion object{
        fun newInstance(mapModeValue : Int): MapFragment {
            val f = MapFragment()
            // Supply index input as an argument.
            val args = Bundle()
            args.putInt("mapMode", mapModeValue)
            f.setArguments(args)
            return f
        }
    }


}
