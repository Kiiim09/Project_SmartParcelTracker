package com.example.smartparceltracker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;  // Import FloatingActionButton

public class ViewParcelsActivity extends AppCompatActivity {
    private ParcelService parcelService;
    private ListView parcelListView;
    private SimpleCursorAdapter adapter;
    private Cursor cursor;
    private FloatingActionButton addParcelFab; // FloatingActionButton declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parcels);

        parcelService = new ParcelService(this);
        parcelListView = findViewById(R.id.parcelListView);
        addParcelFab = findViewById(R.id.addParcelFab); // Initialize FloatingActionButton

        loadParcelData();

        // Set item click listener for the ListView
        parcelListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    int trackingIndex = cursor.getColumnIndex("tracking_number");
                    int statusIndex = cursor.getColumnIndex("status");

                    if (trackingIndex != -1 && statusIndex != -1) {
                        String trackingNumber = cursor.getString(trackingIndex);
                        String status = cursor.getString(statusIndex);

                        Toast.makeText(
                                ViewParcelsActivity.this,
                                "Tracking Number: " + trackingNumber + "\nStatus: " + status,
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        Toast.makeText(ViewParcelsActivity.this, "Column not found in the database.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewParcelsActivity.this, "Unable to retrieve parcel details.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Set long click listener for deletion
        parcelListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    int idIndex = cursor.getColumnIndex("_id"); // Assuming "_id" is the primary key
                    if (idIndex != -1) {
                        int parcelId = cursor.getInt(idIndex);
                        deleteParcel(parcelId, position);
                    } else {
                        Toast.makeText(ViewParcelsActivity.this, "Parcel ID not found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ViewParcelsActivity.this, "Unable to identify parcel for deletion.", Toast.LENGTH_SHORT).show();
                }

                return true; // Return true to consume the event
            }
        });

        // Set OnClickListener for the Floating Action Button
        addParcelFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to AddParcelActivity
                Intent intent = new Intent(ViewParcelsActivity.this, AddParcelActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadParcelData() {
        cursor = parcelService.viewParcels();
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No parcels found.", Toast.LENGTH_SHORT).show();
            return;
        }

        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[]{"tracking_number", "status"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );

        parcelListView.setAdapter(adapter);
    }

    private void deleteParcel(int parcelId, int position) {
        boolean isDeleted = parcelService.deleteParcel(parcelId);

        if (isDeleted) {
            Toast.makeText(this, "Parcel deleted successfully.", Toast.LENGTH_SHORT).show();

            // Update the cursor after deletion
            cursor = parcelService.viewParcels();
            adapter.changeCursor(cursor); // Update the adapter with the new cursor
        } else {
            Toast.makeText(this, "Failed to delete parcel.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (parcelService != null) {
            parcelService.close();
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}