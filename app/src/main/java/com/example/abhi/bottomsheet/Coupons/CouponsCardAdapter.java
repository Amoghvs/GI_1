package com.example.abhi.bottomsheet.Coupons;


import android.support.v7.widget.CardView;
import android.view.View;

public interface CouponsCardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();


}
