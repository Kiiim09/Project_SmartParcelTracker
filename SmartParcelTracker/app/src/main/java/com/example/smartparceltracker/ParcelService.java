package com.example.smartparceltracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ParcelService {
    private static final String TAG = "ParcelService";
    private final DatabaseHelper dbHelper;

    public ParcelService(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    /**
     * Add a new parcel to the database.
     */
    public boolean addParcel(
            String trackingNumber,
            String status,
            String senderName,
            String senderPhone,
            String senderPostcode,
            String senderAddress,
            String recipientName,
            String recipientPhone,
            String recipientPostcode,
            String recipientAddress) {

        // Validate inputs
        if (isAnyStringNullOrEmpty(trackingNumber, status, senderName, senderPhone, senderPostcode,
                senderAddress, recipientName, recipientPhone, recipientPostcode, recipientAddress)) {
            Log.e(TAG, "Invalid input for adding parcel.");
            return false;
        }

        SQLiteDatabase db = null;
        long result = -1;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("tracking_number", trackingNumber);
            values.put("status", status);
            values.put("sender_name", senderName);
            values.put("sender_phone", senderPhone);
            values.put("sender_postcode", senderPostcode);
            values.put("sender_address", senderAddress);
            values.put("recipient_name", recipientName);
            values.put("recipient_phone", recipientPhone);
            values.put("recipient_postcode", recipientPostcode);
            values.put("recipient_address", recipientAddress);

            result = db.insert("parcels", null, values);
        } catch (SQLException e) {
            Log.e(TAG, "Database insert operation failed: ", e);
        } finally {
            if (db != null) db.close();
        }

        return result != -1;
    }

    /**
     * Fetch all parcels from the database.
     */
    public Cursor viewParcels() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            return db.rawQuery(
                    "SELECT _id, tracking_number, status, sender_name, sender_phone, sender_postcode, sender_address, " +
                            "recipient_name, recipient_phone, recipient_postcode, recipient_address FROM parcels",
                    null
            );
        } catch (SQLException e) {
            Log.e(TAG, "Error fetching parcels: ", e);
            return null;
        }
    }

    /**
     * Fetch a specific parcel by its ID.
     */
    public Cursor getParcelById(int id) {
        if (id <= 0) {
            Log.e(TAG, "Invalid ID for fetching parcel.");
            return null;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM parcels WHERE _id = ?",
                    new String[]{String.valueOf(id)}
            );
            if (cursor != null && cursor.moveToFirst()) {
                return cursor;
            } else {
                Log.e(TAG, "Parcel not found for ID: " + id);
                return null;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error fetching parcel by ID: ", e);
            return null;
        }
    }

    /**
     * Update the status of a parcel by its tracking number.
     */
    public boolean updateParcelByTrackingNumber(String trackingNumber, String status) {
        if (status == null || trackingNumber == null || trackingNumber.isEmpty()) {
            Log.e(TAG, "Invalid input for updating parcel.");
            return false;
        }

        SQLiteDatabase db = null;
        int rowsUpdated = 0;

        try {
            db = dbHelper.getWritableDatabase();

            // Debug log to check the tracking number
            Log.d(TAG, "Attempting to update parcel with tracking number: " + trackingNumber);

            // Check if the parcel exists
            Cursor cursor = db.rawQuery(
                    "SELECT _id FROM parcels WHERE tracking_number = ?",
                    new String[]{trackingNumber}
            );

            if (cursor != null && cursor.moveToFirst()) {
                // Parcel found, proceed with update
                ContentValues values = new ContentValues();
                values.put("status", status);

                // Update the parcel's status based on tracking number
                rowsUpdated = db.update("parcels", values, "tracking_number = ?", new String[]{trackingNumber});
                cursor.close();
            } else {
                Log.e(TAG, "Parcel not found with tracking number: " + trackingNumber);
                if (cursor != null) cursor.close();
                return false;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating parcel: ", e);
        } finally {
            if (db != null) db.close();
        }

        if (rowsUpdated > 0) {
            Log.d(TAG, "Parcel status updated successfully. Rows affected: " + rowsUpdated);
            return true;
        } else {
            Log.e(TAG, "No rows updated. Parcel with tracking number may not exist or status is unchanged.");
            return false;
        }
    }

    /**
     * Delete a parcel by its ID.
     */
    public boolean deleteParcel(int id) {
        if (id <= 0) {
            Log.e(TAG, "Invalid Parcel ID for deletion.");
            return false;
        }

        SQLiteDatabase db = null;
        int rowsDeleted = 0;

        try {
            db = dbHelper.getWritableDatabase();
            rowsDeleted = db.delete("parcels", "_id = ?", new String[]{String.valueOf(id)});
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting parcel: ", e);
        } finally {
            if (db != null) db.close();
        }

        if (rowsDeleted > 0) {
            Log.d(TAG, "Parcel deleted successfully. Rows affected: " + rowsDeleted);
            return true;
        } else {
            Log.e(TAG, "No rows deleted. Parcel ID may not exist.");
            return false;
        }
    }

    /**
     * Check if a parcel exists by tracking number.
     */
    public boolean doesParcelExist(String trackingNumber) {
        if (trackingNumber == null || trackingNumber.isEmpty()) {
            Log.e(TAG, "Invalid tracking number for existence check.");
            return false;
        }

        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean exists = false;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery(
                    "SELECT _id FROM parcels WHERE tracking_number = ?",
                    new String[]{trackingNumber}
            );

            exists = (cursor.getCount() > 0);
        } catch (SQLException e) {
            Log.e(TAG, "Error checking if parcel exists: ", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return exists;
    }

    /**
     * Close the database when not in use.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Helper method to validate strings for null or empty values.
     */
    private boolean isAnyStringNullOrEmpty(String... strings) {
        for (String s : strings) {
            if (s == null || s.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
