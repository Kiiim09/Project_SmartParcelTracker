package com.example.smartparceltracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "parcel_tracker.db";
    private static final int DATABASE_VERSION = 3; // Incremented version for schema updates

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String userTable = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE, " +
                "password TEXT)";

        // Create parcels table with sender and recipient details
        String parcelTable = "CREATE TABLE parcels (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " + // Ensure '_id' is primary key
                "tracking_number TEXT UNIQUE, " +
                "status TEXT, " +
                "sender_name TEXT, " +
                "sender_phone TEXT, " +
                "sender_postcode TEXT, " +
                "sender_address TEXT, " +
                "recipient_name TEXT, " +
                "recipient_phone TEXT, " +
                "recipient_postcode TEXT, " +
                "recipient_address TEXT)";

        // Execute table creation
        db.execSQL(userTable);
        db.execSQL(parcelTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades based on version
        if (oldVersion < 2) {
            // Example: Add a new column or table changes
            db.execSQL("ALTER TABLE parcels ADD COLUMN delivery_date TEXT");
        }
        if (oldVersion < 3) {
            // In case renaming fails or if there are issues, we could just recreate the table
            db.execSQL("DROP TABLE IF EXISTS parcels");
            onCreate(db); // Recreate the table with the correct '_id' column name
        }
    }
}
