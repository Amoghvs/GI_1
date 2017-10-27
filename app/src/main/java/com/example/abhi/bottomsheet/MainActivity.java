package com.example.abhi.bottomsheet;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abhi.bottomsheet.BottomSheet.BottomSheetItemObject;
import com.example.abhi.bottomsheet.BottomSheet.BottomSheetRecyclerViewAdapter;
import com.example.abhi.bottomsheet.Coupons.CouponsCardFragmentPagerAdapter;
import com.example.abhi.bottomsheet.Coupons.CouponsCardItem;
import com.example.abhi.bottomsheet.Coupons.CouponsCardPagerAdapter;
import com.example.abhi.bottomsheet.Coupons.CouponsShadowTransformer;
import com.example.abhi.bottomsheet.DatabaseTransaction.PersonDatabaseHelper;
import com.example.abhi.bottomsheet.EcoService.BatteryService;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    BottomSheetBehavior mBottomSheetBehavior;
    TextView swipe,txtname,txtseeds;
    ImageView swipebut;
    CardView maincard,quotecard,homecard,transcard;
    private GridLayoutManager lLayout;
    private Button buybut;
    ImageView qrcsn;
    private ViewPager mViewPager;
    private IntentIntegrator qrScan;

    public Toolbar toolbar;

    private PersonDatabaseHelper databaseHelper;

    private CouponsCardPagerAdapter mCardAdapter;
    private CouponsShadowTransformer mCardShadowTransformer;
    private CouponsCardFragmentPagerAdapter mFragmentCardAdapter;
    private CouponsShadowTransformer mFragmentCardShadowTransformer;
    private static final int REQUEST_SELECT_PLACE = 1000;


    public String name = "NULL";
    public int seeds = 1000;

    public String currentDateTime;



    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.
                checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE},1);
        }


        //Database
        databaseHelper = new PersonDatabaseHelper(this);

        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        currentDateTime = sdf1.format(new Date());


        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifi != null & datac != null)
                && (wifi.isConnected() | datac.isConnected())) {
            //connection is avlilable
            //Toast.makeText(getApplicationContext(),"Available",Toast.LENGTH_SHORT).show();
        }else{

            Intent i = new Intent(MainActivity.this,ConnLost_act.class);
            startActivity(i);
            finish();
            //no connection

        }


        swipe =(TextView)findViewById(R.id.swipe);
        maincard=(CardView)findViewById(R.id.maincard);
        quotecard=(CardView)findViewById(R.id.quotecard);
        homecard=(CardView)findViewById(R.id.homecard);
        transcard=(CardView)findViewById(R.id.transcard);

        buybut=(Button)findViewById(R.id.buybut) ;

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Project GI");
        setSupportActionBar(toolbar);


        txtname =(TextView)findViewById(R.id.txt_name);
        txtseeds =(TextView)findViewById(R.id.txt_seeds);


        txtname.setText(name);
        txtseeds.setText(String.valueOf(seeds));


        List<BottomSheetItemObject> rowListItem = getAllItemList();
        lLayout = new GridLayoutManager(MainActivity.this, 2);

        RecyclerView rView = (RecyclerView)findViewById(R.id.recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        BottomSheetRecyclerViewAdapter rcAdapter = new BottomSheetRecyclerViewAdapter(MainActivity.this, rowListItem);
        rView.setAdapter(rcAdapter);

        View bottomSheet = findViewById( R.id.bottom_sheet );
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setPeekHeight(210);



        //options for coupons

        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mCardAdapter = new CouponsCardPagerAdapter();
        mCardAdapter.addCardItem(new CouponsCardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CouponsCardItem(R.string.title_2, R.string.text_1));
        mCardAdapter.addCardItem(new CouponsCardItem(R.string.title_3, R.string.text_1));
        mCardAdapter.addCardItem(new CouponsCardItem(R.string.title_4, R.string.text_1));
        mCardAdapter.addCardItem(new CouponsCardItem(R.string.title_5, R.string.text_1));
        mCardAdapter.addCardItem(new CouponsCardItem(R.string.title_6, R.string.text_1));
        mFragmentCardAdapter = new CouponsCardFragmentPagerAdapter(getSupportFragmentManager(),
                dpToPixels(2, this));

        mCardShadowTransformer = new CouponsShadowTransformer(mViewPager, mCardAdapter);
        mFragmentCardShadowTransformer = new CouponsShadowTransformer(mViewPager, mFragmentCardAdapter);


        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);


        mViewPager.setCurrentItem(1);
        mCardShadowTransformer.enableScaling(true);
        mFragmentCardShadowTransformer.enableScaling(true);


        save();



        //Main act cards onClick

        maincard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //databaseHelper.insertData("asd","tht");
                startActivity(new Intent(MainActivity.this,Details.class));

            }
        });
        quotecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHelper.DatabaseDrop();
                //startActivity(new Intent(MainActivity.this,QuotesAct.class));
            }
        });
        homecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,HomeAccount.class));
            }
        });
        transcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,TransAct.class));
            }
        });


        //services

        //battery
        startService(new Intent(getBaseContext(), BatteryService.class));
        startService(new Intent(getBaseContext(),ServiceIoT.class));
        startService(new Intent(getBaseContext(),ServiceChat.class));

        qrcsn = (ImageView) findViewById(R.id.qr);
        qrScan = new IntentIntegrator(this);

        qrcsn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });



    }

    private List<BottomSheetItemObject> getAllItemList(){

        List<BottomSheetItemObject> allItems = new ArrayList<BottomSheetItemObject>();

        allItems.add(new BottomSheetItemObject("Carticipate", R.drawable.ic_car_act_imgvctr));
        allItems.add(new BottomSheetItemObject("Recyclers", R.drawable.ic_sync_black_24dp));
        allItems.add(new BottomSheetItemObject("Home", R.drawable.ic_home_black_24dp));
        allItems.add(new BottomSheetItemObject("Vehicle", R.drawable.ic_airline_seat_recline_normal_black_24dp));

        return allItems;
    }

    public void func(View view)
    {
        if(seeds-100>0) {
            seeds=seeds-100;
            Toast.makeText(MainActivity.this, "You bought card", Toast.LENGTH_SHORT).show();
            databaseHelper.insertData("Bought a card", currentDateTime);
        }
        txtseeds.setText(String.valueOf(seeds));


    }

    public void  save()  // SAVE
    {
        File file= null;


        FileOutputStream fileOutputStream = null;
        try {
            name = "Abhishek ";
            file = getFilesDir();
            fileOutputStream = openFileOutput("Code.txt", Context.MODE_PRIVATE); //MODE PRIVATE
            fileOutputStream.write(name.getBytes());
            fileOutputStream.write(String.valueOf(seeds).getBytes());
            Toast.makeText(this, "Saved \n" + "Path --" + file + "\tCode.txt", Toast.LENGTH_SHORT).show();
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void  load()
    {
        try {
            FileInputStream fileInputStream =  openFileInput("Code.txt");
            int read = -1;
            StringBuffer buffer = new StringBuffer();
            while((read =fileInputStream.read())!= -1){
                buffer.append((char)read);
            }
            Log.d("Code", buffer.toString());
            name = buffer.substring(0,buffer.indexOf(" "));
            seeds = Integer.parseInt((buffer.substring(buffer.indexOf(" ")-1)));

            Toast.makeText(this,buffer.substring(0,buffer.indexOf(" ")), Toast.LENGTH_SHORT).show();
            Toast.makeText(this,(buffer.substring(buffer.indexOf(" ")-1)), Toast.LENGTH_SHORT).show();

            txtseeds.setText(String.valueOf(seeds));
            txtname.setText(name);

        } catch (Exception e) {
            Toast.makeText(this,"cant do", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        Toast.makeText(this,"Loaded", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    //textViewName.setText(obj.getString("name"));
                    Toast.makeText(getApplicationContext(),obj.getString("name"),Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(),obj.getString("address"),Toast.LENGTH_SHORT).show();
                    //   /textViewAddress.setText(obj.getString("address"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
