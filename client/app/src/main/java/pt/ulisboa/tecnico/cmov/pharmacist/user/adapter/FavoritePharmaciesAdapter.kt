import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

class PharmacyAdapter(
    private var pharmacies: List<PharmacyDto>,
    private val userLocation: Location?
) : RecyclerView.Adapter<PharmacyAdapter.ViewHolder>() {

    fun updatePharmacies(newPharmacies: List<PharmacyDto>) {
        pharmacies = newPharmacies
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pharmacy_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pharmacy = pharmacies[position]
        holder.bind(pharmacy, userLocation)
    }

    override fun getItemCount(): Int {
        return pharmacies.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val pharmacyNameTextView: TextView = itemView.findViewById(R.id.pharmacyNameTextView)
        private val pharmacyAddressTextView: TextView = itemView.findViewById(R.id.closestAddressNameTextView)
        private val distanceTextView: TextView = itemView.findViewById(R.id.pharmacyDistanceView)

        fun bind(pharmacy: PharmacyDto, userLocation: Location?) {
            pharmacyNameTextView.text = pharmacy.name
            pharmacyAddressTextView.text = pharmacy.address

            if (userLocation != null) {
                val pharmacyLocation = Location("").apply {
                    latitude = pharmacy.latitude
                    longitude = pharmacy.longitude
                }
                val distanceInMeters = userLocation.distanceTo(pharmacyLocation)
                val distanceInKm = distanceInMeters / 1000
                distanceTextView.text = String.format("%.2f km", distanceInKm)
            } else {
                distanceTextView.text = "Unknown distance"
            }
        }
    }
}