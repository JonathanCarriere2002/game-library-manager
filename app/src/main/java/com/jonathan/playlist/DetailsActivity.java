/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      DetailsActivity.java         ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Activity allowing to display the details pertaining to a specific video game
 */
public class DetailsActivity extends AppCompatActivity {

    // Flag allowing to identify if a video game will be removed from its final category without confirmation
    boolean finalCategoryConfirmation = true;

    // Identifier of the video game that is going to be displayed
    int videoGameId;

    // ArrayList containing the video game from the database which will be displayed
    ArrayList<VideoGame> videoGame;

    // Checkboxes representing the various categories of the video game
    CheckBox checkBoxBacklog, checkBoxCollection, checkBoxCompletion, checkBoxWishlist;

    // ImageView representing the cover art of the video game
    ImageView imageViewGameCover;

    // Menu containing the button allowing to display the popup menu
    Menu detailsMenu;

    // MySQLiteOpenHelper allowing to manage the database
    MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this);

    // TextView representing various controls in the details page
    TextView textViewTitle, textViewPlatform, textViewPrice, textViewPublisher, textViewReleaseDate, textViewCompletionDate, textViewPlaytime;

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
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization of the activity
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_details);

        // Obtain the ActionBar of the activity
        ActionBar actionBar = getSupportActionBar();

        // Display the back button in the ActionBar
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set a custom icon for the back button in the ActionBar
        actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24));
        actionBar.setHomeActionContentDescription(this.getString(R.string.home_screen));

        // Set the activity label with white text color
        String activityLabel = getString(R.string.details_video_game);
        SpannableString spannableLabel = new SpannableString(activityLabel);
        spannableLabel.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.white)), 0, activityLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the colored activityLabel to the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(spannableLabel);
        }

        // Association between activity objects and layout elements
        checkBoxBacklog = findViewById(R.id.checkBoxBacklog);
        checkBoxCollection = findViewById(R.id.checkBoxCollection);
        checkBoxCompletion = findViewById(R.id.checkBoxCompletion);
        checkBoxWishlist = findViewById(R.id.checkBoxWishlist);
        imageViewGameCover = findViewById(R.id.imageViewGameCover);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewPlatform = findViewById(R.id.textViewPlatform);
        textViewPrice = findViewById(R.id.textViewPrice);
        textViewPublisher = findViewById(R.id.textViewPublisher);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        textViewCompletionDate = findViewById(R.id.textViewCompletionDate);
        textViewPlaytime = findViewById(R.id.textViewPlaytime);

        // Obtain the intent used when opening this activity
        Intent intent = getIntent();

        // Initialize SharedPreferences for the images
        if (this.getSharedPreferences(PREFS_NAME_DISPLAY, Context.MODE_PRIVATE) != null) {
            sharedPreferencesImages = this.getSharedPreferences(PREFS_NAME_DISPLAY, Context.MODE_PRIVATE);
        }

        // Initialize SharedPreferences for the deletion verification
        if (this.getSharedPreferences(PREFS_NAME_CONFIRMATION, Context.MODE_PRIVATE) != null) {
            sharedPreferencesVerification = this.getSharedPreferences(PREFS_NAME_CONFIRMATION, Context.MODE_PRIVATE);
        }

        // Verify if the activity is being opened with an intent containing a video game's ID
        if (intent != null && intent.hasExtra("VIDEO_GAME_ID")) {

            // Obtain the ID of the video game that is going to be displayed
            videoGameId = intent.getIntExtra("VIDEO_GAME_ID", -1);

            // Obtain the video game from the database
            videoGame = mySQLiteOpenHelper.readOneVideoGame(videoGameId);

            // Display the information of the video game within the corresponding fields
            if (videoGame != null) {

                // Set data to corresponding fields
                textViewTitle.setText(videoGame.get(0).getTitle());
                textViewPlatform.setText(videoGame.get(0).getPlatform());
                textViewPublisher.setText(videoGame.get(0).getPublisher());
                textViewReleaseDate.setText(String.valueOf(videoGame.get(0).getReleaseDate()));

                // Set data to the price field
                if (videoGame.get(0).getPrice() <= 0) {
                    textViewPrice.setText(R.string.free);
                }

                else {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    String formattedPrice = decimalFormat.format(videoGame.get(0).getPrice());
                    textViewPrice.setText("$" + formattedPrice);
                }

                // Set data to the completion date field
                if (videoGame.get(0).getCompletionDate() == null) {
                    textViewCompletionDate.setText(R.string.completion_date_no);
                }

                else {
                    textViewCompletionDate.setText(String.valueOf(videoGame.get(0).getCompletionDate()));
                }

                // Set data to the playtime field
                if (videoGame.get(0).getPlaytime() <= 0) {
                    textViewPlaytime.setText(R.string.playtime_no);
                }

                else if (videoGame.get(0).getPlaytime() == 1) {
                    textViewPlaytime.setText(videoGame.get(0).getPlaytime() + " " + this.getString(R.string.hour));
                }

                else {
                    textViewPlaytime.setText(videoGame.get(0).getPlaytime() + " " + this.getString(R.string.hours));
                }

                // Set data to the checkbox fields based on if the video game is in the corresponding category
                checkBoxBacklog.setChecked(videoGame.get(0).isBacklog());
                checkBoxCollection.setChecked(videoGame.get(0).isCollection());
                checkBoxCompletion.setChecked(videoGame.get(0).isCompletion());
                checkBoxWishlist.setChecked(videoGame.get(0).isWishlist());

                // Verify if the cover art for the video game should be displayed
                if (sharedPreferencesImages.getInt(KEY_DISPLAY, 1) == 0) {
                    imageViewGameCover.setVisibility(View.GONE);
                }

                // Display the cover art of the video game
                else {

                    // Set the visibility of the ImageView to VISIBLE
                    imageViewGameCover.setVisibility(View.VISIBLE);

                    // Obtain the URI of the cover art image associated to the video game
                    String imagePath = videoGame.get(0).getImagePath();
                    Uri imageUri = Uri.parse(imagePath);

                    // Attempt to load the cover art image using the URI
                    try {
                        // Verify if the file corresponding to the URI exists
                        if (Objects.equals(imageUri.getScheme(), "file")) {
                            File imageFile = new File(imageUri.getPath());
                            // If the image file exists, set the image to the ImageView
                            if (imageFile.exists()) {
                                imageViewGameCover.setImageURI(imageUri);
                            }
                            // If the image file does not exist, set the image to a placeholder
                            else {
                                imageViewGameCover.setImageResource(R.drawable.baseline_image_24);
                            }
                        }
                        // If the URI scheme is content, use ContentResolver to load the image
                        else if (Objects.equals(imageUri.getScheme(), "content")) {
                            ContentResolver contentResolver = imageViewGameCover.getContext().getContentResolver();
                            InputStream inputStream = contentResolver.openInputStream(imageUri);
                            // If the input stream is not null, set the image to the ImageView
                            if (inputStream != null) {
                                inputStream.close();
                                imageViewGameCover.setImageURI(imageUri);
                            }
                            // If the input stream is null, set the image to a placeholder
                            else {
                                imageViewGameCover.setImageResource(R.drawable.baseline_image_24);
                            }
                        }
                        // If the URI scheme is not supported, set the image to a placeholder
                        else {
                            imageViewGameCover.setImageResource(R.drawable.baseline_image_24);
                        }
                    }

                    // Display the placeholder image if any exceptions are thrown when attempting to display the cover image
                    catch (Exception e) {
                        imageViewGameCover.setImageResource(R.drawable.baseline_image_24);
                    }

                }

            }

        }

    }

    /**
     * Method allowing to inflate the options menu
     * @param menu The options menu in which you place your items
     * @return True if the menu was successfully inflated, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu and associate it with the mainMenu variable
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_details, menu);
        detailsMenu = menu;

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

        // If the activity menu icon is selected, display the popup menu
        else if (item.getItemId() == R.id.activityMenuButton) {
            showBottomSheetMenu(this.findViewById(R.id.activityMenuButton));
            return true;
        }

        // Return the selected MenuItem
        return super.onOptionsItemSelected(item);

    }

    /**
     * Method allowing to display the BottomSheetDialog menu containing additional actions for the displayed video game
     * @param view View in which the BottomSheetDialog will be displayed
     */
    private void showBottomSheetMenu(View view) {

        // Initialization of the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.menu_action, view.findViewById(R.id.linearLayoutActionMenu));

        // Find TextView elements in the BottomSheetDialog
        TextView textViewBacklog = bottomSheetView.findViewById(R.id.textViewBacklog);
        TextView textViewCollection = bottomSheetView.findViewById(R.id.textViewCollection);
        TextView textViewCompletion = bottomSheetView.findViewById(R.id.textViewCompletion);
        TextView textViewWishlist = bottomSheetView.findViewById(R.id.textViewWishlist);

        // Set the text of the menu items based on the status of the video game
        updateTextView(textViewBacklog, videoGame.get(0).isBacklog(), "Backlog");
        updateTextView(textViewCollection, videoGame.get(0).isCollection(), "Collection");
        updateTextView(textViewCompletion, videoGame.get(0).isCompletion(), "Completion");
        updateTextView(textViewWishlist, videoGame.get(0).isWishlist(), "Wishlist");

        // Show the BottomSheetDialog
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        // Code executed if the user selects the "edit" option in the menu
        bottomSheetView.findViewById(R.id.textViewEdit).setOnClickListener(v -> {

            // Create an Intent to start FormActivity
            Intent intent = new Intent(this, FormActivity.class);

            // Pass the video game ID as an extra
            intent.putExtra("VIDEO_GAME_ID", Integer.valueOf(videoGame.get(0).getId()));

            // Pass an extra indicating that the form was opened from the details page
            intent.putExtra("FORM_OPENED_FROM_DETAILS", 1);

            // Start FormActivity
            startActivity(intent);

            // Dismiss the BottomSheetDialog
            bottomSheetDialog.dismiss();

        });

        // Code executed if the user selects the "delete" option in the menu
        bottomSheetView.findViewById(R.id.textViewDelete).setOnClickListener(v -> {

            // Show a confirmation dialog box to confirm the deletion of the video game
            showDeleteConfirmationDialog(false);

            // Dismiss the BottomSheetDialog
            bottomSheetDialog.dismiss();

        });

        // Code executed if the user selects the "backlog" option in the menu
        bottomSheetView.findViewById(R.id.textViewBacklog).setOnClickListener(v -> {

            // Update the status of the video game in the backlog
            updateCategory(videoGame.get(0).getId(), "backlog", this.getString(R.string.save_backlog_success), this.getString(R.string.save_backlog_error), this.getString(R.string.remove_backlog_success), this.getString(R.string.remove_backlog_error));

            // Dismiss the BottomSheetDialog
            bottomSheetDialog.dismiss();

        });

        // Code executed if the user selects the "collection" option in the menu
        bottomSheetView.findViewById(R.id.textViewCollection).setOnClickListener(v -> {

            // Update the status of the video game in the collection
            updateCategory(videoGame.get(0).getId(), "collection", this.getString(R.string.save_collection_success), this.getString(R.string.save_collection_error), this.getString(R.string.remove_collection_success), this.getString(R.string.remove_collection_error));

            // Dismiss the BottomSheetDialog
            bottomSheetDialog.dismiss();

        });

        // Code executed if the user selects the "completion" option in the menu
        bottomSheetView.findViewById(R.id.textViewCompletion).setOnClickListener(v -> {

            // Update the status of the video game in the completion list
            updateCategory(videoGame.get(0).getId(), "completion", this.getString(R.string.save_completion_success), this.getString(R.string.save_completion_error), this.getString(R.string.remove_completion_success), this.getString(R.string.remove_completion_error));

            // Dismiss the BottomSheetDialog
            bottomSheetDialog.dismiss();

        });

        // Code executed if the user selects the "wishlist" option in the menu
        bottomSheetView.findViewById(R.id.textViewWishlist).setOnClickListener(v -> {

            // Update the status of the video game in the wishlist
            updateCategory(videoGame.get(0).getId(), "wishlist", this.getString(R.string.save_wishlist_success), this.getString(R.string.save_wishlist_error), this.getString(R.string.remove_wishlist_success), this.getString(R.string.remove_wishlist_error));

            // Dismiss the BottomSheetDialog
            bottomSheetDialog.dismiss();

        });

    }

    /**
     * Method allowing to set the displayed text within each TextView of the BottomSheetDialog
     * @param textView TextView in which the displayed text will be set
     * @param status Status of the video game indicating whether or not it is saved within the specified category
     * @param category Category in which the video game is saved or removed
     */
    private void updateTextView(TextView textView, boolean status, String category) {

        // If the video game is already within the specified category, display "remove" text
        if (status) {
            // Switch case allowing to set the text of the TextView based on the category
            switch (category) {
                case "Backlog":
                    textView.setText(this.getString(R.string.remove_backlog));
                    break;
                case "Collection":
                    textView.setText(this.getString(R.string.remove_collection));
                    break;
                case "Completion":
                    textView.setText(this.getString(R.string.remove_completion));
                    break;
                case "Wishlist":
                    textView.setText(this.getString(R.string.remove_wishlist));
                    break;
            }
        }

        // If the video game is not already within the specified category, display "save" text
        else {
            // Switch case allowing to set the text of the TextView based on the category
            switch (category) {
                case "Backlog":
                    textView.setText(this.getString(R.string.save_backlog));
                    break;
                case "Collection":
                    textView.setText(this.getString(R.string.save_collection));
                    break;
                case "Completion":
                    textView.setText(this.getString(R.string.save_completion));
                    break;
                case "Wishlist":
                    textView.setText(this.getString(R.string.save_wishlist));
                    break;
            }
        }

    }

    /**
     * Method that opens a AlertDialog box in order to confirm the deletion of thw video game
     * @param isFinalCategory Boolean indicating whether or not the video game is being removed from its final category
     */
    private void showDeleteConfirmationDialog(Boolean isFinalCategory) {

        // Verify if confirmation should be asked when deleting a video game
        if (sharedPreferencesVerification.getInt(KEY_CONFIRMATION, 1) == 1) {

            // Create an AlertDialog builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Set the title of the AlertDialog box
            builder.setTitle(this.getString(R.string.delete) + " " + videoGame.get(0).getTitle() + "?");

            // Conditionally set the message of the AlertDialog box
            if (isFinalCategory) {
                builder.setMessage(this.getString(R.string.delete_message_final) + " " + this.getString(R.string.delete_message));
            }

            else {
                builder.setMessage(this.getString(R.string.delete_message));
            }

            // Set the positive button to confirm the deletion of the video game
            builder.setPositiveButton(this.getString(R.string.confirm), (dialog, which) -> deleteVideoGame(videoGame.get(0).getId()));

            // Set the negative button to cancel the deletion of the video game
            builder.setNegativeButton(this.getString(R.string.cancel), null);

            // Show the AlertDialog box
            builder.show();

        }

        // If no confirmation is required, simply delete the video game
        else {
            finalCategoryConfirmation = false;
            deleteVideoGame(videoGame.get(0).getId());
        }

    }

    /**
     * Method using the MySQLiteOpenHelper class to delete a video game from the database
     * @param id The id of the video game to be deleted from the database
     */
    public void deleteVideoGame(int id) {

        // Use the MySQLiteOpenHelper class to delete the video game from the database
        try {
            // Delete the video game from the database and display a message indicating the status of the deletion of the video game
            if (mySQLiteOpenHelper.deleteOneVideoGame(id)) {
                Toast.makeText(this, this.getString(R.string.delete_video_game_success), Toast.LENGTH_SHORT).show();
                // Return to the main activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(this, this.getString(R.string.delete_video_game_error), Toast.LENGTH_SHORT).show();
            }
        }

        // Catch any exceptions when trying to delete the video game from the database
        catch (Exception e) {
            // Display a message if any error occurs
            Toast.makeText(this, this.getString(R.string.delete_video_game_error), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Method allowing to update the status of a video game in the backlog, collection, completion or wishlist
     * @param videoGameId ID of the video game to be updated
     * @param category Category in which the video game will be added or removed
     * @param saveSuccessMessage Message to display when the video game is saved successfully
     * @param saveErrorMessage Message to display when the video game is not saved successfully
     * @param removeSuccessMessage Message to display when the video game is removed successfully
     * @param removeErrorMessage Message to display when the video game is not removed successfully
     */
    private void updateCategory(int videoGameId, String category, String saveSuccessMessage, String saveErrorMessage, String removeSuccessMessage, String removeErrorMessage) {

        // Initialize the status of the video game within the specified category
        boolean categoryStatus = false;

        // Obtain the status of the video within the specified category
        switch (category) {
            case "backlog": {
                categoryStatus = videoGame.get(0).isBacklog();
                break;
            }
            case "collection": {
                categoryStatus = videoGame.get(0).isCollection();
                break;
            }
            case "completion": {
                categoryStatus = videoGame.get(0).isCompletion();
                break;
            }
            case "wishlist": {
                categoryStatus = videoGame.get(0).isWishlist();
                break;
            }
        }

        // Verify if the video game is already saved in the specified category
        if (categoryStatus) {

            // Verify if the video game will not be removed from its final category
            if (mySQLiteOpenHelper.getCategoryStatusTotal(videoGameId) != 1) {

                // Update the status of the video game in the specified category by removing it
                Boolean updateCategoryStatus = mySQLiteOpenHelper.updateCategoryStatus(category, videoGameId, 0);

                // Display a message indicating the status of the update of the video game
                if (updateCategoryStatus) {
                    Toast.makeText(this, removeSuccessMessage, Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(this, removeErrorMessage, Toast.LENGTH_SHORT).show();
                }

            }

            // If the video game will be removed from its final category, delete if from the database
            else {
                // Show the confirmation box allowing to confirm the deletion of the video game
                showDeleteConfirmationDialog(true);
            }

        }

        // Verify if the video game is not already saved in the specified category
        else {

            // Update the status of the video game in the specified category by adding it
            Boolean updateCategoryStatus = mySQLiteOpenHelper.updateCategoryStatus(category, videoGameId, 1);

            // Display a message indicating the status of the update of the video game
            if (updateCategoryStatus) {
                Toast.makeText(this, saveSuccessMessage, Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(this, saveErrorMessage, Toast.LENGTH_SHORT).show();
            }

        }

        // Reset the values of the video game and update the status of the CheckBox items in the layout
        if (videoGame != null && finalCategoryConfirmation) {
            videoGame = mySQLiteOpenHelper.readOneVideoGame(videoGameId);
            checkBoxBacklog.setChecked(videoGame.get(0).isBacklog());
            checkBoxCollection.setChecked(videoGame.get(0).isCollection());
            checkBoxCompletion.setChecked(videoGame.get(0).isCompletion());
            checkBoxWishlist.setChecked(videoGame.get(0).isWishlist());
        }

    }

}
