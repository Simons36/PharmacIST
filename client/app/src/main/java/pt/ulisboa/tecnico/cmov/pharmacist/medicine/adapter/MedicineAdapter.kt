package pt.ulisboa.tecnico.cmov.pharmacist.medicine.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pt.ulisboa.tecnico.cmov.pharmacist.R
import pt.ulisboa.tecnico.cmov.pharmacist.medicine.dto.MedicineDTO

class MedicineAdapter(private var medicines: List<MedicineDTO>) : RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder>() {

    class MedicineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.medicineNameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicineViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.medicine_item, parent, false)
        return MedicineViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicineViewHolder, position: Int) {
        val medicine = medicines[position]
        holder.nameTextView.text = medicine.getName()
    }

    override fun getItemCount(): Int = medicines.size

    fun updateMedicines(newMedicines: List<MedicineDTO>) {
        medicines = newMedicines
        notifyDataSetChanged()
    }
}
