/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      MainActivity.java            ***
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import java.util.Objects;

/**
 * Primary activity enabling management of application fragments and primary functionalities
 */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Main menu containing the search, sort and settings functionalities
    private Menu mainMenu;

    // FloatingActionButton allowing to add a new video game
    FloatingActionButton floatingActionButton;

    // TabLayout containing tabs corresponding to each fragment
    TabLayout tabLayout;

    // ViewPager2 allowing to swap between tabs of the TabLayout
    ViewPager2 viewPager2;

    // ViewPageAdapter permitting to determine which fragment to load into the TabLayout
    ViewPagerAdapter viewPagerAdapter;

    // SharedPreferences used to store the most recently selected tab layout fragment position
    SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "TabLayoutPreferences";
    private static final String KEY_LAYOUT_POSITION = "TabLayoutPosition";

    /**
     * Code executed at the start of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Initialization of the activity
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        // Set the colour of the navigation bar
        Window window = getWindow();
        if (window != null) {
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        }

        // Set the activity label with white text color
        String activityLabel = getString(R.string.app_name);
        SpannableString spannableLabel = new SpannableString(activityLabel);
        spannableLabel.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, android.R.color.white)), 0, activityLabel.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Apply the colored activityLabel to the ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(spannableLabel);
        }

        // Association between activity objects and layout elements
        floatingActionButton = findViewById(R.id.floatingActionButtonAddVideoGame);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);

        // Initialize SharedPreferences for the TabLayout position
        if (getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) != null) {
            sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }

        // Set the position of the TabLayout using the value stored in the SharedPreferences
        int savedTabPosition = getTabLayoutPosition();
        viewPager2.setCurrentItem(savedTabPosition);
        Objects.requireNonNull(tabLayout.getTabAt(savedTabPosition)).select();

        // Association between layout elements and activity methods
        floatingActionButton.setOnClickListener(openAddVideoGame);

        // Code invoked when tab is selecting from TabLayout
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            /**
             * Executed upon selection of a tab from the TabLayout
             * @param tab The tab that was selected
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // Set currently selected tab within the TabLayout
                viewPager2.setCurrentItem(tab.getPosition());

                // Save the position of the selected tab in the SharedPreferences
                saveTabLayoutPosition(tab.getPosition());

                // Close the SearchView upon fragment change
                closeSearchView();

            }

            /**
             * Executed upon deselection of a tab from the TabLayout
             * @param tab The tab that was unselected
             */
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            /**
             * Executed upon reselection of a tab from the TabLayout
             * @param tab The tab that was reselected.
             */
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }

        });

        // Callback invoked when page withing TabLayout is changed
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            /**
             * Executed when a new page within the TabLayout is selected
             * @param position Position index of the new selected page.
             */
            @Override
            public void onPageSelected(int position) {

                // Initialize selected page at specified position
                super.onPageSelected(position);
                Objects.requireNonNull(tabLayout.getTabAt(position)).select();

            }

        });

        // Disable OnLongClickListener events for TabLayout tabs
        if (tabLayout != null) {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                final TabLayout.Tab tab = tabLayout.getTabAt(i);
                if (tab != null) {
                    View tabView = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
                    tabView.setOnLongClickListener(v -> true);
                }
            }
        }

    }

    /**
     * Code executed when the activity is stopped
     */
    @Override
    protected void onStop() {

        // Stop the activity and close the SearchView
        super.onStop();
        closeSearchView();

    }

    /**
     * OnClick method allowing to open the activity containing the form to add a new video game
     */
    private final View.OnClickListener openAddVideoGame = new View.OnClickListener() {

        /**
         * Method executed when the button is clicked allowing to open the FormActivity
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {

            // Open a new intent containing the form for creating video games while passing the currently active fragment as an extra
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            intent.putExtra("FRAGMENT_ID", tabLayout.getSelectedTabPosition());
            startActivity(intent);

        }

    };

    /**
     * Method allowing to inflate the options menu
     * @param menu The options menu in which you place your items
     * @return True if the menu was successfully inflated, false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu and associate it with the mainMenu variable
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        mainMenu = menu;

        // Associate the search icon with the SearchView
        MenuItem menuItem = menu.findItem(R.id.mainMenuIconSearch);
        SearchView searchView = (SearchView) menuItem.getActionView();

        // Associate the SearchView with the search listener
        assert searchView != null;
        searchView.setOnQueryTextListener(this);

        // Set the hint text of the SearchView
        searchView.setQueryHint(String.format(getString(R.string.search)));

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

        // Identifier of the selected menu item
        int menuItemId = item.getItemId();

        // If the search icon is selected
        if (menuItemId == R.id.mainMenuIconSearch) {

            // Return the result of the MenuItem click
            return true;

        }

        // If the sorting icon is selected
        else if (menuItemId == R.id.mainMenuIconSort) {

            // Initialize a PopupMenu allowing to display the sorting options
            PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.mainMenuIconSort));
            popupMenu.getMenuInflater().inflate(R.menu.menu_sort, popupMenu.getMenu());

            // Get the currently visible fragment within the activity
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + viewPager2.getCurrentItem());

            // Verify that the current fragment is an instance of a VideoGameFragment
            if (currentFragment instanceof VideoGameFragment) {

                // Obtain the stored sorting option and sorting order from the SharedPreferences
                int savedSortOption = ((VideoGameFragment) currentFragment).getSavedSortOption();
                int savedSortOrder = ((VideoGameFragment) currentFragment).getSavedSortOrder();

                // Update the PopupMenu icons based on saved sorting menu options
                updateMenuItemIcon(popupMenu.getMenu(), savedSortOption, R.drawable.baseline_radio_button_checked_24);
                updateMenuItemIcon(popupMenu.getMenu(), savedSortOrder, R.drawable.baseline_radio_button_checked_24);

                // Obtain the name of the currently active fragment and update the text of the MenuItems accordingly
                String currentFragmentName = ((VideoGameFragment) currentFragment).getFragmentName();
                updateMenuItemText(currentFragmentName, popupMenu);

                // Handle OnMenuItemClickListener events for the MenuItems of the sorting menu
                popupMenu.setOnMenuItemClickListener(menuItem -> {

                    // Obtain the ID of the selected MenuItem
                    int menuItemID = menuItem.getItemId();

                    // If the user selected a sorting option, update the sorting of the current RecyclerView
                    if (menuItemID == R.id.menuGroupItemTitle || menuItemID == R.id.menuGroupItemPlatform || menuItemID == R.id.menuGroupItemPublisher ||menuItemID == R.id.menuGroupItemCompletionRelease || menuItemID == R.id.menuGroupItemCategoryOption) {

                        // Update the PopMenu icon of the previously selected sorting option
                        updateMenuItemIcon(popupMenu.getMenu(), savedSortOption, R.drawable.baseline_radio_button_unchecked_24);

                        // Save the selected sorting option in the SharedPreferences
                        ((VideoGameFragment) currentFragment).saveSortOption(menuItemID);

                        // Refresh the data displayed in the RecyclerView if a new sorting option was selected and close the SearchView
                        if (menuItemID != savedSortOption) {
                            ((VideoGameFragment) currentFragment).refreshFragmentVideoGames();
                            closeSearchView();
                            Toast.makeText(MainActivity.this, R.string.sort_options_updated, Toast.LENGTH_SHORT).show();
                        }

                        // Update the PopMenu icon of the newly selected sorting option
                        updateMenuItemIcon(popupMenu.getMenu(), menuItemID, R.drawable.baseline_radio_button_checked_24);

                        // Return the status of the MenuItem click event
                        return true;

                    }

                    // If the user selected an ordering option, update the sorting of the current RecyclerView
                    else if (menuItemID == R.id.menuGroupItemAscending || menuItemID == R.id.menuGroupItemDescending) {

                        // Update the PopMenu icon of the previously selected ordering option
                        updateMenuItemIcon(popupMenu.getMenu(), savedSortOrder, R.drawable.baseline_radio_button_unchecked_24);

                        // Save the selected ordering option in the SharedPreferences
                        ((VideoGameFragment) currentFragment).saveSortOrder(menuItemID);

                        // Refresh the data displayed in the RecyclerView if a new sorting order was selected and close the SearchView
                        if (menuItemID != savedSortOrder) {
                            ((VideoGameFragment) currentFragment).refreshFragmentVideoGames();
                            closeSearchView();
                            Toast.makeText(MainActivity.this, R.string.sort_options_updated, Toast.LENGTH_SHORT).show();
                        }

                        // Update the PopMenu icon of the newly selected ordering option
                        updateMenuItemIcon(popupMenu.getMenu(), menuItemID, R.drawable.baseline_radio_button_checked_24);

                        // Return the status of the MenuItem click event
                        return true;

                    }

                    // Return false in order to ensure that no null menu option was selected
                    return false;

                });

                // Display the sorting menu
                popupMenu.show();

                // Return the result of the MenuItem click
                return true;

            }

            // Return false if the currently visible fragment is not an instance of a VideoGameFragment
            else {
                return false;
            }

        }

        // If the settings icon is selected
        else if (menuItemId == R.id.mainMenuIconSettings) {

            // Open the activity containing the settings for the application
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);

            // Return the result of the MenuItem click
            return true;

        }

        // Return the default result of the MenuItem click if no valid menu option was found
        else {
            return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Method allowing to submit the search query
     * @param query the query text that is to be submitted
     * @return Status of the query submission
     */
    @Override
    public boolean onQueryTextSubmit(String query) {

        // Return false in order to prevent the query from being submitted
        return false;

    }

    /**
     * Method allowing to update the search query when the query text changes
     * @param newText the new content of the query text field
     * @return Status of the query text change
     */
    @Override
    public boolean onQueryTextChange(String newText) {

        // Get the currently visible fragment within the activity
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag("f" + viewPager2.getCurrentItem());

        // Verify that the currently active fragment is an instance of a SearchableFragment
        if (currentFragment instanceof VideoGameFragment) {

            // Call the search method of the SearchableFragment in order to search for video games within the RecyclerView
            ((VideoGameFragment) currentFragment).searchRecyclerView(newText);

        }

        // Return true in order to submit the query text change
        return true;

    }

    /**
     * Method allowing to close the SearchView and collapse the keyboard used when searching for video games
     */
    private void closeSearchView() {

        // Ensure that the main menu is not null
        if (mainMenu != null) {

            // Obtain the MenuItem associated with the search bar
            MenuItem menuItem = mainMenu.findItem(R.id.mainMenuIconSearch);

            // If the search bar MenuItem is found, collapse the search bar
            if (menuItem != null) {
                menuItem.collapseActionView();
            }

            // Obtain the currently visible fragment within the activity
            View view = this.getCurrentFocus();

            // If the current focus is not null, hide the on-screen keyboard
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }

    }

    /**
     * Method allowing to update the icon of a selected MenuItem in the sorting menu
     * @param menu The sorting menu containing the MenuItems
     * @param itemId The ID of the menu item to be updated
     * @param iconResId The resource ID of the icon to be set
     */
    private void updateMenuItemIcon(Menu menu, int itemId, @DrawableRes int iconResId) {

        // Obtain the MenuItem associated with the specified ID
        MenuItem item = menu.findItem(itemId);

        // If the MenuItem is found, set the icon of the MenuItem to the specified resource ID
        if (item != null) {
            item.setIcon(iconResId);
        }

    }

    /**
     * Method allowing to update the text of the sorting menu options based on the currently active fragment
     * @param currentFragmentName Name of the currently active fragment
     * @param popupMenu The sorting menu containing the MenuItems
     */
    private void updateMenuItemText(String currentFragmentName, PopupMenu popupMenu) {

        // Obtain the MenuItems associated with the third and fourth options in the sorting menu
        MenuItem menuItemCompletionRelease = popupMenu.getMenu().findItem(R.id.menuGroupItemCompletionRelease);
        MenuItem menuGroupItemCategoryOption = popupMenu.getMenu().findItem(R.id.menuGroupItemCategoryOption);

        // Ensure that both MenuItems are not null
        if (menuItemCompletionRelease != null && menuGroupItemCategoryOption != null) {

            // Dynamically set the text for the third and fourth menu options depending on which fragment is currently in use
            switch (currentFragmentName) {
                case "backlog":
                    menuGroupItemCategoryOption.setTitle(R.string.playtime);
                    break;
                case "collection":
                    menuGroupItemCategoryOption.setTitle(R.string.price);
                    break;
                case "completion":
                    menuItemCompletionRelease.setTitle(R.string.completion_date);
                    menuGroupItemCategoryOption.setTitle(R.string.playtime);
                    break;
                case "wishlist":
                    menuGroupItemCategoryOption.setTitle(R.string.price);
                    break;
                default:
                    break;
            }

        }

    }

    /**
     * Method allowing to obtain the position of the selected tab in the SharedPreferences
     * @return Position of the selected tab in the TabLayout from the SharedPreferences
     */
    private int getTabLayoutPosition() {

        // Return the value of the most recently selected tab layout position from the SharedPreferences
        return sharedPreferences.getInt(KEY_LAYOUT_POSITION, 0);

    }

    /**
     * Method allowing to save the position of the selected tab in the SharedPreferences
     * @param position Position of the selected tab in the TabLayout
     */
    private void saveTabLayoutPosition(int position) {

        // Save the position of the selected tab in the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_LAYOUT_POSITION, position);
        editor.apply();

    }

}
