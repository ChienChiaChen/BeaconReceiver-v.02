package com.example.jiacheng.blescanner;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;

/**
 * Created by jiacheng on 2015/2/23.
 */
public class scanService extends Service {
    final static String TAG = "scanService";
    //-------------------
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<String, String> DevicNameDistance;
    static int countDevicelist;
    Intent intent;
    public static final String BROADCAST_ACTION = "broadcasttest";

    //-----------
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
        DevicNameDistance = new HashMap<>();
        intent = new Intent(BROADCAST_ACTION);
        startReceiving();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopReceiving();
        Log.v(TAG, "onDestroy");
    }

    private void startReceiving() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private void stopReceiving() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String Name = device.toString();
            double tempRssi = rssi;
            String tempDistance = calculateDistance(tempRssi);
            updatDevice(Name, tempDistance);
            if (!DevicNameDistance.isEmpty()) {
                intent.putExtra("map", DevicNameDistance);
                sendBroadcast(intent);
            }
        }
    };

    protected void updatDevice(String name, String tempDistance) {
        renewList();
        if (!DevicNameDistance.isEmpty()) {
            if (!DevicNameDistance.containsKey(name))
                addDevice(name, tempDistance);//若沒有這組，則加入
            else if (DevicNameDistance.get(name) != tempDistance)//更新 entry
            {
                DevicNameDistance.remove(name);
                addDevice(name, tempDistance);
            }
        } else {
            addDevice(name, tempDistance);//空的，加入
        }
    }

    protected void renewList() {
        countDevicelist++;
        if (countDevicelist == 10) {
            DevicNameDistance = new HashMap<>();
            countDevicelist = 0;
        }
    }

    protected void addDevice(String name, String distance) {
        DevicNameDistance.put(name, distance);

    }

    public String calculateDistance(double RSSI) {

        double distance = Math.pow(10, ((-RSSI - 60) / (10 * 2)));
//        DecimalFormat a1 = new DecimalFormat("#.00");
//        String tempDistance = a1.format(distance);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits( 2 );    //小數後兩位

        return nf.format(distance);

    }
}
