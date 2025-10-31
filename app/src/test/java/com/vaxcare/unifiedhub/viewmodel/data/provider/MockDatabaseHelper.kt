package com.vaxcare.unifiedhub.viewmodel.data.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MockDatabaseHelper(
    context: Context
) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_DB_TABLE)
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "BluDeviceDb"
        private const val DATABASE_VERSION = 1

        const val TABLE_NAME = "DeviceInfo"
        const val DEVICE_INFO_ID = "_id"
        const val DEVICE_INFO_SERIAL = "serial"
        const val DEVICE_INFO_IMEI = "imei"
        const val DEVICE_INFO_ICCID = "iccid"

        private const val CREATE_DB_TABLE = (
            "CREATE TABLE $TABLE_NAME (" +
                "$DEVICE_INFO_ID INTEGER NOT NULL PRIMARY KEY," +
                "$DEVICE_INFO_SERIAL TEXT NOT NULL," +
                "$DEVICE_INFO_IMEI TEXT NOT NULL," +
                "$DEVICE_INFO_ICCID TEXT NOT NULL);"
        )
    }
}
