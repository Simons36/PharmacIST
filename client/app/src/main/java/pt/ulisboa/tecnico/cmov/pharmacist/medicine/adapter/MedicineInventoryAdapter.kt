package pt.ulisboa.tecnico.cmov.pharmacist.medicine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.DisplayMedicineDto

class MedicineInventoryAdapter(
    private var medicines: List<DisplayMedicineDto>,
    private val onItemClick: (DisplayMedicineDto) -> Unit
) : RecyclerView.Adapter<MedicineInventoryAdapter.MedicineViewHolder>() {

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.medicineNameTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.medicineQuantityTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.medicine_inventory_item, parent, false)
        return MedicineViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.nameTextView.text = medicine.name
        val text = "Quantity: ${medicine.quantity}"
        holder.quantityTextView.text = text

        holder.itemView.setOnClickListener {
            onItemClick(medicine)
        }
    }

    override fun getItemCount(): Int = medicines.size

    fun updateMedicines(newMedicines: List<DisplayMedicineDto>) {
        medicines = newMedicines
        notifyDataSetChanged()
    }
}
