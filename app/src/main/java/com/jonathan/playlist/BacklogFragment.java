/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      BacklogFragment.java         ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Fragment pertaining to displaying and managing the user's video game backlog
 */
public class BacklogFragment extends Fragment implements VideoGameFragment {

    // ArrayLists containing the video games that will be displayed
    ArrayList<VideoGame> videoGames;

    // MyRecyclerViewAdapter used for displaying games in the RecyclerView
    RecyclerViewAdapter recyclerViewAdapter;

    // MySQLiteOpenHelper allowing to manage the database
    MySQLiteOpenHelper mySQLiteOpenHelper;

    // RecyclerView displaying the video games in the backlog
    RecyclerView recyclerView;

    // TextView displaying a message if no games are found
    TextView textViewBacklog;

    // SharedPreferences used to store sorting menu selections for the backlog
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SortingPreferencesBacklog";
    private static final String KEY_SORT_OPTION = "SortOption";
    private static final String KEY_SORT_ORDER = "SortOrder";

    // SharedPreferences used to store image display settings
    SharedPreferences sharedPreferencesImages;
    private static final String PREFS_NAME_DISPLAY = "DisplayPreferences";
    private static final String KEY_DISPLAY = "DisplayOption";

    // SharedPreferences used to store confirmation verification settings
    SharedPreferences sharedPreferencesVerification;
    private static final String PREFS_NAME_CONFIRMATION = "ConfirmationPreferences";
    private static final String KEY_CONFIRMATION = "ConfirmationOption";

    /**
     * Code executed upon initialization of the fragment allowing to obtain the context for the database
     * @param context Context used for the database
     */
    @Override
    public void onAttach(@NonNull Context context) {

        // Attaching the context of the activity to the fragment
        super.onAttach(context);
        mySQLiteOpenHelper = new MySQLiteOpenHelper(context);

    }

    /**
     * Code executed upon initialization of the fragment allowing to display it
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backlog, container, false);

        // Association between fragment objects and layout elements
        recyclerView = view.findViewById(R.id.recyclerViewBacklog);
        textViewBacklog = view.findViewById(R.id.textViewBacklog);

        // Initialization of the data that will be displayed within the fragment
        videoGames = new ArrayList<>();

        // Initialize SharedPreferences
        if ((getContext() != null) && (getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) != null)) {
            sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

        // Initialize SharedPreferences for the images
        if ((getContext() != null) && (getContext().getSharedPreferences(PREFS_NAME_DISPLAY, Context.MODE_PRIVATE) != null)) {
            sharedPreferencesImages = getContext().getSharedPreferences(PREFS_NAME_DISPLAY, Context.MODE_PRIVATE);
        }

        // Initialize SharedPreferences for deletion verification
        if ((getContext() != null) && (getContext().getSharedPreferences(PREFS_NAME_CONFIRMATION, Context.MODE_PRIVATE) != null)) {
            sharedPreferencesVerification = getContext().getSharedPreferences(PREFS_NAME_CONFIRMATION, Context.MODE_PRIVATE);
        }

        // Method allowing to obtain all video games from the backlog
        obtainFragmentVideoGames();

        // Initialize the RecyclerView in order to display all video games from the backlog
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), getFragmentName(), textViewBacklog, videoGames, sharedPreferencesImages.getInt(KEY_DISPLAY, 1), sharedPreferencesVerification.getInt(KEY_CONFIRMATION, 1));
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Return the inflated fragment once initialization as been completed
        return view;

    }

    /**
     * Code executed when the fragment becomes visible to the user
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {

        // Refresh the RecyclerView in order to display an updated version of the backlog
        super.onResume();

        // Clear the current data displayed in the backlog
        videoGames.clear();

        // Refresh the data displayed in the backlog
        obtainFragmentVideoGames();
        recyclerViewAdapter.refreshCopyData();

        // Reinitialize the adapter with the updated data
        recyclerViewAdapter.notifyDataSetChanged();

    }

    /**
     * Method allowing to obtain the name of the currently active fragment
     * @return Name of the currently active fragment
     */
    @Override
    public String getFragmentName() {

        // Return the name of the currently active fragment
        return "backlog";

    }

