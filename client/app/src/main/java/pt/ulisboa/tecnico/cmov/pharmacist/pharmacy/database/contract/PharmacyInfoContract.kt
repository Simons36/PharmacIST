package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.contract

import android.provider.BaseColumns

object PharmacyInfoContract {
    object PharmacyInfoEntry : BaseColumns {
        const val TABLE_NAME = "pharmacy_status"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_LATITUDE = "latitude"
        const val COLUMN_NAME_LONGITUDE = "longitude"
        const val COLUMN_NAME_ADDRESS = "address"
    }
}