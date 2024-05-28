package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.service

import android.content.ContentValues
import android.content.Context
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.contract.PharmacyInfoContract.PharmacyInfoEntry
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.helper.PharmacyInfoDbHelper
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

object PharmacyInfoDbServiceImpl : PharmacyInfoDbService{

    override fun getCachedPharmaciesInfo(context: Context): List<PharmacyDto> {
        val dbHelper = PharmacyInfoDbHelper(context).readableDatabase

        val projection = arrayOf(
            PharmacyInfoEntry.COLUMN_NAME_NAME,
            PharmacyInfoEntry.COLUMN_NAME_LATITUDE,
            PharmacyInfoEntry.COLUMN_NAME_LONGITUDE,
            PharmacyInfoEntry.COLUMN_NAME_ADDRESS
        )

        val cursor = dbHelper.query(
            PharmacyInfoEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val pharmacies = mutableListOf<PharmacyDto>()
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_NAME))
                val latitude =
                    getDouble(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_LATITUDE))
                val longitude =
                    getDouble(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_LONGITUDE))
                val address =
                    getString(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_ADDRESS))

                pharmacies.add(PharmacyDto(name, address, latitude, longitude))
            }
        }

        cursor.close()

        return pharmacies
    }

    override fun removePharmacyInfoFromCache(pharmacy: PharmacyDto, context: Context){
        val dbHelper = PharmacyInfoDbHelper(context).writableDatabase

        val selection = "${PharmacyInfoEntry.COLUMN_NAME_NAME} = ?"
        val selectionArgs = arrayOf(pharmacy.name)

        dbHelper.delete(PharmacyInfoEntry.TABLE_NAME, selection, selectionArgs)

    }

    override fun addPharmacyInfoToCache(pharmacy: PharmacyDto, context: Context) {
        val dbHelper = PharmacyInfoDbHelper(context).writableDatabase

        val values = ContentValues().apply {
            put(PharmacyInfoEntry.COLUMN_NAME_NAME, pharmacy.name)
            put(PharmacyInfoEntry.COLUMN_NAME_LATITUDE, pharmacy.latitude)
            put(PharmacyInfoEntry.COLUMN_NAME_LONGITUDE, pharmacy.longitude)
            put(PharmacyInfoEntry.COLUMN_NAME_ADDRESS, pharmacy.address)
        }

        dbHelper.insert(PharmacyInfoEntry.TABLE_NAME, null, values)
    }


}