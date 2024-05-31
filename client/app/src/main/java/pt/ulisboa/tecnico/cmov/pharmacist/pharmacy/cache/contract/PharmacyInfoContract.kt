package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.cache.contract

import android.provider.BaseColumns

object PharmacyInfoContract {
    object PharmacyInfoEntry : BaseColumns {
        const val TABLE_NAME = "pharmacy_info"
        const val COLUMN_NAME_NAME = "name"
        const val COLUMN_NAME_LATITUDE = "latitude"
        const val COLUMN_NAME_LONGITUDE = "longitude"
        const val COLUMN_NAME_ADDRESS = "address"
        const val COLUMN_NAME_PHOTO_PATH = "photo_path"
        const val COLUMN_NAME_IS_FAVORITE = "is_favorite"
    }
}