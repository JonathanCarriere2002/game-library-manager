/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      SettingsActivity.java        ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

/**
 * Activity containing the primary settings for the application
 */
public class SettingsActivity extends AppCompatActivity {

    // Button used to delete all video games from the database
    Button buttonDeleteAllData;

    // Switches used to manage images and deletion verification
    SwitchCompat switchImages, switchVerification;

    // MySQLiteOpenHelper allowing to manage the database
    MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this);

    // SharedPreferences used to store image display settings
    SharedPreferences sharedPreferencesImages;
    private static final String PREFS_NAME_DISPLAY = "DisplayPreferences";
    private static final String KEY_DISPLAY = "DisplayOption";

    // SharedPreferences used to store confirmation verification settings
    SharedPreferences sharedPreferencesVerification;
    private static final String PREFS_NAME_CONFIRMATION = "ConfirmationPreferences";
    private static final String KEY_CONFIRMATION = "ConfirmationOption";

    /**
     * Code executed at the start of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization of the activity
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_settings);

        // Obtain the ActionBar of the activity
        ActionBar actionBar = getSupportActionBar();

        // Display the back button in the ActionBar
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set a custom icon for the back button in the ActionBar
        actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24));
        actionBar.setHomeActionContentDescription(this.getString(R.string.home_screen));

        // Set the activity label with white text color
        String activityLabel = getString(R.string.settings);
        SpannableString spannableLabel = new SpannableString(activityLabel);
        spannableLabel.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.white)), 0, activityLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the colored activityLabel to the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(spannableLabel);
        }

        // Association between activity objects and layout elements
        buttonDeleteAllData = findViewById(R.id.buttonDeleteAllData);
        switchImages = findViewById(R.id.switchImages);
        switchVerification = findViewById(R.id.switchVerification);

        // OnClickLister for buttonDeleteAllData allowing to delete all video games from the database
        buttonDeleteAllData.setOnClickListener(v -> {
            // Show a confirmation dialog box to confirm the deletion of all video games
            showDeleteConfirmationDialog();
        });

        // Initialize SharedPreferences for the images
        if (this.getSharedPreferences(PREFS_NAME_DISPLAY, Context.MODE_PRIVATE) != null) {
            sharedPreferencesImages = this.getSharedPreferences(PREFS_NAME_DISPLAY, Context.MODE_PRIVATE);
        }

        // Initialize SharedPreferences for the deletion verification
        if (this.getSharedPreferences(PREFS_NAME_CONFIRMATION, Context.MODE_PRIVATE) != null) {
            sharedPreferencesVerification = this.getSharedPreferences(PREFS_NAME_CONFIRMATION, Context.MODE_PRIVATE);
        }

        // Set the checked state of the display switch based on the value stored in SharedPreferences
        switchImages.setChecked(sharedPreferencesImages.getInt(KEY_DISPLAY, 1) == 1);

        // Set the checked state of the confirmation switch based on the value stored in SharedPreferences
        switchVerification.setChecked(sharedPreferencesVerification.getInt(KEY_CONFIRMATION, 1) == 1);

        // Set an OnCheckedChangeListener for switchImages
        switchImages.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update SharedPreferences for image display when the switch is toggled
            SharedPreferences.Editor editor = sharedPreferencesImages.edit();
            editor.putInt(KEY_DISPLAY, isChecked ? 1 : 0);
            editor.apply();
            // Display a message indicating the SharedPreferences were updated
            Toast.makeText(this, this.getString(R.string.settings_updated), Toast.LENGTH_SHORT).show();
        });

        // Set an OnCheckedChangeListener for switchVerification
        switchVerification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update SharedPreferences for deletion verification when the switch is toggled
            SharedPreferences.Editor editor = sharedPreferencesVerification.edit();
            editor.putInt(KEY_CONFIRMATION, isChecked ? 1 : 0);
            editor.apply();
            // Display a message indicating the SharedPreferences were updated
            Toast.makeText(this, this.getString(R.string.settings_updated), Toast.LENGTH_SHORT).show();
        });

        // Ensure that settings are correctly applied upon return to the main activity
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Method allowing to inflate the options menu
     * @param menu The options menu in which you place your items
     * @return True if the menu was successfully inflated, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Return the result of the menu inflation
        return true;

    }

    /**
     * Method allowing to handle menu item selections
     * @param item The menu item that was selected
     * @return True if the menu item was successfully handled, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // If the back icon is selected, return to the main activity
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }

        // Return the selected MenuItem
        return super.onOptionsItemSelected(item);

    }

    /**
     * Method allowing to show a confirmation dialog box to confirm the deletion of all video games from the database
     */
    private void showDeleteConfirmationDialog() {

        // Verify if there are any video games in the database
        if (mySQLiteOpenHelper.readVideoGameCount() == 0) {
            Toast.makeText(this, this.getString(R.string.delete_video_game_error_no_data), Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title of the AlertDialog box
        builder.setTitle(this.getString(R.string.delete_all));

        // Set the message of the AlertDialog box
        builder.setMessage(this.getString(R.string.delete_message_all));

        // Set the positive button to confirm the deletion of the video game
        builder.setPositiveButton(this.getString(R.string.confirm), (dialog, which) -> deleteAllVideoGames());

        // Set the negative button to cancel the deletion of the video game
        builder.setNegativeButton(this.getString(R.string.cancel), null);

        // Show the AlertDialog box
        builder.show();

    }

    /**
     * Method allowing to delete all video games from the database
     */
    public void deleteAllVideoGames() {

        // Delete all video games from the database and display a message indicating the status of the deletion of the video games
        try {
            if (mySQLiteOpenHelper.deleteAllVideoGames()) {
                Toast.makeText(this, this.getString(R.string.delete_video_game_success_all), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, this.getString(R.string.delete_video_game_error_all), Toast.LENGTH_SHORT).show();
            }
        }

        // Catch any exceptions when trying to delete all video games from the database
        catch (Exception e) {
            Toast.makeText(this, this.getString(R.string.delete_video_game_error_all), Toast.LENGTH_SHORT).show();
        }

    }

}
