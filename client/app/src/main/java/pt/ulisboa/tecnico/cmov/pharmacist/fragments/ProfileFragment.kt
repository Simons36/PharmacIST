package pt.ulisboa.tecnico.cmov.pharmacist.fragments

import PharmacyAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.util.UtilFunctions

class ProfileFragment : Fragment() {

    private var username: String? = null
    private lateinit var recyclerViewPharmacies: RecyclerView
    private lateinit var pharmacyAdapter: PharmacyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        val textViewHelloUser: TextView = view.findViewById(R.id.textViewHelloUser)
        recyclerViewPharmacies = view.findViewById(R.id.recyclerViewPharmacies)

        // Get username from JWT token
        username = UtilFunctions.getUsernameFromJwtToken(requireContext())
        textViewHelloUser.text = "Hello, $username!"

        // Set up RecyclerView
        recyclerViewPharmacies.layoutManager = LinearLayoutManager(requireContext())
        pharmacyAdapter = PharmacyAdapter(emptyList(), userLocation = getUserLocation())
        recyclerViewPharmacies.adapter = pharmacyAdapter

        // Fetch favorite pharmacies
        fetchFavoritePharmacies()
    }

    private fun getUserLocation(): Location {

    }

    private fun fetchFavoritePharmacies() {
        TODO("Not yet implemented")
    }


}
