package com.iantmeyer.tic_tac_toe.util;

import android.content.res.Resources;

public class ViewUtil {

    /**
     * Calculate density independent pixel from true pixels
     * @param px
     * @return
     */
    public static int dp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Calculate pixels from density independent pixels
     * @param dp
     * @return
     */
    public static int px(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
