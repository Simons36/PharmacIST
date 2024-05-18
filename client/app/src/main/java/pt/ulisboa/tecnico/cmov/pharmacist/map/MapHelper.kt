package pt.ulisboa.tecnico.cmov.pharmacist.map

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.location.Location
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.exception.ErrorAddingMarkerException
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions

class MapHelper(private val context: Context, private val googleMap: GoogleMap, private val resources: Resources) {

    private var currentLocationMarker : Marker? = null

    private var defaultMarkersMap = mutableMapOf<String, Marker>()

    fun addCurrentLocationMarker(location: LatLng, title: String, drawableId : Int) {
        val drawable =
            ResourcesCompat.getDrawable(resources, drawableId, null)
                ?: return

        // Resize the bitmap to desired dimensions
        val scaledBitmap = Bitmap.createScaledBitmap(UtilFunctions.drawableToBitmap(drawable), 80, 80, false)

        // Create a BitmapDescriptor from the resized bitmap
        val icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

        this.currentLocationMarker = googleMap.addMarker(
            MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .icon(icon)
                .title("Current Location")
        )
    }

    fun moveCurrentLocationMarker(location: Location) {
        currentLocationMarker?.position = LatLng(location.latitude, location.longitude)
    }

    fun moveCamera(location: Location, zoomLevel: Float) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), zoomLevel))
    }

    fun addDefaultMarker(latLng: LatLng, title: String, id : String) {
        val marker = googleMap.addMarker(MarkerOptions().position(latLng).title(title)) ?: throw ErrorAddingMarkerException()
        defaultMarkersMap[id] = marker
    }

    fun moveDefaultMarker(id: String, latLng: LatLng) {
        defaultMarkersMap[id]?.position = latLng
    }

    // Add more map-related functions as needed...
}