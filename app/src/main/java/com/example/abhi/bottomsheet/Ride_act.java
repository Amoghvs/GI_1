package com.example.abhi.bottomsheet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cdflynn.android.library.checkview.CheckView;

public class Ride_act extends AppCompatActivity {


    static class Views {

        CheckView check;

        Views(Ride_act activity) {
            check = (CheckView) activity.findViewById(R.id.check);
        }
    }

    private Views mViews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_act);

        mViews = new Views(this);
        mViews.check.check();


    }
}