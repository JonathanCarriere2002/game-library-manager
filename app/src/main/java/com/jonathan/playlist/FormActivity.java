/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      FormActivity.java            ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

/**
 * Activity containing the form used for creating and modifying video games
 */
public class FormActivity extends AppCompatActivity {

    // Request code for the permission to read external storage
    private static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 1000;

    // Boolean indicating if the DatePickerDialog is open
    boolean isDatePickerDialogOpen = false;

    // Boolean indicating if the form is in edit mode
    boolean isEditMode = false;

    // Boolean indicating if an image is currently being selected from the gallery
    boolean isImageSelected = false;

    // Integer indicating whether or not the form was accessed via the details activity
    int openedFromDetailsActivity;

    // Identifier for which fragment opened the activity
    int fragmentId;

    // Identifier of the video game that is going to be edited
    int videoGameId;

    // ActivityResultLauncher used to open the gallery
    ActivityResultLauncher<Intent> galleryLauncher;

    // Button representing the save button in the form
    Button buttonSave;

    // Calendar objects representing the completion date and release date of the video game
    Calendar calendarCompletionDate = Calendar.getInstance();
    Calendar calendarReleaseDate = Calendar.getInstance();

    // CheckBox representing various controls in the video game form
    CheckBox checkBoxBacklog, checkBoxCollection, checkBoxCompletion, checkBoxWishlist;

    // EditText representing various controls in the video game form
    EditText editTextTitle, editTextPlatform, editTextPublisher, editTextPrice, editTextReleaseDate, editTextCompletionDate, editTextPlaytime;

    // ImageViews representing clear buttons associated with each field
    ImageView imageViewClearCoverArt, imageViewClearTitle, imageViewClearPlatform, imageViewClearPublisher, imageViewClearPrice, imageViewClearReleaseDate, imageViewClearCompletionDate, imageViewClearPlaytime;

    // ImageView representing the cover art of the video game
    ImageView imageViewGameCover;

    // MySQLiteOpenHelper allowing to manage the database
    MySQLiteOpenHelper mySQLiteOpenHelper = new MySQLiteOpenHelper(this);

    // TextView associated with the cover art image
    TextView textViewCoverArt;

    // TextViews used to display error messages in the form
    TextView textViewTitleValidation, textViewPlatformValidation, textViewPriceValidation, textViewPublisherValidation, textViewReleaseDateValidation, textViewCompletionDateValidation, textViewPlaytimeValidation, textViewSaveToValidation;

    // URI correspond to the selected cover art image for the video game
    Uri coverArtURI;

    // SharedPreferences used to store image display settings
    SharedPreferences sharedPreferencesImages;
    private static final String PREFS_NAME_DISPLAY = "DisplayPreferences";
    private static final String KEY_DISPLAY = "DisplayOption";

