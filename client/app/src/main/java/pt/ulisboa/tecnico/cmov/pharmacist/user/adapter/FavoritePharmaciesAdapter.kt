import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

class PharmacyAdapter(private val pharmacies: List<PharmacyDto>, private val userLocation: Location) : RecyclerView.Adapter<PharmacyAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pharmacy_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pharmacy = pharmacies[position]
        holder.bind(pharmacy)
    }

    override fun getItemCount(): Int {
        return pharmacies.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val medicineNameTextView: TextView = itemView.findViewById(R.id.medicineNameTextView)
        private val closestPharmacyNameTextView: TextView = itemView.findViewById(R.id.closestPharmacyNameTextView)
        private val pharmacyAddressView: TextView = itemView.findViewById(R.id.pharmacyAddressView)

        fun bind(pharmacy: PharmacyDto) {
            medicineNameTextView.text = pharmacy.name
            closestPharmacyNameTextView.text = pharmacy.address

            // Calculate distance here using userLocation and pharmacy's address
            val distance = pharmacy.address?.let { calculateDistance(userLocation, it) }
            pharmacyAddressView.text = distance.toString() // Convert distance to string
        }

        // Function to calculate distance between userLocation and pharmacy's address
        private fun calculateDistance(userLocation: Location, pharmacyAddress: String): Double {
            // Implement your logic to calculate distance
            // You can use libraries like Google Maps API or implement your own logic here
            // For demonstration purposes, let's assume a constant distance for now
            return 5.0 // Return a constant value for demonstration
        }
    }
}
