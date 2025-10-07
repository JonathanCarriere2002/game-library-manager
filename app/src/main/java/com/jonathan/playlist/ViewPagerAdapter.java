/***********************************************
 *** Author:    Jonathan Carrière            ***
 *** Date:      2024-08-20                   ***
 *** File:      ViewPageAdapter.java         ***
 *** Project:   PlayList                     ***
 ***********************************************/

package com.jonathan.playlist;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Adapter enabling to manage the content of the fragment within the ViewPager
 */
public class ViewPagerAdapter extends FragmentStateAdapter {

    /**
     * Initialization of the ViewPagerAdapter
     */
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {

        // Initialization of the FragmentActivity
        super(fragmentActivity);

    }

    /**
     * Creation of a specified fragment via a position
     * @param position Position of the selected fragment
     * @return Fragment that will be created and displayed
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {

        // Switch allowing to determine which fragment must be created
        switch (position) {
            case 1: return new CollectionFragment();
            case 2: return new CompletionFragment();
            case 3: return new WishlistFragment();
            default: return new BacklogFragment();
        }

    }

    /**
     * Get the total quantity of fragments for the ViewPager
     * @return Amount of fragments within the ViewPager
     */
    @Override
    public int getItemCount() {

        // Return the total amount of fragments for the ViewPager
        return 4;

    }

}
