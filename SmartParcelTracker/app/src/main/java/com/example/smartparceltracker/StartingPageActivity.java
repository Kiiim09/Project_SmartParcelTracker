package com.example.smartparceltracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StartingPageActivity extends AppCompatActivity {

    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_page);

        // Find the TextView from layout
        welcomeText = findViewById(R.id.welcomeText);

        // Create fade-in animation for the text
        AlphaAnimation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(2000); // Set duration for the fade-in effect
        fadeIn.setStartOffset(500); // Delay before starting

        // Create scale animation
        ScaleAnimation scaleUp = new ScaleAnimation(
                0.5f, 1f, // scale from 50% to 100%
                0.5f, 1f, // scale from 50% to 100%
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(2000);
        scaleUp.setStartOffset(500);

        // Create rotation animation
        RotateAnimation rotate = new RotateAnimation(
                0, 360, // rotate from 0 to 360 degrees
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(2000);
        rotate.setStartOffset(500);

        // Combine the animations into an AnimationSet
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(scaleUp);
        animationSet.addAnimation(rotate);

        // Start the combined animation
        welcomeText.startAnimation(animationSet);

        // Animate text character by character
        animateText();
    }

    private void animateText() {
        final String message = "Welcome to Smart Parcel Tracker";
        final int messageLength = message.length();
        welcomeText.setText(""); // Clear the text initially

        // Animate each character appearing one by one
        for (int i = 0; i < messageLength; i++) {
            final int index = i;
            // Use a delay for each character
            welcomeText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    welcomeText.append(String.valueOf(message.charAt(index)));
                }
            }, i * 150); // Adjust timing for the character-by-character animation
        }

        // After the animation finishes, directly go to the LoginActivity
        welcomeText.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start LoginActivity
                startActivity(new Intent(StartingPageActivity.this, LoginActivity.class));
                finish(); // Close the StartingPageActivity (splash screen)
            }
        }, messageLength * 150 + 2000); // Adjust timing to match the animation duration
    }
}
