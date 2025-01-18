package com.example.smartparceltracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AuthService {
    private DatabaseHelper dbHelper;

    public AuthService(Context context) {
        this.dbHelper = new DatabaseHelper(context);
    }

    public boolean register(String username, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        long result = db.insert("users", null, values);
        db.close();
        return result != -1;
    }

    public boolean login(String username, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});

        boolean loggedIn = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return loggedIn;
    }
}
