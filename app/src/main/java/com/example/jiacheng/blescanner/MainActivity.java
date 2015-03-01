package com.example.jiacheng.blescanner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    private Intent intent;
    private Button scanButton, notifyButton;
//    private TextView showDevice;
    final static String TAG = "MainActivity";
//    private Handler mHandler;
//    private Thread thread;
    private String newData;
    private ListView listview;
    final String ID_Address = "Address", ID_Distance = "Distance";
    boolean notifyFlag, notify;//false為開，true為關

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanButton = (Button) findViewById(R.id.scan);
        notifyButton = (Button) findViewById(R.id.notifyButton);
        listview = (ListView) findViewById(R.id.listViewv);
//        showDevice = (TextView) findViewById(R.id.showDevice);
        //--
        notify = true;
        //--
        notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().toString().equals("notify_on")) {
                    b.setText("notify_off");
                    notifyFlag = true;
                } else {
                    b.setText("notify_on");
                    notifyFlag = false;
                }
            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button b = (Button) v;
                if (b.getText().toString().equals("scan")) {
                    startService(intent);
                    b.setText("stop");
                } else {
                    stopService(intent);
                    b.setText("scan");
                }
            }
        });
        intent = new Intent(this, scanService.class);
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(scanService.BROADCAST_ACTION));
//        mHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case 1:
////                        showDevice.setText(newData);
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        };
//        thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Message msg = new Message();
//                        msg.what = 1;
//                        mHandler.sendMessage(msg);
//                        Thread.sleep(2500);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//        });
//        thread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        notify = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        notify = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        notify = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        notify = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notify = true;
        unregisterReceiver(broadcastReceiver);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
            if (!notifyFlag)
                if (!notify)
                    createNotification(getBaseContext());

        }
    };

    private void updateUI(Intent intent) {
        HashMap<String, String> hashMap = (HashMap<String, String>) intent.getSerializableExtra("map");
        String name = "";
        for (Object key : hashMap.keySet()) {
            name = name + "\n" + key + "\t" + hashMap.get(key) + "\n";
        }
        newData = name;
        ArrayList<HashMap<String, String>> myListData = new ArrayList<HashMap<String, String>>();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            //your code block
            HashMap<String, String> tempHashmap = new HashMap<>();
            tempHashmap.put(ID_Address, entry.getKey());
            tempHashmap.put(ID_Distance, entry.getValue());
            myListData.add(tempHashmap);
        }
        this.setList(myListData);
    }

    public void setList(ArrayList<HashMap<String, String>> myListData) {
        listview.setAdapter(new SimpleAdapter(
                        this,
                        myListData,
                        android.R.layout.simple_list_item_2,
                        new String[]{ID_Address, ID_Distance},
                        new int[]{android.R.id.text1, android.R.id.text2})
        );
    }

    private void createNotification(Context context) {
        NotificationManager notificationManager
                = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long[] vibratepattern = {100, 400, 500, 400};

        builder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(newData)
//                .setContentText("BeaconText")
//                .setContentInfo("按下他吧...")
                .setTicker("票")
                .setLights(0xFFFFFFFF, 1000, 1000)
                .setVibrate(vibratepattern)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false);

        Notification notification = builder.getNotification();
        notificationManager.notify(R.mipmap.ic_launcher, notification);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
