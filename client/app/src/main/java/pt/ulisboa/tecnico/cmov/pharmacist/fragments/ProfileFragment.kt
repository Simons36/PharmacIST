import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.service.PharmacyServiceImpl
import pt.ulisboa.tecnico.cmov.pharmacist.user.service.UserServiceImpl

class ProfileFragment : Fragment() {

    private lateinit var recyclerViewPharmacies: RecyclerView
    private lateinit var pharmacyAdapter: PharmacyAdapter
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private fun getLastKnownLocation(): Task<Location>? {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                1
            )
            return null
        }
        return fusedLocationProviderClient.lastLocation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        recyclerViewPharmacies = view.findViewById(R.id.recyclerViewPharmacies)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Fetch last known location
        getLastKnownLocation()?.addOnSuccessListener { location ->
            val userLocation = location ?: return@addOnSuccessListener
            pharmacyAdapter = PharmacyAdapter(emptyList(), userLocation)
            recyclerViewPharmacies.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewPharmacies.adapter = pharmacyAdapter

            // Fetch favorite pharmacies
            fetchFavoritePharmacies(userLocation)
        }
    }

    private fun fetchFavoritePharmacies(userLocation: Location) {
        lifecycleScope.launch {
            try {
                val favorites = UserServiceImpl.getFavorites(requireContext())
                val pharmacyDtoList = mutableListOf<PharmacyDto>()
                for (pharmacyName in favorites) {
                    try {
                        // Fetch PharmacyDto for each favorite pharmacy name
                        val pharmacyDto = PharmacyServiceImpl.getPharmacyByName(pharmacyName, requireContext())
                        pharmacyDtoList.add(pharmacyDto)
                    } catch (e: Exception) {
                        // Handle error fetching PharmacyDto for a specific pharmacy name
                        Log.e("FetchPharmacyError", "Error fetching pharmacy for name: $pharmacyName, Error: ${e.message}")
                    }
                }
                // Update the RecyclerView adapter with the fetched list of PharmacyDto
                pharmacyAdapter.updatePharmacies(pharmacyDtoList)
            } catch (e: Exception) {
                // Handle error, e.g., show a toast or log the error
                e.printStackTrace()
            }
        }
    }
}