    /**
     * Method allowing to filter the RecyclerView of a fragment by searching for video game titles
     * @param searchQuery The searchQuery used to search for video game titles
     */
    @Override
    public void searchRecyclerView(String searchQuery) {

        // Verify that the detailedRecyclerAdapter is not null
        if (recyclerViewAdapter != null) {
            // Search for video games within the RecyclerView
            recyclerViewAdapter.search(searchQuery);
        }

    }

    /**
     * Method allowing to save the sorting option within a specific fragment in SharedPreferences
     * @param value Value of the sorting option to be saved
     */
    @Override
    public void saveSortOption(int value) {

        // Save the selected sorting option in the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SORT_OPTION, value);
        editor.apply();

    }

    /**
     * Method allowing to obtain the saved sorting option within a specific fragment from SharedPreferences
     * @return Value of the saved sorting option
     */
    @Override
    public int getSavedSortOption() {

        // Return the saved sorting option from the SharedPreferences
        return sharedPreferences.getInt(KEY_SORT_OPTION, R.id.menuGroupItemTitle);

    }

    /**
     * Method allowing to save the ordering option within a specific fragment in SharedPreferences
     * @param value Value of the ordering option to be saved
     */
    @Override
    public void saveSortOrder(int value) {

        // Save the selected ordering option in the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_SORT_ORDER, value);
        editor.apply();

    }

    /**
     * Method allowing to obtain the saved ordering option within a specific fragment from SharedPreferences
     * @return Value of the saved ordering option
     */
    @Override
    public int getSavedSortOrder() {

        // Return the saved ordering option from the SharedPreferences
        return sharedPreferences.getInt(KEY_SORT_ORDER, R.id.menuGroupItemAscending);

    }

    /**
     * Method allowing to obtain all video games for the current fragment and load them into the RecyclerView
     */
    @Override
    public void obtainFragmentVideoGames() {

        // Initialize the Strings used to store the necessary variables for the SQLite query
        String sortColumn = "title";
        String sortOrder = " ASC";

        // Obtain the saved sorting option and sorting order from the SharedPreferences
        int savedSortOption = getSavedSortOption();
        int savedSortOrder = getSavedSortOrder();

        // Identify which database column will be used in the "ORDER BY" clause of the SQLite query
        if (savedSortOption == R.id.menuGroupItemTitle) {
            sortColumn = "title";
        }
        else if (savedSortOption == R.id.menuGroupItemPlatform) {
            sortColumn = "platform";
        }
        else if (savedSortOption == R.id.menuGroupItemPublisher) {
            sortColumn = "publisher";
        }
        else if (savedSortOption == R.id.menuGroupItemCompletionRelease) {
            sortColumn = "release_date";
        }
        else if (savedSortOption == R.id.menuGroupItemCategoryOption) {
            sortColumn = "playtime";
        }

        // Identify if the SQLite query should be executed in ascending or descending order
        if (savedSortOrder == R.id.menuGroupItemAscending) {
            sortOrder = " ASC";
        }
        else if (savedSortOrder == R.id.menuGroupItemDescending) {
            sortOrder = " DESC";
        }

        // Cursor that will obtain all video games from the backlog
        ArrayList<VideoGame> fragmentVideoGames = mySQLiteOpenHelper.readAllVideoGamesBacklog(sortColumn, sortOrder);

        // Verification that the cursor is not empty
        if (!fragmentVideoGames.isEmpty()) {
            // Hide the message indicating that no games were found
            textViewBacklog.setVisibility(View.INVISIBLE);
            // Fill the ArrayLists with the data from the cursor
            videoGames.addAll(fragmentVideoGames);
        }

        else {
            // Hide the message indicating that no games were found
            textViewBacklog.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Method allowing to refresh all video games for the current fragment and load them into the RecyclerView
     */
    @Override
    public void refreshFragmentVideoGames() {

        // Empty the ArrayLists containing the previous video game data
        videoGames.clear();

        // Obtain the current data from the database
        obtainFragmentVideoGames();

        // Refresh the data displayed in the RecyclerView
        recyclerViewAdapter.refreshOriginalData(videoGames);
        recyclerViewAdapter.refreshCopyData();

    }

}
