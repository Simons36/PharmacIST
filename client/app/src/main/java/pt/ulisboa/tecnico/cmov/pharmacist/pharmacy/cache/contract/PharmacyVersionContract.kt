package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.cache.contract

import android.provider.BaseColumns

object PharmacyVersionContract {

    object PharmacyVersionEntry : BaseColumns{
        const val TABLE_NAME = "known_version"
        const val COLUMN_NAME_VERSION = "version"
    }

}