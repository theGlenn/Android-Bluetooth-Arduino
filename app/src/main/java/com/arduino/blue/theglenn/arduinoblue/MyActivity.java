package com.arduino.blue.theglenn.arduinoblue;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class MyActivity extends Activity {

    private int REQUEST_ENABLE_BT = 42;

    //private ArrayAdapter<String> mArrayAdapter;
    private BlueAdapter mBlueAdapter;
    private BluetoothAdapter mBluetoothAdapter;

    private ListView mListView;


    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                if (Constants.FILTER && device.getName().contains(Constants.FILTER_TAG)) {
                    feedAdapter(device);
                } else {
                    feedAdapter(device);
                }
            }
        }
    };

    // Register the BroadcastReceiver
    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);


        //mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mBlueAdapter = new BlueAdapter(this, R.layout.list_item_device);

        mListView = (ListView) this.findViewById(R.id.blueList);
        mListView.setAdapter(mBlueAdapter);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MyActivity.this, DeviceActivity.class);
                intent.putExtra(Constants.DEVICE_KEY, mBlueAdapter.getItem(i));

                MyActivity.this.startActivity(intent);
                mBluetoothAdapter.cancelDiscovery();
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
            finish();

        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter to show in a ListView
                    mBlueAdapter.add(device);
                    //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    mBlueAdapter.notifyDataSetChanged();
                }
            }
            registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
            mBluetoothAdapter.startDiscovery();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(Constants.BLE_TAG, "onActivityResult" + resultCode);
    }

    void feedAdapter(BluetoothDevice device) {

        mBlueAdapter.add(device);
        mBlueAdapter.notifyDataSetChanged();

        //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
        Log.d(Constants.BLE_TAG, device.getName() + "\n" + device.getAddress());
    }
}
