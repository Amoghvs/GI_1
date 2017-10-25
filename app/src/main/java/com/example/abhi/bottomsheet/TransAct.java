package com.example.abhi.bottomsheet;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.abhi.bottomsheet.DatabaseTransaction.CustomCursorAdapter;
import com.example.abhi.bottomsheet.DatabaseTransaction.PersonDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TransAct extends AppCompatActivity {

    private CustomCursorAdapter customAdapter;
    private PersonDatabaseHelper databaseHelper;
    private ListView listView;
    public String currentDateTime;
    public String trans;
    private static final String TAG = TransAct.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);

        databaseHelper = new PersonDatabaseHelper(this);

        listView = (ListView) findViewById(R.id.list_data);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "clicked on item: " + position);
            }
        });

        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff for you <img src="http://s0.wp.com/wp-includes/images/smilies/icon_smile.gif?m=1129645325g" alt=":)" class="wp-smiley">

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                customAdapter = new CustomCursorAdapter(TransAct.this, databaseHelper.getAllData());
                listView.setAdapter(customAdapter);
            }
        });


        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateTime = sdf1.format(new Date());

        trans="Some transaction";



    }

    public void onClickEnterData(View btnAdd) {

        updateDatabase(trans,currentDateTime);

    }

    public void updateDatabase(String t,String d) {

        databaseHelper.insertData(t,d);
        customAdapter.changeCursor(databaseHelper.getAllData());
    }


}

