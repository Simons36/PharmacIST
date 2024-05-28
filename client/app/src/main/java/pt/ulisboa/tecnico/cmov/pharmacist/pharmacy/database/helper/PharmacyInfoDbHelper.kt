package pt.ulisboa.tecnico.cmov.pharmacist.pharmacy.database.helper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * This class will serve as an API to access the SQL lite database that will store
 * information about pharmacies (acting as a cache for data received from server)
 */

class PharmacyInfoDbHelper(context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }


    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "PharmacyInfo.db"

        private const val SQL_CREATE_ENTRIES =
            "CREATE TABLE Pharmacy (" +
                    "id INTEGER PRIMARY KEY," +
                    "name TEXT," +
                    "address TEXT," +
                    "latitude REAL," +
                    "longitude REAL)"

        private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS Pharmacy"
    }
}
