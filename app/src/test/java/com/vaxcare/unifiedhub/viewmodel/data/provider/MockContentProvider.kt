package com.vaxcare.unifiedhub.viewmodel.data.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.util.Log
import com.vaxcare.unifiedhub.viewmodel.data.provider.MockDatabaseHelper.Companion.DEVICE_INFO_ID
import com.vaxcare.unifiedhub.viewmodel.data.provider.MockDatabaseHelper.Companion.TABLE_NAME

class MockContentProvider : ContentProvider() {
    companion object {
        private const val PROVIDER_NAME = "com.vaxcare.mobilebridge.t105"
        private const val URL = "content://com.vaxcare.mobilebridge.t105/datapoints"
        private const val DATA_VAL = 1
        private const val DATA_ID = 2

        val CONTENT_URI: Uri = Uri.parse(URL)
    }

    private var db: SQLiteDatabase? = null

    private val uriMatcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(PROVIDER_NAME, "datapoints", DATA_VAL)
        addURI(PROVIDER_NAME, "datapoints/*", DATA_ID)
    }

    private val values: HashMap<String, String>? = null

    fun populateDummyData() {
        val values = ContentValues().apply {
            put(MockDatabaseHelper.DEVICE_INFO_SERIAL, "FAKE_SERIAL")
            put(MockDatabaseHelper.DEVICE_INFO_IMEI, "FAKE_IMEI")
            put(MockDatabaseHelper.DEVICE_INFO_ICCID, "FAKE_ICCID")
        }
        insert(CONTENT_URI, values)
    }

    override fun getType(uri: Uri): String =
        when (uriMatcher.match(uri)) {
            DATA_VAL -> "vnd.android.cursor.dir/vnd.vaxcare.datapoints"

            DATA_ID -> "vnd.android.cursor.item/vnd.vaxcare.datapoints"

            else -> throw IllegalArgumentException("Unsupported URI: $uri")
        }

    override fun onCreate(): Boolean =
        context?.let {
            db = MockDatabaseHelper(it).writableDatabase

            db != null
        } ?: false

    override fun query(
        uri: Uri,
        projection: Array<String?>?,
        selection: String?,
        selectionArgs: Array<String?>?,
        sortOrder: String?
    ): Cursor? {
        val qb = SQLiteQueryBuilder()
        qb.tables = TABLE_NAME

        when (uriMatcher.match(uri)) {
            DATA_VAL -> qb.projectionMap = values

            DATA_ID -> qb.appendWhere("$DEVICE_INFO_ID = ${uri.pathSegments[1]}")

            else -> throw IllegalArgumentException("Unknown URI $uri")
        }

        val sOrder = if (!sortOrder.isNullOrBlank()) {
            DEVICE_INFO_ID
        } else {
            sortOrder
        }

        val c = qb.query(
            db,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sOrder
        )
        c.setNotificationUri(context?.contentResolver, uri)

        return c
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        values?.put(DEVICE_INFO_ID, 1)

        val rowID = db?.insertWithOnConflict(
            TABLE_NAME,
            "",
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )

        if (rowID != null && rowID > 0) {
            val contentUris = ContentUris.withAppendedId(CONTENT_URI, rowID)

            context?.contentResolver?.notifyChange(contentUris, null)

            Log.i("DataProvider", "Data inserted")

            return contentUris
        }

        Log.i("DataProvider", "Data not inserted")

        throw SQLiteException("Failed to add a record into $uri")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        val count = when (uriMatcher.match(uri)) {
            DATA_VAL -> db?.update(TABLE_NAME, values, selection, selectionArgs) ?: 0

            DATA_ID -> {
                val segment = uri.pathSegments[1]

                val whereClause = "$DEVICE_INFO_ID = $segment" +
                    if (!selection.isNullOrEmpty()) "AND ($selection)" else ""

                db?.update(TABLE_NAME, values, whereClause, selectionArgs) ?: 0
            }

            else -> throw IllegalArgumentException("Unknown URI $uri")
        }

        context?.contentResolver?.notifyChange(uri, null)

        return count
    }

    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String?>?
    ): Int {
        val count = when (uriMatcher.match(uri)) {
            DATA_VAL -> db?.delete(TABLE_NAME, selection, selectionArgs) ?: 0

            DATA_ID -> {
                val segment = uri.pathSegments[1]

                val whereClause = "$DEVICE_INFO_ID = $segment" +
                    if (!selection.isNullOrEmpty()) "AND ($selection)" else ""

                db?.delete(TABLE_NAME, whereClause, selectionArgs) ?: 0
            }

            else -> throw IllegalArgumentException("Unknown URI $uri")
        }

        context?.contentResolver?.notifyChange(uri, null)

        return count
    }
}
