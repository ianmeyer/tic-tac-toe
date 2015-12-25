package com.iantmeyer.tic_tac_toe.view;

import android.content.Context;
import android.widget.ImageView;

/**
 *  ImageView height is forced to be the same as the width
 */
public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
