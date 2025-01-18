package com.example.smartparceltracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateParcelActivity extends AppCompatActivity {
    private static final String TAG = "UpdateParcelActivity";
    private ParcelService parcelService;

    private EditText trackingNumberInput, statusInput;
    private Button updateParcelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_parcel);

        // Initialize ParcelService
        parcelService = new ParcelService(this);

        // Initialize UI elements
        trackingNumberInput = findViewById(R.id.trackingNumberInput);
        statusInput = findViewById(R.id.statusInput);
        updateParcelButton = findViewById(R.id.updateParcelButton);

        // Set up update button click listener
        updateParcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trackingNumber = trackingNumberInput.getText().toString().trim();
                String newStatus = statusInput.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(trackingNumber) || TextUtils.isEmpty(newStatus)) {
                    showToast("Please fill all fields.");
                    return;
                }

                // Debugging log
                Log.d(TAG, "Updating parcel with Tracking Number: " + trackingNumber + ", New Status: " + newStatus);

                // Check if parcel exists before updating
                if (!parcelService.doesParcelExist(trackingNumber)) {
                    showToast("Tracking number not found. Please enter a valid number.");
                    return;
                }

                // Update the parcel status in the database
                if (parcelService.updateParcelByTrackingNumber(trackingNumber, newStatus)) {
                    showToast("Parcel updated successfully!");
                    Log.d(TAG, "Parcel updated successfully. Tracking Number: " + trackingNumber);
                    finish(); // Close the activity
                } else {
                    showToast("Failed to update parcel. Status is unchanged or tracking number not found.");
                    Log.e(TAG, "Failed to update parcel with Tracking Number: " + trackingNumber);
                }
            }
        });
    }

    /**
     * Show a toast message.
     */
    private void showToast(String message) {
        Toast.makeText(UpdateParcelActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection when the activity is destroyed
        parcelService.close();
    }
}
