package pt.ulisboa.tecnico.cmov.pharmacist.map

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.Task
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import pt.ulisboa.tecnico.cmov.pharmacist.fragments.MapFragment

class LocationService(
    private val context: Context,
    private val mapHelper: MapHelper,
    private val mapFragment: MapFragment
) : LocationListener {

    private val MIN_TIME = 60000L // 60 seconds for updating location
    private val MIN_DISTANCE = 90f // 90 meters for updating location
    private val FINE_PERMISSION_CODE = 1


    private lateinit var locationManager : LocationManager;
    private var lastKnownLocation: Location? = null;

    private val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    init {
        setupLocationManager(context as Activity, mapFragment)
    }

    private fun setupLocationManager(activity : Activity, fragment : MapFragment){
        // Initialize locationManager using getSystemService
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
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
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )

            fragment.onRequestPermissionsResult(FINE_PERMISSION_CODE, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), intArrayOf(PackageManager.PERMISSION_GRANTED))
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this)
    }


    private fun startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME,
                MIN_DISTANCE,
                this
            )
            val task: Task<Location>? = fusedLocationProviderClient.lastLocation
            task?.addOnSuccessListener { location ->
                lastKnownLocation = location
            }
        } catch (ex: SecurityException) {
            Log.e("LocationManagerHelper", "Error requesting location updates: ${ex.message}")
        }
    }

    public fun fetchLastKnownLocation() : Task<Location>?{
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_PERMISSION_CODE
            )
            return null;
        }

        return fusedLocationProviderClient.lastLocation
    }

    fun getLastKnownLocation() : Location? {
        return this.lastKnownLocation;
    }

    fun setLastKnownLocation(location: Location){
        this.lastKnownLocation = location;
    }

    override fun onLocationChanged(location: Location) {
        // Handle location change
        lastKnownLocation = location
        mapHelper.moveCurrentLocationMarker(location)
        mapHelper.moveCamera(location, 15f)
    }

    fun stopLocationUpdates() {
        locationManager.removeUpdates(this)
    }
}
