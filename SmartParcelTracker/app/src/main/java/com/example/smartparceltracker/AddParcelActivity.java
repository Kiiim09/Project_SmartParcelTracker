package com.example.smartparceltracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddParcelActivity extends AppCompatActivity {
    private ParcelService parcelService;

    // Input fields for Sender and Recipient details
    private EditText senderNameInput, senderPhoneInput, senderPostcodeInput, senderAddressInput;
    private EditText recipientNameInput, recipientPhoneInput, recipientPostcodeInput, recipientAddressInput;

    // UI elements for price calculation and adding parcel
    private TextView priceTextView;
    private Button calculatePriceButton, addParcelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_parcel);

        parcelService = new ParcelService(this);

        // Initialize sender-related input fields
        senderNameInput = findViewById(R.id.senderNameInput);
        senderPhoneInput = findViewById(R.id.senderPhoneInput);
        senderPostcodeInput = findViewById(R.id.senderPostcodeInput);
        senderAddressInput = findViewById(R.id.senderAddressInput);

        // Initialize recipient-related input fields
        recipientNameInput = findViewById(R.id.recipientNameInput);
        recipientPhoneInput = findViewById(R.id.recipientPhoneInput);
        recipientPostcodeInput = findViewById(R.id.recipientPostcodeInput);
        recipientAddressInput = findViewById(R.id.recipientAddressInput);

        // Initialize price display and action buttons
        priceTextView = findViewById(R.id.priceTextView);
        calculatePriceButton = findViewById(R.id.calculatePriceButton);
        addParcelButton = findViewById(R.id.addParcelButton);

        // Button to calculate shipping price based on postcode
        calculatePriceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postcode = recipientPostcodeInput.getText().toString().trim();
                if (postcode.isEmpty()) {
                    showToast("Please enter recipient's postcode.");
                    return;
                }

                try {
                    // Calculate shipping price based on the provided postcode
                    double price = calculatePrice(postcode);
                    priceTextView.setText(String.format("Shipping Price: RM %.2f", price));
                } catch (NumberFormatException e) {
                    showToast("Invalid postcode. Please enter a valid number.");
                }
            }
        });

        // Button to add parcel with sender and recipient details
        addParcelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect sender details
                String senderName = senderNameInput.getText().toString().trim();
                String senderPhone = senderPhoneInput.getText().toString().trim();
                String senderPostcode = senderPostcodeInput.getText().toString().trim();
                String senderAddress = senderAddressInput.getText().toString().trim();

                // Collect recipient details
                String recipientName = recipientNameInput.getText().toString().trim();
                String recipientPhone = recipientPhoneInput.getText().toString().trim();
                String recipientPostcode = recipientPostcodeInput.getText().toString().trim();
                String recipientAddress = recipientAddressInput.getText().toString().trim();

                // Validate inputs
                if (!validateInputs(senderName, senderPhone, senderPostcode, senderAddress, recipientName, recipientPhone, recipientPostcode, recipientAddress)) {
                    return;
                }

                // Add parcel details to the database
                boolean success = parcelService.addParcel(
                        generateTrackingNumber(), // Auto-generate tracking number
                        "Pending", // Default status
                        senderName, senderPhone, senderPostcode, senderAddress,
                        recipientName, recipientPhone, recipientPostcode, recipientAddress
                );

                if (success) {
                    showToast("Parcel added successfully!");

                    // After adding the parcel, navigate to ViewParcelsActivity
                    Intent intent = new Intent(AddParcelActivity.this, ViewParcelsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Clear the back stack
                    startActivity(intent);

                    // Set the result and finish activity
                    setResult(RESULT_OK);
                    finish();
                } else {
                    showToast("Failed to add parcel.");
                }
            }
        });
    }

    /**
     * Validate user inputs for both sender and recipient fields.
     */
    private boolean validateInputs(String senderName, String senderPhone, String senderPostcode, String senderAddress,
                                   String recipientName, String recipientPhone, String recipientPostcode, String recipientAddress) {
        if (senderName.isEmpty() || senderPhone.isEmpty() || senderPostcode.isEmpty() || senderAddress.isEmpty() ||
                recipientName.isEmpty() || recipientPhone.isEmpty() || recipientPostcode.isEmpty() || recipientAddress.isEmpty()) {
            showToast("Please fill all fields.");
            return false;
        }

        if (!isNumeric(senderPostcode) || !isNumeric(recipientPostcode)) {
            showToast("Invalid postcode. Please enter numeric values.");
            return false;
        }

        if (!isPhoneValid(senderPhone) || !isPhoneValid(recipientPhone)) {
            showToast("Invalid phone number. Please enter a valid phone number.");
            return false;
        }

        return true;
    }

    /**
     * Calculate shipping price based on postcode regions.
     */
    private double calculatePrice(String postcode) {
        int region = Integer.parseInt(postcode) / 1000; // Extract region prefix from postcode
        switch (region) {
            case 1: // Region 1: Federal Territory, Selangor
                return 5.00;
            case 2: // Region 2: Penang, Kedah, Perlis
                return 7.00;
            case 3: // Region 3: Perak, Kelantan
                return 10.00;
            case 8: // Region 8: Sabah, Sarawak
                return 15.00;
            default: // Other regions
                return 12.00;
        }
    }

    /**
     * Generate a unique tracking number based on the current timestamp.
     */
    private String generateTrackingNumber() {
        return "TRK-" + System.currentTimeMillis();
    }

    /**
     * Show a toast message.
     */
    private void showToast(String message) {
        Toast.makeText(AddParcelActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check if a string is numeric.
     */
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate phone number length (basic check).
     */
    private boolean isPhoneValid(String phone) {
        return phone.length() >= 10 && phone.length() <= 15;
    }
}
