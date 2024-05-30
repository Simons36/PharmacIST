package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.helper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.contract.PharmacyInfoContract.PharmacyInfoEntry
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.contract.PharmacyVersionContract.PharmacyVersionEntry
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.`interface`.PharmacyInfoDbInterface
import pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.dto.PharmacyDto

/**
 * This class will serve as an API to access the SQL lite database that will store
 * information about pharmacies (acting as a cache for data received from server)
 */

class PharmacyInfoDbHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION),
    PharmacyInfoDbInterface {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_KNOWN_VERSION_TABLE)
        db.execSQL(SQL_CREATE_KNOWN_VERSION_ENTRY)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        db.execSQL(SQL_DELETE_KNOWN_VERSION_ENTRY)
        onCreate(db)
    }

    override fun getCachedPharmaciesInfo(): List<PharmacyDto> {
        val db = this.readableDatabase

        val projection = arrayOf(
            PharmacyInfoEntry.COLUMN_NAME_NAME,
            PharmacyInfoEntry.COLUMN_NAME_LATITUDE,
            PharmacyInfoEntry.COLUMN_NAME_LONGITUDE,
            PharmacyInfoEntry.COLUMN_NAME_ADDRESS
        )

        val cursor = db.query(
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

    override fun removePharmacyInfoFromCache(pharmacy: PharmacyDto){
        val db = this.writableDatabase

        val selection = "${PharmacyInfoEntry.COLUMN_NAME_NAME} = ?"
        val selectionArgs = arrayOf(pharmacy.name)

        db.delete(PharmacyInfoEntry.TABLE_NAME, selection, selectionArgs)

    }

    override fun addPharmacyInfoToCache(pharmacy: PharmacyDto) {
        val db =  this.writableDatabase

        val values = ContentValues().apply {
            put(PharmacyInfoEntry.COLUMN_NAME_NAME, pharmacy.name)
            put(PharmacyInfoEntry.COLUMN_NAME_LATITUDE, pharmacy.latitude)
            put(PharmacyInfoEntry.COLUMN_NAME_LONGITUDE, pharmacy.longitude)
            put(PharmacyInfoEntry.COLUMN_NAME_ADDRESS, pharmacy.address)
        }

        db.insert(PharmacyInfoEntry.TABLE_NAME, null, values)
    }

    override fun getLatestVersion() : Int {
        val db = this.readableDatabase

        val projection = arrayOf(
            PharmacyVersionEntry.COLUMN_NAME_VERSION
        )

        val cursor = db.query(
            PharmacyVersionEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        var version = 0
        with(cursor) {
            while (moveToNext()) {
                version = getInt(getColumnIndexOrThrow(PharmacyVersionEntry.COLUMN_NAME_VERSION))
            }
        }

        cursor.close()

        return version
    }

    override fun setLatestVersion(version : Int){
        val db = this.writableDatabase

        // must delete the previous version and add new one (always one entry)
        db.delete(PharmacyVersionEntry.TABLE_NAME, null, null)

        val values = ContentValues().apply {
            put(PharmacyVersionEntry.COLUMN_NAME_VERSION, version)
        }

        db.insert(PharmacyVersionEntry.TABLE_NAME, null, values)

    }
    fun getNumberOfPharmaciesInCache() : Int{
        val db = this.readableDatabase
        val projection = arrayOf(
            PharmacyInfoEntry.COLUMN_NAME_NAME,
            PharmacyInfoEntry.COLUMN_NAME_LATITUDE,
            PharmacyInfoEntry.COLUMN_NAME_LONGITUDE
        )

        val cursor = db.query(
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
                pharmacies.add(PharmacyDto(name, null, latitude, longitude))
            }
        }

        cursor.close()

        return pharmacies.size
    }

    override fun getPharmacyInfo(pharmacyName: String): PharmacyDto? {
        val db = this.readableDatabase

        val projection = arrayOf(
            PharmacyInfoEntry.COLUMN_NAME_NAME,
            PharmacyInfoEntry.COLUMN_NAME_LATITUDE,
            PharmacyInfoEntry.COLUMN_NAME_LONGITUDE,
            PharmacyInfoEntry.COLUMN_NAME_ADDRESS
        )

        val selection = "${PharmacyInfoEntry.COLUMN_NAME_NAME} = ?"
        val selectionArgs = arrayOf(pharmacyName)

        val cursor = db.query(
            PharmacyInfoEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var pharmacy : PharmacyDto? = null
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_NAME))
                val latitude =
                    getDouble(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_LATITUDE))
                val longitude =
                    getDouble(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_LONGITUDE))
                val address =
                    getString(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_ADDRESS))

                pharmacy = PharmacyDto(name, address, latitude, longitude)
            }
        }

        cursor.close()

        return pharmacy
    }

    override fun getPharmacyPhotoPath(pharmacyName: String) : String?{
        val db = this.readableDatabase

        val projection = arrayOf(
            PharmacyInfoEntry.COLUMN_NAME_PHOTO_PATH
        )

        val selection = "${PharmacyInfoEntry.COLUMN_NAME_NAME} = ?"
        val selectionArgs = arrayOf(pharmacyName)

        val cursor = db.query(
            PharmacyInfoEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        var photoPath : String? = null
        with(cursor) {
            while (moveToNext()) {
                photoPath = getString(getColumnIndexOrThrow(PharmacyInfoEntry.COLUMN_NAME_PHOTO_PATH))
            }
        }

        cursor.close()

        return photoPath
    }

    override fun addPharmacyPhoto(pharmacyName: String, photoPath: String) {
        val db = this.writableDatabase

        val values = ContentValues().apply {
            put(PharmacyInfoEntry.COLUMN_NAME_PHOTO_PATH, photoPath)
        }

        val selection = "${PharmacyInfoEntry.COLUMN_NAME_NAME} = ?"
        val selectionArgs = arrayOf(pharmacyName)

        db.update(PharmacyInfoEntry.TABLE_NAME, values, selection, selectionArgs)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "PharmacyInfo.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE pharmacy_info (" +
                    "${PharmacyInfoEntry.COLUMN_NAME_NAME} TEXT PRIMARY KEY," +
                    "${PharmacyInfoEntry.COLUMN_NAME_ADDRESS} TEXT," +
                    "${PharmacyInfoEntry.COLUMN_NAME_LATITUDE} REAL," +
                    "${PharmacyInfoEntry.COLUMN_NAME_LONGITUDE} REAL," +
                    "${PharmacyInfoEntry.COLUMN_NAME_PHOTO_PATH} TEXT)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${PharmacyInfoEntry.TABLE_NAME}"

        private const val SQL_CREATE_KNOWN_VERSION_TABLE =
            "CREATE TABLE ${PharmacyVersionEntry.TABLE_NAME} (" +
                    "${PharmacyVersionEntry.COLUMN_NAME_VERSION} INTEGER)"

        private const val SQL_CREATE_KNOWN_VERSION_ENTRY = "INSERT INTO ${PharmacyVersionEntry.TABLE_NAME} VALUES (0)"

        private const val SQL_DELETE_KNOWN_VERSION_ENTRY = "DROP TABLE IF EXISTS ${PharmacyVersionEntry.TABLE_NAME}"
    }
}
