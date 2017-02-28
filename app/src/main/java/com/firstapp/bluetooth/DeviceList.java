package com.firstapp.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class DeviceList extends AppCompatActivity {

    //widgets

    //Bluetooth
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothAdapter mBluetoothAdapter;
    OutputStream outStream;
    public static String EXTRA_ADDRESS;
    ListView ls;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       outStream = null;
       EXTRA_ADDRESS = "device_address";

        ls = (ListView) findViewById(R.id.listview);

        Button btnPaired = (Button) findViewById(R.id.button);
        btnPaired.setOnClickListener(new View.OnClickListener() {
                                         public void onClick(View v) {
                                             pairedDevicesList();
                                         }
                                     }
        );

        //if the  device has bluetooth


        if (BluetoothAdapter.getDefaultAdapter() == null) {
            //show a message that bluetooth device is not available
            Toast.makeText(getApplicationContext(), "Bluetooth device not available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            //Request to turn on bluetooth

            Intent turnBton = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnBton, 1);
        }
    }
        private AdapterView.OnItemClickListener myListCLickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
                //Get the device MAC address, the last 17 chars in view
                String info = ((TextView) v).getText().toString();
                String address = info.substring(info.length()-17);

                //Create an intent to start next activity
                Intent i = new Intent(DeviceList.this , deviceControl.class);

                //switching to next activity
                i.putExtra(EXTRA_ADDRESS, address);//this will be rerceiveed at deviceControl class
                startActivity(i);
            }
        };

    //pairedDevicesList method

    void pairedDevicesList() {

        pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList();
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());//accessing device name and address
            }
        } else {
            Toast.makeText(getApplicationContext(), "No paired devices found.", Toast.LENGTH_LONG).show();
        }

        ls.setAdapter(adapter);
        ls.setOnItemClickListener(myListCLickListener);//function called when a device is chosen
    }

    //myListClickListener method giyhub wenkjwehrkjwherhwekjrh

}

