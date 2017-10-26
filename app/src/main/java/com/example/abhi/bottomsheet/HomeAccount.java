package com.example.abhi.bottomsheet;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeAccount extends AppCompatActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String Name = "nameKey";
    public static final String Seeds = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_account);

        SharedPreferences settings = getSharedPreferences(MyPREFERENCES,
                Context.MODE_PRIVATE);
        final String name = settings.getString(Name,"name");
        final int seeds = settings.getInt(Seeds, 0);

        Button button = (Button) findViewById(R.id.but);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"dsfds"+name+seeds,Toast.LENGTH_SHORT).show();

            }
        });

    }
}
