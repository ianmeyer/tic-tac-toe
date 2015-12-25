package com.iantmeyer.tic_tac_toe.util;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

public class FragmentHelper {

    public static void replace(AppCompatActivity activity, Fragment replacementFragment,
                               int placeHolderId) {
        if (activity.findViewById(placeHolderId) != null) {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(placeHolderId, replacementFragment)
                    .commit();
        }
    }
}
