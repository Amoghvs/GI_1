package com.example.abhi.bottomsheet;

/**
 * Created by anuj on 27/10/17.
 */

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.abhi.bottomsheet.EcoService.DialogAct;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by amogh on 26/10/17.
 */

public class ServiceIoT extends Service {




    String host = "tcp://m12.cloudmqtt.com:11871";
    // String clientId = "ExampleAndroidClient";
    String topic = "topic/iot";

    String username = "zyekiwpb";
    String password = "z58Alb-SFL-_";

    MqttAndroidClient client;
    IMqttToken token = null;
    MqttConnectOptions options;
    int seeds;
    String named;

    int val = 0;


    @Override
    public void onCreate() {
        super.onCreate();


        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this.getApplicationContext(), host, clientId);

        options = new MqttConnectOptions();
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        Calendar c = Calendar.getInstance();
        String sData = c.get(Calendar.HOUR_OF_DAY)+":"+c.get(Calendar.MINUTE) + ":"+c.get(Calendar.SECOND);

       if(sData.equals("0:0:0")) {
           load();
           seeds += 10;
           save();
       }





        try {
            token = client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        try {
            token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(getApplicationContext(),"Connection successful",Toast.LENGTH_SHORT).show();
                    subscribtion();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getApplicationContext(),"Connection failed",Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String msg = new String(message.getPayload());
                if(!msg.contains("value")) {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    if (msg.contains("car")) {
                        startNotification();
                    }
                    if (msg.contains("car")) {
                        startNotification();
                    }
                    if (msg.contains("w")){
                        val++;
                        if(val==20)
                        {
                            startActivity(new Intent(ServiceIoT.this, DialogAct.class));

                        }
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.

        Toast.makeText(this, "ServiceChat Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "ServiceChat Destroyed", Toast.LENGTH_LONG).show();
    }

    private void subscribtion(){
        try {
            client.subscribe(topic,0);
        } catch (MqttSecurityException e) {
            e.printStackTrace();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void  save()  // SAVE
    {
        File file= null;


        FileOutputStream fileOutputStream = null;
        try {
            file = getFilesDir();
            fileOutputStream = openFileOutput("Code.txt", Context.MODE_PRIVATE); //MODE PRIVATE
            fileOutputStream.write((named+" ").getBytes());
            fileOutputStream.write(String.valueOf(seeds).getBytes());
            //Toast.makeText(this, "Saved \n" + "Path --" + file + "\tCode.txt", Toast.LENGTH_SHORT).show();
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

            String array[] = buffer.toString().split(" ");
            named = array[0];
            seeds = Integer.parseInt(array[1]);


        } catch (Exception e) {
            Toast.makeText(this,"cant do", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


        Toast.makeText(this,"Loaded", Toast.LENGTH_SHORT).show();
    }

    private void startNotification(){

        // Set Notification Title
        String strtitle = getString(R.string.notificationtitle);
        // Set Notification Text
        String strtext = getString(R.string.notificationtext);

        // Open NotificationView Class on Notification Click
        Intent intent = new Intent(this, DriveOver.class);
        // Send data to NotificationView Class
        intent.putExtra("title", "connected");
        intent.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent2 = new Intent(this, CarAct.class);
        // Send data to NotificationView Class
        intent2.putExtra("title", strtitle);
        intent2.putExtra("text", strtext);
        // Open NotificationView.java Activity
        PendingIntent pIntent2 = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);



        //Create Notification using NotificationCompat.Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.drawable.logo)
                // Set Ticker Message
                .setTicker(getString(R.string.notificationticker))
                // Set Title
                .setContentTitle("Driving Mode Started")
                // Set Text
                .setContentText(getString(R.string.notificationtext))
                .addAction(R.drawable.cast_ic_notification_0, "Stop", pIntent)

                // Set PendingIntent into Notification
                .setContentIntent(pIntent2)
                // Dismiss Notification
                .setAutoCancel(true);


        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(0, builder.build());


    }

}
