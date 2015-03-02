package com.arduino.blue.theglenn.arduinoblue;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.UUID;


public class DeviceActivity extends Activity {


    Switch itemSwitch;
    InputStream tmpIn = null;
    OutputStream tmpOut = null;
    ConnectThread c;
    TextView valTextViewt;

    public static final String VAL = "val";
    private Handler uiUpdate;

    StringBuffer read = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        itemSwitch = (Switch) this.findViewById(R.id.item_switch);
        valTextViewt = (TextView) this.findViewById(android.R.id.text1);

        uiUpdate = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();
                String val = bundle.getString(VAL);
                valTextViewt.setText(val);
            }
        };

        BluetoothDevice bluetoothDevice = getIntent().getExtras().getParcelable("btdevice");
        c = new ConnectThread(bluetoothDevice);
        c.start();

        itemSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int value = isChecked ? 1 : 0;

                write(value);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.cancel();

    }

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;


        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            //mBluetoothAdapter.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            manageConnectedSocket(mmSocket);
        }

        /**
         * Will cancel an in-progress connection, and close the socket
         */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String msg) {

        byte[] bytes = msg.getBytes(Charset.forName("US-ASCII"));
        Log.d("write", msg);
        try {
            tmpOut.write(bytes);
            //tmpOut.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /* Call this from the main activity to send data to the remote device */
    public void write(int val) {

        //byte[] bytes = val.getBytes(Charset.forName("US-ASCII"));
        Log.d("writing", "..." + val);
        try {
            tmpOut.write(val);
            //tmpOut.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        c.cancel();
        finish();
    }

    public void manageConnectedSocket(BluetoothSocket socket) {
        // Get the input and output streams, using temp objects because
        // member streams are final
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();

            Log.d("Manage", "-");
            new Thread() {
                @Override
                public void run() {
                    BufferedInputStream bufferedIN = new BufferedInputStream(tmpIn);
                    // Keep listening to the InputStream until an exception occurs
                    Log.d("Manage", "Run");
                    while (true) {
                        try {

                            int bytesAvailable = bufferedIN.available();
                            byte[] buffer = new byte[bytesAvailable];  // buffer store for the stream

                            if (bytesAvailable > 0) {

                                int bytes = bufferedIN.read(buffer);
                                // Read from the InputStream
                                String o = new String(buffer, "US-ASCII");

                                read.append(o);
                                if (o.endsWith("\n")) {

                                    Message msg = new Message();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(VAL, read.toString());
                                    msg.setData(bundle);

                                    uiUpdate.sendMessage(msg);

                                    Log.d("return", read.toString());
                                    read.setLength(0);
                                }
                            }
                        } catch (IOException e) {
                            Log.d("read  error", e.getMessage());
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void read(int bytesAvailable, Byte[] packetBytes) {
        int readBufferPosition = 0;
        for (int i = 0; i < bytesAvailable; i++) {
            byte b = packetBytes[i];

            byte[] encodedBytes = new byte[readBufferPosition];
            //System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
            final String data;
            try {
                data = new String(encodedBytes, "US-ASCII");
                readBufferPosition = 0;
                Log.d("ardui", data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}