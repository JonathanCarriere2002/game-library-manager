/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      SearchableFragment.java      ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

/**
 * Interface allowing to manage the RecyclerView containing the video games of a fragment
 */
public interface VideoGameFragment {

    /**
     * Method allowing to obtain the name of the currently active fragment
     * @return Name of the currently active fragment
     */
    String getFragmentName();

    /**
     * Method allowing to filter the RecyclerView of a fragment by searching for video game titles
     * @param searchQuery The searchQuery used to search for video game titles
     */
    void searchRecyclerView(String searchQuery);

    /**
     * Method allowing to save the sorting option within a specific fragment in SharedPreferences
     * @param value Value of the sorting option to be saved
     */
    void saveSortOption(int value);

    /**
     * Method allowing to obtain the saved sorting option within a specific fragment from SharedPreferences
     * @return Value of the saved sorting option
     */
    int getSavedSortOption();

    /**
     * Method allowing to save the ordering option within a specific fragment in SharedPreferences
     * @param value Value of the ordering option to be saved
     */
    void saveSortOrder(int value);

    /**
     * Method allowing to obtain the saved ordering option within a specific fragment from SharedPreferences
     * @return Value of the saved ordering option
     */
    int getSavedSortOrder();

    /**
     * Method allowing to obtain all video games for the current fragment and load them into the RecyclerView
     */
    void obtainFragmentVideoGames();

    /**
     * Method allowing to refresh all video games for the current fragment and load them into the RecyclerView
     */
    void refreshFragmentVideoGames();

}
