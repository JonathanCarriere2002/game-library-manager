/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      RecyclerViewAdapter.java     ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

import  android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * RecyclerViewAdapter containing the detailed view of video games within the backlog, collection, completion and wishlist
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    // Integer indicating whether or not images should be displayed
    private final int imageDisplayMode;

    // Integer indicating whether or not confirmation should be asked when deleting a video game
    private final int deletionConfirmation;

    // Context used for the RecyclerViewAdapter
    private final Context context;

    // String used to indicate which fragment the RecyclerViewAdapter is utilized in
    private final String fragmentName;

    // TextView used to display a message if no video games were found when searching
    private final TextView textViewResultsFound;

    // ArrayList received as parameters containing the data that will be displayed
    private ArrayList<VideoGame> videoGames;

    // ArrayList used when the RecyclerView is filtered
    private ArrayList<VideoGame> videoGamesCopy;
    
    // Animation used to enable smooth scrolling in the recycler view
    Animation recyclerViewTranslate;

    // MySQLiteOpenHelper allowing to manage the database
    MySQLiteOpenHelper mySQLiteOpenHelper;

    /**
     * Constructor for the DetailedRecyclerAdapter object
     * @param context Context received from the activity
     * @param fragmentName String indicating which fragment the RecyclerViewAdapter is utilized in
     * @param textViewResultsFound TextView used to display a message if no video games were found
     * @param videoGames ArrayList containing VideoGame objects
     * @param imageDisplayMode Integer stored in the SharedPreferences indicating if images should be displayed or not
     * @param deletionConfirmation Integer stored in the SharedPreferences indicating if confirmation should be asked when deleting a video game
     */
    RecyclerViewAdapter(Context context, String fragmentName, TextView textViewResultsFound, ArrayList<VideoGame> videoGames, int imageDisplayMode, int deletionConfirmation) {

        // Association between the class's properties and the parameters received by the constructor
        this.context = context;
        this.fragmentName = fragmentName;
        this.textViewResultsFound = textViewResultsFound;
        this.videoGames = videoGames;
        this.imageDisplayMode = imageDisplayMode;
        this.deletionConfirmation = deletionConfirmation;

        // Initialization of the ArrayLists used when the RecyclerView is filtered by copying the original ArrayLists into them
        this.videoGamesCopy = new ArrayList<>(videoGames);

        // Initialization of the MySQLiteOpenHelper
        mySQLiteOpenHelper = new MySQLiteOpenHelper(context);

    }

    /**
     * Called when RecyclerView needs a new MyViewHolder of the given type to represent an item.
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position
     * @param viewType The view type of the new View
     * @return A new MyViewHolder that holds a View of the given view type
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Inflate the layout for the RecyclerView item and return a MyViewHolder containing a specific video game
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_adapter_item, parent, false);
        return new MyViewHolder(view);

    }

    /**
     *  Method allowing to bind the data received by the MyViewHolder to the fields in the RecyclerView
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set
     * @param position The position of the item within the adapter's data set
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // Setting text to each corresponding field in the RecyclerView
        holder.textViewTitle.setText(String.valueOf(videoGames.get(position).getTitle()));
        holder.textViewPlatform.setText(String.valueOf(videoGames.get(position).getPlatform()));
        holder.textViewPublisher.setText(String.valueOf(videoGames.get(position).getPublisher()));
        holder.textViewDate.setText(String.valueOf(videoGames.get(position).getReleaseDate()));

        // Verify if images should be displayed in the RecyclerView
        if (imageDisplayMode == 0) {
            // Set the visibility of the ImageView to GONE if images should not be displayed
            holder.imageViewCoverArt.setVisibility(View.GONE);
        }
        else {
            // Set the visibility of the ImageView to VISIBLE if images should be displayed
            holder.imageViewCoverArt.setVisibility(View.VISIBLE);
            // Obtain the URI of the cover art image associated to the video game
            Uri imageUri = Uri.parse(videoGames.get(position).getImagePath());
            // Attempt to load the cover art image using the URI
            try {
                // Verify if the file corresponding to the URI exists
                if (Objects.equals(imageUri.getScheme(), "file")) {
                    File imageFile = new File(imageUri.getPath());
                    // If the image file exists, set the image to the ImageView
                    if (imageFile.exists()) {
                        holder.imageViewCoverArt.setImageURI(imageUri);
                    }
                    // If the image file does not exist, set the image to a placeholder
                    else {
                        holder.imageViewCoverArt.setImageResource(R.drawable.baseline_image_24);
                    }
                }
                // If the URI scheme is content, use ContentResolver to load the image
                else if (Objects.equals(imageUri.getScheme(), "content")) {
                    ContentResolver contentResolver = holder.imageViewCoverArt.getContext().getContentResolver();
                    InputStream inputStream = contentResolver.openInputStream(imageUri);
                    // If the input stream is not null, set the image to the ImageView
                    if (inputStream != null) {
                        inputStream.close();
                        holder.imageViewCoverArt.setImageURI(imageUri);
                    }
                    // If the input stream is null, set the image to a placeholder
                    else {
                        holder.imageViewCoverArt.setImageResource(R.drawable.baseline_image_24);
                    }
                }
                // If the URI scheme is not supported, set the image to a placeholder
                else {
                    holder.imageViewCoverArt.setImageResource(R.drawable.baseline_image_24);
                }
            }
            // Display the placeholder image if any exceptions are thrown when attempting to display the cover image
            catch (Exception e) {
                holder.imageViewCoverArt.setImageResource(R.drawable.baseline_image_24);
            }
        }


        // Set the text of the category specific TextView based on the currently active fragment
        switch (fragmentName) {

            // Set the playtime in the category specific TextView if the currently active fragment is the backlog
            case "backlog":
                if (videoGames.get(position).getPlaytime() <= 0) {
                    holder.textViewCategory.setText(context.getString(R.string.playtime_no));
                }
                else if (videoGames.get(position).getPlaytime() == 1) {
                    holder.textViewCategory.setText(videoGames.get(position).getPlaytime() + " " + context.getString(R.string.hour));
                }
                else {
                    holder.textViewCategory.setText(videoGames.get(position).getPlaytime() + " " + context.getString(R.string.hours));
                }
                break;

            // Set the price in the category specific TextView if the currently active fragment is the collection
            case "collection":
                if (videoGames.get(position).getPrice() <= 0) {
                    holder.textViewCategory.setText(context.getString(R.string.free));
                }
                else {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    String formattedPrice = decimalFormat.format(videoGames.get(position).getPrice());
                    holder.textViewCategory.setText("$" + formattedPrice);
                }
                break;

            // Set the completion date in the release date TextView and set the playtime in the category specific TextView if the currently active fragment is the completion list
            case "completion":
                // Set the completion date in the corresponding TextView
                if (videoGames.get(position).getCompletionDate() == null) {
                    holder.textViewDate.setText(context.getString(R.string.completion_date_no));
                }
                else {
                    holder.textViewDate.setText(String.valueOf(videoGames.get(position).getCompletionDate()));
                }
                // Set the playtime in the corresponding TextView
                if (videoGames.get(position).getPlaytime() <= 0) {
                    holder.textViewCategory.setText(context.getString(R.string.playtime_no));
                }
                else if (videoGames.get(position).getPlaytime() == 1) {
                    holder.textViewCategory.setText(videoGames.get(position).getPlaytime() + " " + context.getString(R.string.hour));
                }
                else {
                    holder.textViewCategory.setText(videoGames.get(position).getPlaytime() + " " + context.getString(R.string.hours));
                }
                break;

            // Set the price in the category specific TextView if the currently active fragment is the wishlist
            case "wishlist":
                if (videoGames.get(position).getPrice() <= 0) {
                    holder.textViewCategory.setText(context.getString(R.string.free));
                }
                else {
                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    String formattedPrice = decimalFormat.format(videoGames.get(position).getPrice());
                    holder.textViewCategory.setText("$" + formattedPrice);
                }
                break;

        }

        // Bind the position of the video game to the ViewHolder
        holder.bind(position);

    }

    /**
     * Returns the total number of items in the data set held by the RecyclerView
     * @return Total number of items in the data set held by the RecyclerView
     */
    @Override
    public int getItemCount() {

        // Return the number of items in the ArrayList
        return videoGames.size();

    }

    /**
     * Method allowing to display a message if no video games were found within the current fragment
     */
    public void notifyResultsFound() {

        // Verify if the RecyclerView contains any video games and set the visibility of the TextView accordingly
        if (getItemCount() != 0) {
            textViewResultsFound.setVisibility(View.INVISIBLE);
        }
        else {
            textViewResultsFound.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Method allowing to search for and filter video games within the RecyclerView using the video game titles
     * @param query Search query text used to update the RecyclerView
     */
    @SuppressLint("NotifyDataSetChanged")
    public void search(String query) {

        // Sanitize the search query text by removing special characters and extra spaces
        query = query.replaceAll("[^a-zA-Z0-9 ]", "");
        query = query.replaceAll("\\s{2,}", " ");
        query = query.trim();

        // Clear the original ArrayLists of video games
        videoGames.clear();

        // If the search query text is empty, restore the original ArrayLists of video games
        if (query.isEmpty()) {

            // Restore the original ArrayLists of video games
            videoGames.addAll(videoGamesCopy);

        }

        // If the search query text is not empty, search for video games with a title matching the search query text
        else {

            // Loop allowing to obtain the video games with a title matching the search query
            for (int index = 0; index < videoGamesCopy.size(); index++) {

                // Get the title of the video game at the current index
                String title = videoGamesCopy.get(index).getTitle();

                // Sanitize the title by removing special characters and extra spaces
                String sanitizedTitle = title.replaceAll("[^a-zA-Z0-9 ]", "").replaceAll("\\s{2,}", " ").trim();

                // If the video game title matches the search query text, add it to the displayed ArrayLists of video games
                if (sanitizedTitle.toLowerCase().startsWith(query.toLowerCase())) {

                    // Add the video game to the displayed ArrayLists of video games
                    videoGames.add(videoGamesCopy.get(index));

                }

            }

        }

        // Notify the RecyclerView that the displayed data has been updated
        notifyDataSetChanged();

        // Verify if the RecyclerView currently contains any video games and set the visibility of the TextView accordingly
        notifyResultsFound();

    }

    /**
     * Method allowing to refresh the displayed ArrayLists of video games when the RecyclerView is filtered
     * @param videoGamesRefreshed ArrayList containing refreshed VideoGame data
     */
    @SuppressLint("NotifyDataSetChanged")
    public void refreshOriginalData(ArrayList<VideoGame> videoGamesRefreshed) {

        // Refresh the original ArrayList of video games using the updated values
        this.videoGames = videoGamesRefreshed;

        // Notify the adapter that the data has changed
        notifyDataSetChanged();

    }

    /**
     * Method allowing to copy the original ArrayLists of video games into the ArrayLists used when the RecyclerView is filtered
     */
    public void refreshCopyData() {

        // Copy the original ArrayLists of video games into the ArrayLists used when the RecyclerView is filtered
        this.videoGamesCopy = new ArrayList<>(videoGames);

    }

    /**
     * Class containing the fields of the RecyclerView item
     */
     public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Position of the video game in the RecyclerView
        int position;

        // ImageButton containing additional actions for each row of the RecyclerView
        ImageButton imageButtonMoreActions;

        // ImageView used to display the cover art of each video game
        ImageView imageViewCoverArt;

        // LinearLayout representing a row in the RecyclerView
        LinearLayout linearLayout;

        // TextView objects used to display the data of each video game
        TextView textViewTitle, textViewPlatform, textViewPublisher, textViewDate, textViewCategory;

        /**
         * Constructor for the MyViewHolder object
         * @param itemView Individual RecyclerView item
         */
        public MyViewHolder(@NonNull View itemView) {

            // Initialisation of the MyViewHolder object
            super(itemView);

            // Association between the class's properties and the layout elements
            imageButtonMoreActions = itemView.findViewById(R.id.imageButtonMoreActions);
            imageViewCoverArt = itemView.findViewById(R.id.imageView);
            linearLayout = itemView.findViewById(R.id.recyclerViewRow);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewPlatform = itemView.findViewById(R.id.textViewPlatform);
            textViewPublisher = itemView.findViewById(R.id.textViewPublisher);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewCategory = itemView.findViewById(R.id.textViewSaveTo);

            // OnClickListener event for the for the imageButtonMoreActions image button
            imageButtonMoreActions.setOnClickListener(this);

            // OnClickListener event for a video game within the RecyclerView allowing to open the details page
            linearLayout.setOnClickListener(view -> {

                // Open the details page of a the selected video game while passing the ID as an extra
                Intent intent = new Intent(itemView.getContext(), DetailsActivity.class);
                intent.putExtra("VIDEO_GAME_ID", Integer.valueOf(videoGames.get(position).getId()));
                itemView.getContext().startActivity(intent);

            });

            // Set animation for RecyclerView row
            recyclerViewTranslate = AnimationUtils.loadAnimation(context, R.anim.recycler_view_translate);
            linearLayout.setAnimation(recyclerViewTranslate);

        }

        /**
         * Bind the position to the ViewHolder
         * @param position Position of the item in the RecyclerView
         */
        public void bind(int position) {

            // Bind the position of the video game within the RecyclerView to the ViewHolder
            this.position = position;

        }

        /**
         * onClick method for the image button allowing to open the action menu
         * @param view RecyclerView item that was clicked
         */
        @Override
        public void onClick(View view) {

            // Clicking on the image button corresponding to more actions opens the action menu
            showBottomSheetMenu(view);

        }

        /**
         * Method allowing to display the BottomSheetDialog menu containing additional actions for each video game
         * @param view View in which the BottomSheetDialog will be displayed
         */
        private void showBottomSheetMenu(View view) {

            // Initialization of the BottomSheetDialog
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.menu_action, view.findViewById(R.id.linearLayoutActionMenu));

            // Find TextView elements in the BottomSheetDialog
            TextView textViewBacklog = bottomSheetView.findViewById(R.id.textViewBacklog);
            TextView textViewCollection = bottomSheetView.findViewById(R.id.textViewCollection);
            TextView textViewCompletion = bottomSheetView.findViewById(R.id.textViewCompletion);
            TextView textViewWishlist = bottomSheetView.findViewById(R.id.textViewWishlist);

            // Set the text of the menu items based on the status of the video game
            updateTextView(textViewBacklog, videoGames.get(position).isBacklog(), "Backlog");
            updateTextView(textViewCollection, videoGames.get(position).isCollection(), "Collection");
            updateTextView(textViewCompletion, videoGames.get(position).isCompletion(), "Completion");
            updateTextView(textViewWishlist, videoGames.get(position).isWishlist(), "Wishlist");

            // Show the BottomSheetDialog
            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();

            // Code executed if the user selects the "edit" option in the menu
            bottomSheetView.findViewById(R.id.textViewEdit).setOnClickListener(v -> {

                // Create an Intent to start FormActivity
                Intent intent = new Intent(itemView.getContext(), FormActivity.class);

                // Pass the video game ID as an extra
                intent.putExtra("VIDEO_GAME_ID", Integer.valueOf(videoGames.get(position).getId()));

                // Start FormActivity
                itemView.getContext().startActivity(intent);

                // Dismiss the BottomSheetDialog
                bottomSheetDialog.dismiss();

            });

            // Code executed if the user selects the "delete" option in the menu
            bottomSheetView.findViewById(R.id.textViewDelete).setOnClickListener(v -> {

                // Show a confirmation dialog box to confirm the deletion of the video game
                showDeleteConfirmationDialog(position, false);

                // Dismiss the BottomSheetDialog
                bottomSheetDialog.dismiss();

            });

            // Code executed if the user selects the "backlog" option in the menu
            bottomSheetView.findViewById(R.id.textViewBacklog).setOnClickListener(v -> {

                // Update the status of the video game in the backlog
                updateCategory(videoGames, videoGamesCopy, "backlog", position, context.getString(R.string.save_backlog_success), context.getString(R.string.save_backlog_error), context.getString(R.string.remove_backlog_success), context.getString(R.string.remove_backlog_error));

                // Dismiss the BottomSheetDialog
                bottomSheetDialog.dismiss();

            });

            // Code executed if the user selects the "collection" option in the menu
            bottomSheetView.findViewById(R.id.textViewCollection).setOnClickListener(v -> {

                // Update the status of the video game in the collection
                updateCategory(videoGames, videoGamesCopy, "collection", position, context.getString(R.string.save_collection_success), context.getString(R.string.save_collection_error), context.getString(R.string.remove_collection_success), context.getString(R.string.remove_collection_error));

                // Dismiss the BottomSheetDialog
                bottomSheetDialog.dismiss();

            });

            // Code executed if the user selects the "completion" option in the menu
            bottomSheetView.findViewById(R.id.textViewCompletion).setOnClickListener(v -> {

                // Update the status of the video game in the completion list
                updateCategory(videoGames, videoGamesCopy, "completion", position, context.getString(R.string.save_completion_success), context.getString(R.string.save_completion_error), context.getString(R.string.remove_completion_success), context.getString(R.string.remove_completion_error));

                // Dismiss the BottomSheetDialog
                bottomSheetDialog.dismiss();

            });

            // Code executed if the user selects the "wishlist" option in the menu
            bottomSheetView.findViewById(R.id.textViewWishlist).setOnClickListener(v -> {

                // Update the status of the video game in the wishlist
                updateCategory(videoGames, videoGamesCopy, "wishlist", position, context.getString(R.string.save_wishlist_success), context.getString(R.string.save_wishlist_error), context.getString(R.string.remove_wishlist_success), context.getString(R.string.remove_wishlist_error));

                // Dismiss the BottomSheetDialog
                bottomSheetDialog.dismiss();

            });

        }

        /**
         * Method that opens a AlertDialog box in order to confirm the deletion of a video game
         * @param position The position of the video game to be deleted within the RecyclerView
         * @param isFinalCategory Boolean indicating whether or not the video game is being removed from its final category
         */
        private void showDeleteConfirmationDialog(int position, Boolean isFinalCategory) {

            // Get the ID of the video game at the specified position
            int videoGameId = videoGames.get(position).getId();

            // Verify if confirmation should be asked when deleting a video game
            if (deletionConfirmation == 1) {

                // Create an AlertDialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Set the title of the AlertDialog box
                builder.setTitle(context.getString(R.string.delete) + " " + videoGames.get(position).getTitle() + "?");

                // Conditionally set the message of the AlertDialog box
                if (isFinalCategory) {
                    builder.setMessage(context.getString(R.string.delete_message_final) + " " + context.getString(R.string.delete_message));
                }
                else {
                    builder.setMessage(context.getString(R.string.delete_message));
                }

                // Set the positive button to confirm the deletion of the video game
                builder.setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
                    deleteVideoGame(videoGames.get(position).getId());
                    removeItem(videoGameId);
                });

                // Set the negative button to cancel the deletion of the video game
                builder.setNegativeButton(context.getString(R.string.cancel), null);

                // Show the AlertDialog box
                builder.show();

            }

            // If no confirmation is required, simply delete the video game
            else {
                deleteVideoGame(videoGameId);
                removeItem(videoGameId);
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
                    Toast.makeText(context, context.getString(R.string.delete_video_game_success), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, context.getString(R.string.delete_video_game_error), Toast.LENGTH_SHORT).show();
                }

            }

            // Catch any exceptions when trying to delete the video game from the database
            catch (Exception e) {

                // Display a message if any error occurs
                Toast.makeText(context, context.getString(R.string.delete_video_game_error), Toast.LENGTH_SHORT).show();

            }

        }

        /**
         * Method that removes the video game with the specified ID from the RecyclerView
         * @param id The ID of the video game to be removed
         */
        private void removeItem(int id) {

            // Initialize the position variables for both the original and copy lists
            int originalPosition = -1;
            int copyPosition = -1;

            // Find the position in the original list
            for (int i = 0; i < videoGames.size(); i++) {

                if (videoGames.get(i).getId() == id) {

                    // Set the position in the original list
                    originalPosition = i;
                    break;

                }

            }

            // Find the position in the copy list
            for (int i = 0; i < videoGamesCopy.size(); i++) {

                if (videoGamesCopy.get(i).getId() == id) {

                    // Set the position in the copy list
                    copyPosition = i;
                    break;

                }

            }

            // Remove from original list if found
            if (originalPosition != -1) {

                // Remove the video game from the original list
                videoGames.remove(originalPosition);

                // Notify RecyclerView about item removal
                notifyItemRemoved(originalPosition);
                notifyItemRangeChanged(originalPosition, getItemCount());

            }

            // Remove from copy list if found
            if (copyPosition != -1) {

                // Remove the video game from the copy list
                videoGamesCopy.remove(copyPosition);

            }

            // Verify if the RecyclerView currently contains any video games and set the visibility of the TextView accordingly
            notifyResultsFound();

        }

        /**
         * Method allowing to update the status of a video game in the backlog, collection, completion or wishlist
         * @param categoryList ArrayList containing the status of the video games for the specific category
         * @param categoryListCopy ArrayList containing the status of the video games for the specific category
         * @param category Category in which the video game will be added or removed
         * @param position Position of the video game in the RecyclerView
         * @param saveSuccessMessage Message to display when the video game is saved successfully
         * @param saveErrorMessage Message to display when the video game is not saved successfully
         * @param removeSuccessMessage Message to display when the video game is removed successfully
         * @param removeErrorMessage Message to display when the video game is not removed successfully
         */
        private void updateCategory(ArrayList<VideoGame> categoryList, ArrayList<VideoGame> categoryListCopy, String category, int position, String saveSuccessMessage, String saveErrorMessage, String removeSuccessMessage, String removeErrorMessage) {

            // Get the ID of the video game at the specified position
            int videoGameId = videoGames.get(position).getId();

            // Initialize the position variables for both the original and copy lists
            int originalPosition = -1;
            int copyPosition = -1;

            // Initialize the status of the video game within the specified category
            boolean categoryStatus = false;

            // Find the position in the original list
            for (int i = 0; i < videoGames.size(); i++) {

                if (videoGames.get(i).getId() == videoGameId) {

                    // Set the position in the original list
                    originalPosition = i;
                    break;

                }

            }

            // Find the position in the copy list
            for (int i = 0; i < videoGamesCopy.size(); i++) {

                if (videoGamesCopy.get(i).getId() == videoGameId) {

                    // Set the position in the copy list
                    copyPosition = i;
                    break;

                }

            }

            // Obtain the status of the video within the specified category
            switch (category) {

                case "backlog": {
                    categoryStatus = categoryList.get(position).isBacklog();
                    break;
                }
                case "collection": {
                    categoryStatus = categoryList.get(position).isCollection();
                    break;
                }
                case "completion": {
                    categoryStatus = categoryList.get(position).isCompletion();
                    break;
                }
                case "wishlist": {
                    categoryStatus = categoryList.get(position).isWishlist();
                    break;
                }

            }

            // Verify if the video game is already saved in the specified category
            if (categoryStatus) {

                // Verify if the video game will not be removed from its final category
                if (mySQLiteOpenHelper.getCategoryStatusTotal(videoGameId) != 1) {

                    // Update the status of the video game in the specified category by removing it
                    Boolean updateCategoryStatus = mySQLiteOpenHelper.updateCategoryStatus(category, videoGameId, 0);

                    // Update the status of the video game in the specified category within the ArrayList
                    if (originalPosition != -1) {
                        updateCategoryStatus(category, categoryList, originalPosition, false);
                    }
                    if (copyPosition != -1) {
                        updateCategoryStatus(category, categoryListCopy, copyPosition, false);
                    }

                    // If the video game is removed from the currently active fragment, remove it from the RecyclerView
                    if (category.equals(fragmentName)) {
                        removeItem(videoGameId);
                    }

                    // Display a message indicating the status of the update of the video game
                    if (updateCategoryStatus) {
                        Toast.makeText(context, removeSuccessMessage, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, removeErrorMessage, Toast.LENGTH_SHORT).show();
                    }

                }

                // If the video game will be removed from its final category, delete if from the database
                else {

                    // Show the confirmation box allowing to confirm the deletion of the video game
                    showDeleteConfirmationDialog(position, true);

                }

            }

            // Verify if the video game is not already saved in the specified category
            else {

                // Update the status of the video game in the specified category by adding it
                Boolean updateCategoryStatus = mySQLiteOpenHelper.updateCategoryStatus(category, videoGameId, 1);

                // Update the status of the video game in the specified category within the ArrayList
                if (originalPosition != -1) {
                    updateCategoryStatus(category, categoryList, originalPosition, true);
                }
                if (copyPosition != -1) {
                    updateCategoryStatus(category, categoryListCopy, copyPosition, true);
                }

                // Display a message indicating the status of the update of the video game
                if (updateCategoryStatus) {
                    Toast.makeText(context, saveSuccessMessage, Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, saveErrorMessage, Toast.LENGTH_SHORT).show();
                }

            }

        }

        /**
         * Method allowing to update the value associated to a category within a specific ArrayList
         * @param category Category in which the position of the video game will be set
         * @param videoGames ArrayList containing the video game which will have its category updated
         * @param position Position of the video game in the RecyclerView
         * @param categoryStatus Status of the video game indicating whether or not it is saved within the specified category
         */
        private void updateCategoryStatus(String category, ArrayList<VideoGame> videoGames, int position, boolean categoryStatus) {

            // Switch case allowing to set the value of the category within the ArrayList
            switch (category) {
                case "backlog": {
                    videoGames.get(position).setBacklog(categoryStatus);
                    break;
                }
                case "collection": {
                    videoGames.get(position).setCollection(categoryStatus);
                    break;
                }
                case "completion": {
                    videoGames.get(position).setCompletion(categoryStatus);
                    break;
                }
                case "wishlist": {
                    videoGames.get(position).setWishlist(categoryStatus);
                    break;
                }
            }

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
                        textView.setText(context.getString(R.string.remove_backlog));
                        break;
                    case "Collection":
                        textView.setText(context.getString(R.string.remove_collection));
                        break;
                    case "Completion":
                        textView.setText(context.getString(R.string.remove_completion));
                        break;
                    case "Wishlist":
                        textView.setText(context.getString(R.string.remove_wishlist));
                        break;
                }

            }

            // If the video game is not already within the specified category, display "save" text
            else {

                // Switch case allowing to set the text of the TextView based on the category
                switch (category) {
                    case "Backlog":
                        textView.setText(context.getString(R.string.save_backlog));
                        break;
                    case "Collection":
                        textView.setText(context.getString(R.string.save_collection));
                        break;
                    case "Completion":
                        textView.setText(context.getString(R.string.save_completion));
                        break;
                    case "Wishlist":
                        textView.setText(context.getString(R.string.save_wishlist));
                        break;
                }

            }

        }

    }

}
