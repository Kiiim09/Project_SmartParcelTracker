package com.example.smartparceltracker;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button to add parcel
        Button addParcelButton = findViewById(R.id.addParcelButton);
        addParcelButton.setOnClickListener(v -> startActivitySafe(AddParcelActivity.class, "Error opening Add Parcel screen."));

        // Button to view parcels
        Button viewParcelsButton = findViewById(R.id.viewParcelsButton);
        viewParcelsButton.setOnClickListener(v -> startActivitySafe(ViewParcelsActivity.class, "Error opening View Parcels screen."));

        // Button to update parcel
        Button updateParcelButton = findViewById(R.id.updateParcelButton);
        updateParcelButton.setOnClickListener(v -> startActivitySafe(UpdateParcelActivity.class, "Error opening Update Parcel screen."));

        // Button to log out
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            startActivitySafe(LoginActivity.class, "Error logging out.");
            finish();
        });
    }

    private void startActivitySafe(Class<?> targetActivity, String errorMessage) {
        try {
            startActivity(new Intent(MainActivity.this, targetActivity));
        } catch (Exception e) {
            showToast(errorMessage);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