    /**
     * Code executed at the start of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization of the activity
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_form);

        // Association between activity objects and layout elements
        buttonSave = findViewById(R.id.buttonSave);
        checkBoxBacklog = findViewById(R.id.checkBoxBacklog);
        checkBoxCollection = findViewById(R.id.checkBoxCollection);
        checkBoxCompletion = findViewById(R.id.checkBoxCompletion);
        checkBoxWishlist = findViewById(R.id.checkBoxWishlist);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextPlatform = findViewById(R.id.editTextPlatform);
        editTextPublisher = findViewById(R.id.editTextPublisher);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextReleaseDate = findViewById(R.id.editTextReleaseDate);
        editTextCompletionDate = findViewById(R.id.editTextCompletionDate);
        editTextPlaytime = findViewById(R.id.editTextPlaytime);
        imageViewClearCoverArt = findViewById(R.id.imageViewClearCoverArt);
        imageViewClearTitle = findViewById(R.id.imageViewClearTitle);
        imageViewClearPlatform = findViewById(R.id.imageViewClearPlatform);
        imageViewClearPublisher = findViewById(R.id.imageViewClearPublisher);
        imageViewClearPrice = findViewById(R.id.imageViewClearPrice);
        imageViewClearReleaseDate = findViewById(R.id.imageViewClearReleaseDate);
        imageViewClearCompletionDate = findViewById(R.id.imageViewClearCompletionDate);
        imageViewClearPlaytime = findViewById(R.id.imageViewClearPlaytime);
        imageViewGameCover = findViewById(R.id.imageViewGameCover);
        textViewCoverArt = findViewById(R.id.textViewCoverArt);
        textViewTitleValidation = findViewById(R.id.textViewTitleValidation);
        textViewPlatformValidation = findViewById(R.id.textViewPlatformValidation);
        textViewPriceValidation = findViewById(R.id.textViewPriceValidation);
        textViewPublisherValidation = findViewById(R.id.textViewPublisherValidation);
        textViewReleaseDateValidation = findViewById(R.id.textViewReleaseDateValidation);
        textViewCompletionDateValidation = findViewById(R.id.textViewCompletionDateValidation);
        textViewPlaytimeValidation = findViewById(R.id.textViewPlaytimeValidation);
        textViewSaveToValidation = findViewById(R.id.textViewSaveToValidation);

        // ActivityResultLauncher allowing to handle the selected image from the gallery
        galleryLauncher = registerForActivityResult(
                // Launch an activity for selecting an image from the gallery
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    // If the result is OK and the selected image is not null, get the URI of the selected image
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // Get the URI of the selected image
                        Uri selectedImageUri = result.getData().getData();
                        // Try to get the necessary permissions to read the selected image
                        try {
                            // Assert that the image is not null and get the permission to read the URI across device reboots
                            assert selectedImageUri != null;
                            getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        }
                        // If an exception occurs, display a message to the user
                        catch (SecurityException e) {
                            Toast.makeText(FormActivity.this, R.string.error_gallery, Toast.LENGTH_SHORT).show();
                        }
                        // Set the URI of the selected image as the cover art URI and display the selected image in the ImageView
                        coverArtURI = selectedImageUri;
                        imageViewGameCover.setImageURI(selectedImageUri);
                        imageViewGameCover.setTag(selectedImageUri.toString());
                        updateClearCoverArtVisibility();
                    }
                }
        );

        // OnClickListener for the cover art ImageView allowing to open the gallery
        imageViewGameCover.setOnClickListener(v -> openGallery());

        // Set ImageViews for each EditText field to clear the field when clicked
        setClearButton(editTextTitle, findViewById(R.id.imageViewClearTitle), false, false);
        setClearButton(editTextPlatform, findViewById(R.id.imageViewClearPlatform), false, false);
        setClearButton(editTextPublisher, findViewById(R.id.imageViewClearPublisher), false, false);
        setClearButton(editTextPrice, findViewById(R.id.imageViewClearPrice), false, false);
        setClearButton(editTextReleaseDate, findViewById(R.id.imageViewClearReleaseDate), true, false);
        setClearButton(editTextCompletionDate, findViewById(R.id.imageViewClearCompletionDate), false, true);
        setClearButton(editTextPlaytime, findViewById(R.id.imageViewClearPlaytime), false, false);

        // OnClickListener for imageViewClearCoverArt allowing to clear the cover art image
        imageViewClearCoverArt.setOnClickListener(v -> {
            // Reset the cover art image to the default drawable
            imageViewGameCover.setImageResource(R.drawable.baseline_image_24);
            // Set coverArtURI to null and update the visibility of imageViewClearCoverArt
            coverArtURI = null;
            updateClearCoverArtVisibility();
        });

        // Set FocusChangeListeners for EditText fields allowing to set the cursor at the beginning of the field when unselected
        setFocusChangeListener(editTextTitle);
        setFocusChangeListener(editTextPlatform);
        setFocusChangeListener(editTextPrice);
        setFocusChangeListener(editTextPublisher);
        setFocusChangeListener(editTextReleaseDate);
        setFocusChangeListener(editTextCompletionDate);
        setFocusChangeListener(editTextPlaytime);

        // Set OnClickListeners for EditText fields using dates allowing to open a DatePickerDialog when clicked
        editTextReleaseDate.setOnClickListener(v -> showDatePickerDialog(editTextReleaseDate, calendarReleaseDate, false));
        editTextCompletionDate.setOnClickListener(v -> showDatePickerDialog(editTextCompletionDate, calendarCompletionDate, true));

        // Association between layout elements and activity methods
        buttonSave.setOnClickListener(addVideoGame);

        // Obtain the ActionBar of the activity
        ActionBar actionBar = getSupportActionBar();

        // Display the back button in the ActionBar
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Set a custom icon for the back button in the ActionBar
        actionBar.setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.baseline_arrow_back_24));
        actionBar.setHomeActionContentDescription(this.getString(R.string.home_screen));

        // Ask for confirmation when closing the form via the back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showCancelConfirmationDialog();
            }
        });

        // Obtain the intent used when opening this activity
        Intent intent = getIntent();

        // Initialize SharedPreferences for the images
        if (this.getSharedPreferences(PREFS_NAME_DISPLAY, Context.MODE_PRIVATE) != null) {
            sharedPreferencesImages = this.getSharedPreferences(PREFS_NAME_DISPLAY, Context.MODE_PRIVATE);
        }

        // Verify if the activity is being opened with the intention of editing a video game
        if (intent != null && intent.hasExtra("VIDEO_GAME_ID")) {

            // Set the title for this activity when updating
            isEditMode = true;
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.edit_video_game);
            }

            // Obtain the ID of the video game that is going to be edited
            videoGameId = intent.getIntExtra("VIDEO_GAME_ID", -1);

            // Obtain the video game from the database
            ArrayList<VideoGame> videoGame = mySQLiteOpenHelper.readOneVideoGame(videoGameId);

            // Set the values of the CheckBox fields and EditText fields to the values of the video game
            if (videoGame != null) {

                // Set data to corresponding fields
                editTextTitle.setText(videoGame.get(0).getTitle());
                editTextPlatform.setText(videoGame.get(0).getPlatform());
                editTextPublisher.setText(videoGame.get(0).getPublisher());
                editTextReleaseDate.setText(String.valueOf(videoGame.get(0).getReleaseDate()));
                checkBoxBacklog.setChecked(videoGame.get(0).isBacklog());
                checkBoxCollection.setChecked(videoGame.get(0).isCollection());
                checkBoxCompletion.setChecked(videoGame.get(0).isCompletion());
                checkBoxWishlist.setChecked(videoGame.get(0).isWishlist());

                // Format and set the price field
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("0.00", symbols);
                String formattedPrice = decimalFormat.format(videoGame.get(0).getPrice());
                editTextPrice.setText(formattedPrice);

                // Set the value of the playtime field if the default value is not used
                if (videoGame.get(0).getPlaytime() != -1) {
                    editTextPlaytime.setText(String.valueOf(videoGame.get(0).getPlaytime()));
                }

                // Set the calendar objects to the values of the video game
                calendarReleaseDate.set(videoGame.get(0).getReleaseDate().getYear(), videoGame.get(0).getReleaseDate().getMonthValue() - 1, videoGame.get(0).getReleaseDate().getDayOfMonth());
                if (videoGame.get(0).getCompletionDate() != null) {
                    editTextCompletionDate.setText(String.valueOf(videoGame.get(0).getCompletionDate()));
                    calendarCompletionDate.set(videoGame.get(0).getCompletionDate().getYear(), videoGame.get(0).getCompletionDate().getMonthValue() - 1, videoGame.get(0).getCompletionDate().getDayOfMonth());
                }

                // Obtain the URI of the cover art image associated to the video game
                String imagePath = videoGame.get(0).getImagePath();
                Uri imageUri = Uri.parse(imagePath);
                coverArtURI = imageUri;

                // Verify if the cover art for the video game should be displayed
                if (sharedPreferencesImages.getInt(KEY_DISPLAY, 1) == 0) {
                    textViewCoverArt.setVisibility(View.GONE);
                    imageViewGameCover.setVisibility(View.GONE);
                    imageViewClearCoverArt.setVisibility(View.GONE);
                }

                // Display the cover art of the video game
                else {

                    // Set the visibility of the ImageView to VISIBLE
                    textViewCoverArt.setVisibility(View.VISIBLE);
                    imageViewGameCover.setVisibility(View.VISIBLE);
                    imageViewClearCoverArt.setVisibility(View.VISIBLE);

                    // Verify if the image path is null and update the value of coverArtURI accordingly
                    if ("null".equals(imagePath) || imagePath == null) {
                        coverArtURI = null;
                    }
                    else {
                        coverArtURI = Uri.parse(imagePath);
                    }

                    // Update the visibility of the clear button upon initialization
                    updateClearCoverArtVisibility();

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
                                imageViewClearCoverArt.setVisibility(View.GONE);
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
                                imageViewClearCoverArt.setVisibility(View.GONE);
                            }
                        }
                        // If the URI scheme is not supported, set the image to a placeholder
                        else {
                            imageViewGameCover.setImageResource(R.drawable.baseline_image_24);
                            imageViewClearCoverArt.setVisibility(View.GONE);
                        }
                    }

                    // Display the placeholder image if any exceptions are thrown when attempting to display the cover image
                    catch (Exception e) {
                        imageViewGameCover.setImageResource(R.drawable.baseline_image_24);
                        imageViewClearCoverArt.setVisibility(View.GONE);
                    }

                }

            }

            // Set the save button to update the video game
            buttonSave.setOnClickListener(updateVideoGame);

        }

        // Verify if the activity is being opened with the intention of creating a new video game
        if (intent != null && intent.hasExtra("FRAGMENT_ID") && !intent.hasExtra("VIDEO_GAME_ID")) {

            // Obtain the identifier of the fragment used to open this activity
            fragmentId = intent.getIntExtra("FRAGMENT_ID", -1);

            // Check the appropriate CheckBox based on the fragment ID
            switch (fragmentId) {
                case 0:
                    checkBoxBacklog.setChecked(true);
                    break;
                case 1:
                    checkBoxCollection.setChecked(true);
                    break;
                case 2:
                    checkBoxCompletion.setChecked(true);
                    break;
                case 3:
                    checkBoxWishlist.setChecked(true);
                    break;
                default:
                    break;
            }

        }

        // Verify if the activity is being opened from the details activity
        if (intent != null && intent.hasExtra("FORM_OPENED_FROM_DETAILS")) {
            openedFromDetailsActivity = intent.getIntExtra("FORM_OPENED_FROM_DETAILS", -1);
        }

        // Disable keyboard for EditText fields using dates
        disableKeyboard(editTextReleaseDate);
        disableKeyboard(editTextCompletionDate);

        // Set OnClickListeners for EditText fields using dates allowing to open a DatePickerDialog when clicked
        editTextReleaseDate.setOnClickListener(v -> showDatePickerDialog(editTextReleaseDate, calendarReleaseDate, false));
        editTextCompletionDate.setOnClickListener(v -> showDatePickerDialog(editTextCompletionDate, calendarCompletionDate, true));

        // Set OnFocusChangeListeners for EditText fields using dates to open a DatePickerDialog when focused
        editTextReleaseDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePickerDialog(editTextReleaseDate, calendarReleaseDate, false);
            }
        });
        editTextCompletionDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePickerDialog(editTextCompletionDate, calendarCompletionDate, true);
            }
        });

        // Verify if the cover art for the video game should be displayed
        if (sharedPreferencesImages.getInt(KEY_DISPLAY, 1) == 0) {
            textViewCoverArt.setVisibility(View.GONE);
            imageViewGameCover.setVisibility(View.GONE);
        }
        else {
            textViewCoverArt.setVisibility(View.VISIBLE);
            imageViewGameCover.setVisibility(View.VISIBLE);
        }

        // Set the activity label with white text color
        String activityLabel = getString(R.string.add_video_game);
        if (isEditMode) {
            activityLabel = getString(R.string.edit_video_game);
        }
        SpannableString spannableLabel = new SpannableString(activityLabel);
        spannableLabel.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.white)), 0, activityLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the colored activityLabel to the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(spannableLabel);
        }

    }

    /**
     * Code executed when the activity is destroyed allowing to save the state of the activity
     * @param outState Bundle in which to place your saved state
     *
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {

        // Save the state of the activity
        super.onSaveInstanceState(outState);

        // Save the URI of the selected cover art image if it exists
        if (imageViewGameCover.getTag() != null) {
            outState.putString("image_uri", imageViewGameCover.getTag().toString());
        }

    }

    /**
     * Code executed when the activity is restored allowing to restore the state of the activity
     * @param savedInstanceState the data most recently supplied in {@link #onSaveInstanceState}.
     *
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {

        // Restore the state of the activity
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the URI of the selected cover art image if it was saved
        if (savedInstanceState.containsKey("image_uri")) {
            String imageUriString = savedInstanceState.getString("image_uri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                imageViewGameCover.setImageURI(imageUri);
                imageViewGameCover.setTag(imageUriString);
            }
        }

    }


    /**
     * OnClick method allowing to submit the form and add a new video game into the database
     */
    private final View.OnClickListener addVideoGame = new View.OnClickListener() {

        /**
         * Method executed when the save button is clicked allowing to add a new video game
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {

            // Verify if the form is valid and display an error message if not
            if (formValidation()) {
                Toast.makeText(FormActivity.this, R.string.form_validation_error, Toast.LENGTH_LONG).show();
                return;
            }

            // Initialize values used for the price and playtime
            double price = 0;
            int playtime = -1;

            // Parse the value of the price EditText field to a double
            if (!editTextPrice.getText().toString().trim().isEmpty()) {
                String originalText = editTextPrice.getText().toString().trim();
                String updatedText = originalText.replace(',', '.');
                editTextPrice.setText(updatedText);
                price = Double.parseDouble(updatedText);
            }

            // Parse the value of the playtime EditText field to an integer
            if (!editTextPlaytime.getText().toString().trim().isEmpty()) {
                playtime = Integer.parseInt(editTextPlaytime.getText().toString().trim());
            }

            // Verify if a completion date was selected and set the value accordingly
            String completionDate = editTextCompletionDate.getText().toString().trim();
            if (completionDate.isEmpty()) {
                completionDate = null;
            }

            // Invoking the MySQLiteOpenHelper method allowing to add a new video game
            Boolean addResult = mySQLiteOpenHelper.addVideoGame(editTextTitle.getText().toString().trim(), editTextPlatform.getText().toString().trim(), editTextPublisher.getText().toString().trim(), editTextReleaseDate.getText().toString().trim(), completionDate, playtime, price, checkBoxBacklog.isChecked(), checkBoxCollection.isChecked(), checkBoxCompletion.isChecked(), checkBoxWishlist.isChecked(), String.valueOf(coverArtURI));

            // Display a message indicating the status of the insertion and return to the main activity
            if (addResult) {
                Toast.makeText(FormActivity.this, R.string.add_video_game_success, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FormActivity.this, MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(FormActivity.this, R.string.add_video_game_error, Toast.LENGTH_SHORT).show();
            }

        }

    };

    /**
     * OnClick method allowing to submit the form and update a game within the database
     */
    private final View.OnClickListener updateVideoGame = new View.OnClickListener() {

        /**
         * Method executed when the save button is clicked allowing to update a video game
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {

            // Verify if the form is valid and display an error message if not
            if (formValidation()) {
                Toast.makeText(FormActivity.this, R.string.form_validation_error, Toast.LENGTH_LONG).show();
                return;
            }

            // Initialize values used for the price and playtime
            double price = 0;
            int playtime = -1;

            // Parse the value of the price EditText field to a double
            if (!editTextPrice.getText().toString().trim().isEmpty()) {
                String originalText = editTextPrice.getText().toString().trim();
                String updatedText = originalText.replace(',', '.');
                editTextPrice.setText(updatedText);
                price = Double.parseDouble(updatedText);
            }

            // Parse the value of the playtime EditText field to an integer
            if (!editTextPlaytime.getText().toString().trim().isEmpty()) {
                playtime = Integer.parseInt(editTextPlaytime.getText().toString().trim());
            }

            // Verify if a completion date was selected and set the value accordingly
            String completionDate = editTextCompletionDate.getText().toString().trim();
            if (completionDate.isEmpty()) {
                completionDate = null;
            }

            // Invoking the MySQLiteOpenHelper method allowing to update a video game
            Boolean updateResult = mySQLiteOpenHelper.updateVideoGame(videoGameId, editTextTitle.getText().toString().trim(), editTextPlatform.getText().toString().trim(), editTextPublisher.getText().toString().trim(), editTextReleaseDate.getText().toString().trim(), completionDate, playtime, price, checkBoxBacklog.isChecked(), checkBoxCollection.isChecked(), checkBoxCompletion.isChecked(), checkBoxWishlist.isChecked(), String.valueOf(coverArtURI));

            // Display a message indicating the status of the update and return to the main activity
            if (updateResult) {
                Toast.makeText(FormActivity.this, R.string.update_video_game_success, Toast.LENGTH_SHORT).show();
                // Verify if the form's submission needs to redirect to the details activity or the main activity
                Intent intent;
                if (openedFromDetailsActivity == 1) {
                    intent = new Intent(FormActivity.this, DetailsActivity.class);
                    intent.putExtra("VIDEO_GAME_ID", videoGameId);
                }
                else {
                    intent = new Intent(FormActivity.this, MainActivity.class);
                }
                startActivity(intent);
            }
            else {
                Toast.makeText(FormActivity.this, R.string.update_video_game_error, Toast.LENGTH_SHORT).show();
            }

        }

    };

    /**
     * Method allowing to set a clear button for an EditText field that will be cleared when clicked
     * @param editText EditText field for which to set the clear button
     * @param clearButton Clear button for the EditText field
     * @param isReleaseDate Boolean indicating if the date is for the release date or not
     * @param isCompletionDate Boolean indicating if the date is for the completion date or not
     */
    private void setClearButton(final EditText editText, final View clearButton, boolean isReleaseDate, boolean isCompletionDate) {

        // Set a TextWatcher for the EditText field allowing to display a clear button when the field is not empty
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Set a OnClickListener for the clear button allowing to clear the field when clicked
        clearButton.setOnClickListener(v -> {

            // Clear the EditText field
            editText.setText("");

            // If the EditText field is for the release date, reset the corresponding Calendar object
            if (isReleaseDate) {
                calendarReleaseDate = Calendar.getInstance();
            }

            // If the EditText field is for the completion date, reset the corresponding Calendar object
            if (isCompletionDate) {
                calendarCompletionDate = Calendar.getInstance();
            }

        });

    }

    /**
     * Method allowing to update the visibility of the ImageView allowing to clear the cover art image selection
     */
    private void updateClearCoverArtVisibility() {
        // Verify if a cover art URI exists and update the visibility of the ImageView accordingly
        if (coverArtURI != null) {
            imageViewClearCoverArt.setVisibility(View.VISIBLE);
        }
        else {
            imageViewClearCoverArt.setVisibility(View.GONE);
        }
    }

    /**
     * Method allowing to verify if the form is valid
     * @return Status indicating whether the form is valid or not
     */
    private boolean formValidation() {

        // Boolean indicating if the form is valid or not
        boolean isValid = true;

        // Reset the visibility of the error messages
        textViewTitleValidation.setVisibility(View.GONE);
        textViewPlatformValidation.setVisibility(View.GONE);
        textViewPriceValidation.setVisibility(View.GONE);
        textViewPublisherValidation.setVisibility(View.GONE);
        textViewReleaseDateValidation.setVisibility(View.GONE);
        textViewCompletionDateValidation.setVisibility(View.GONE);
        textViewPlaytimeValidation.setVisibility(View.GONE);
        textViewSaveToValidation.setVisibility(View.GONE);

        // Verify if the title field is valid
        if (editTextTitle.getText().toString().trim().isEmpty()) {
            textViewTitleValidation.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Verify if the platform field is valid
        if (editTextPlatform.getText().toString().trim().isEmpty()) {
            textViewPlatformValidation.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Verify if the price field is valid (price presence)
        if (editTextPrice.getText().toString().trim().isEmpty()) {
            textViewPriceValidation.setText(R.string.required_field);
            textViewPriceValidation.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Verify if the price field is valid (price amount)
        if (!editTextPrice.getText().toString().trim().isEmpty()) {
            String originalText = editTextPrice.getText().toString().trim();
            String updatedText = originalText.replace(',', '.');
            editTextPrice.setText(updatedText);
            if (Double.parseDouble(editTextPrice.getText().toString().trim()) > 10000) {
                textViewPriceValidation.setText(R.string.price_validation_amount);
                textViewPriceValidation.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        // Verify if the price field is valid (price format)
        if (!editTextPrice.getText().toString().trim().isEmpty()) {
            String price = editTextPrice.getText().toString().trim();
            String updatedPrice = price.replace(',', '.');
            String pricePattern = "^[0-9]{1,5}(\\.[0-9]{2})?$";
            if (!updatedPrice.matches(pricePattern)) {
                textViewPriceValidation.setText(R.string.invalid_price_format);
                textViewPriceValidation.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        // Verify if the publisher field is valid
        if (editTextPublisher.getText().toString().trim().isEmpty()) {
            textViewPublisherValidation.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Verify if the release date field is valid
        if (editTextReleaseDate.getText().toString().trim().isEmpty()) {
            textViewReleaseDateValidation.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Verify if the completion date field is valid
        if (!editTextCompletionDate.getText().toString().trim().isEmpty()) {
            if (calendarCompletionDate.compareTo(calendarReleaseDate) < 0) {
                textViewCompletionDateValidation.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        // Verify if the playtime field is valid
        if (!editTextPlaytime.getText().toString().trim().isEmpty()) {
            if (Integer.parseInt(editTextPlaytime.getText().toString().trim()) > 10000) {
                textViewPlaytimeValidation.setVisibility(View.VISIBLE);
                isValid = false;
            }
        }

        // Verify if at least one of the checkbox fields are selected
        if (!checkBoxBacklog.isChecked() && !checkBoxCollection.isChecked() && !checkBoxCompletion.isChecked() && !checkBoxWishlist.isChecked()) {
            textViewSaveToValidation.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // Return the status indicating if the form is valid or not
        return !isValid;

    }

    /**
     * Method executed when the back button is pressed in the ActionBar
     * @param item The menu item that was selected
     * @return True if the menu item was selected, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // If the back button is pressed, display the confirmation box for cancelling form's submission
        if (item.getItemId() == android.R.id.home) {
            showCancelConfirmationDialog();
            return true;
        }

        // Return the selected MenuItem
        return super.onOptionsItemSelected(item);

    }

    /**
     * Method that opens a AlertDialog box in order to confirm the cancellation of the form's submission
     */
    private void showCancelConfirmationDialog() {

        // Create an AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the title of the AlertDialog box
        builder.setTitle(R.string.cancel_form_submission_title);

        // Set the message of the AlertDialog box
        builder.setMessage(R.string.cancel_form_submission_text);

        // Set the positive button to confirm the cancellation of the form's submission
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            // Verify if the form's submission needs to redirect to the details activity or the main activity
            Intent intent;
            if (openedFromDetailsActivity == 1) {
                intent = new Intent(FormActivity.this, DetailsActivity.class);
                intent.putExtra("VIDEO_GAME_ID", videoGameId);
            } else {
                intent = new Intent(FormActivity.this, MainActivity.class);
            }
            startActivity(intent);
        });


        // Set the negative button to cancel the cancellation of the form's submission
        builder.setNegativeButton(R.string.cancel, null);

        // Show the AlertDialog box
        builder.show();

    }

    /**
     * Method allowing to set a FocusChangeListener for EditText fields allowing to set the cursor at the beginning of the field when unselected
     * @param editText EditText field for which to set the focus change listener
     */
    private void setFocusChangeListener(EditText editText) {

        // Set a FocusChangeListener for the EditText field
        editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                editText.setSelection(0);
            }
        });

    }

    /**
     * Method allowing to open a DatePickerDialog when clicking on an EditText field that uses dates
     * @param editText EditText field for which to open the DatePickerDialog for
     * @param calendar Calendar object for which to set the date using the DatePickerDialog
     * @param isCompletionDate Boolean indicating if the date is for the completion date or not
     */
    private void showDatePickerDialog(EditText editText, Calendar calendar, boolean isCompletionDate) {

        // Do not show the DatePickerDialog if an instance is already open
        if (isDatePickerDialogOpen) {
            return;
        }

        // Set the flag indicating that the DatePickerDialog is open to true
        isDatePickerDialogOpen = true;

        // Obtain the date from the Calendar object in order to set the initial date in the DatePickerDialog
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create the DatePickerDialog and set the required parameters
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, yearDatePickerDialog, monthDatePickerDialog, dayDatePickerDialog) -> {

            // Set the selected date from the DatePickerDialog on the Calendar
            calendar.set(Calendar.YEAR, yearDatePickerDialog);
            calendar.set(Calendar.MONTH, monthDatePickerDialog);
            calendar.set(Calendar.DAY_OF_MONTH, dayDatePickerDialog);

            // Format the date that will be displayed in the EditText
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.CANADA);

            // Set the date selected using the DatePickerDialog on the EditText
            editText.setText(simpleDateFormat.format(calendar.getTime()));
        }, year, month, day);

        // Set the flag indicating that the DatePickerDialog is open to false when the dialog is dismissed
        datePickerDialog.setOnDismissListener(dialog -> isDatePickerDialogOpen = false);

        // Disable future dates for the completion date
        if (isCompletionDate) {
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        }

        // Show the DatePickerDialog
        datePickerDialog.show();

    }

    /**
     * Method allowing to disable the keyboard for EditText fields that use dates
     * @param editText EditText field for which to disable the keyboard
     */
    @SuppressLint("ClickableViewAccessibility")
    private void disableKeyboard(EditText editText) {

        // Set the input type of the EditText field to TYPE_NULL to disable the keyboard
        editText.setInputType(InputType.TYPE_NULL);

        // Set a TouchListener for the EditText field allowing to close the keyboard when selected
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                v.performClick();
            }
            return false;
        });

    }

    /**
     * Method allowing to open the gallery used to select an image for the cover art
     */
    @SuppressLint("IntentReset")
    private void openGallery() {

        // Set the flag indicating that an image is currently being selected from the gallery to true
        isImageSelected = true;

        // For Android 13 and above, use the photo picker in order to select an image from the gallery
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        }

        // For Android 12 and below, request storage permissions before selecting an image from the gallery
        else {
            // Verify if the permission to read external storage has not been granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission to read external storage
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
            }
            // Verify if the permission to read external storage has already been granted
            else {
                // Launch the gallery intent if the permissions were granted
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                galleryLauncher.launch(intent);
            }
        }

        // Set the flag indicating that an image is currently being selected from the gallery to false
        isDatePickerDialogOpen = false;

    }

    /**
     * Method executed when the permission request is completed
     * @param requestCode The request code passed for the permission request
     * @param permissions The requested permissions which must never be null
     * @param grantResults The grant results for the corresponding permissions which must never be null
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Call the super method allowing to handle the results of the permission request
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Verify if the request code matches the request code for the permission to read external storage
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL_STORAGE) {
            // If the permission has been granted, launch the gallery intent
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(intent);
            }
            // If the permission has not been granted, show a message to the user
            else {
                Toast.makeText(this, R.string.permissions_denied_gallery, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
