package com.firstapp.bluetooth;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static com.firstapp.bluetooth.R.id.seekBar;

public class deviceControl extends AppCompatActivity {

    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public String address;//receive the address of the connected device
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket btSocket;
    Button btnOn;
    Button btnOff;
    Button btnDis;
    TextView lumn;
    SeekBar brightness;
    int progress_var;
    Intent newint;
    private ProgressDialog progress;
    private boolean isBtConnected;
    private BluetoothDevice dispositivo;
    public void Disconnect(View view)
    {
        if (btSocket!=null)//if socket is connected
        {
            try
            {
                btSocket.close();//close connection
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        finish();//return to the device_list layout
    }

    public void turnOff(View view)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("0".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
    }

    public void turnOn(View view)
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("9".toString().getBytes());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
    //view of device control

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_control);

        newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);//receive the address of the connected device

        mBluetoothAdapter = null;
        btSocket = null;
        isBtConnected = false;


        btnOn = (Button)findViewById(R.id.button2);
        btnOff = (Button)findViewById(R.id.button3);
        btnDis = (Button)findViewById(R.id.button4);
        lumn = (TextView) findViewById(R.id.lumn);

        progress_var = 5;
        new ConnectBT().execute();
        brightness = (SeekBar) findViewById(seekBar);
        brightness.setMax(9);
        brightness.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){
            }
            @Override
        public void onProgressChanged(SeekBar seekBar, int progress_var, boolean fromUser)
        {
            if (fromUser == true)
            {
                lumn.setText(String.valueOf(new Integer(progress_var)));
                try
                {
                    btSocket.getOutputStream().write(String.valueOf(progress_var).getBytes());
                }
                catch (IOException e)
                {

                }
            }
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar){
        }
    });
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void> //UI thread
    {
        private boolean ConnectSuccess = true; //if the device is present then it would be connected
        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(deviceControl.this, "Connecting...", "Please Wait !!!");//showing a progress message
        }
        @Override
        public Void doInBackground(Void... devices)//while the progress dialog is shown connection is established in background
        {
            try
            {
                if(btSocket == null || !isBtConnected)
                {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//get the mobile blueooth device
                    dispositivo = mBluetoothAdapter.getRemoteDevice(address);//connects to the device address and checks if its the desired the location of the required device
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//Initiate connection
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                ConnectSuccess = false;//if connection failed
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result)//it checks whether the background process executed fine or not
        {
            super.onPostExecute(result);
            if (!ConnectSuccess)
            {
                msg("Connection Failed... Try Again!!!");
                finish();
            }
            else
            {
                msg("Connected");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
