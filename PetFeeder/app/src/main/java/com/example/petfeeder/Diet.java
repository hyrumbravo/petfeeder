package com.example.petfeeder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Diet extends AppCompatActivity {

    final Calendar myCalendar = Calendar.getInstance();
    private EditText datePicker, timePicker;

    EditText date_pick;
    EditText time_pick;
    RadioButton btn_lvl_1, btn_lvl_2, btn_lvl_3, btn_lvl_4, btn_lvl_5, btn_lvl_6;
    Button upload;
    private BluetoothAdapter myBluetooth = BluetoothAdapter.getDefaultAdapter();
    String address = null;
    private ProgressDialog custom_progress;
    private BluetoothAdapter bluetoothAdapter;
    private OutputStream outputStream;
    private BluetoothDevice device;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    public static final int PERMISSION_REQUEST_CODE = 1;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    DatabaseReference feedRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Bluetooth.EXTRA_ADDRESS)) {
            address = intent.getStringExtra(Bluetooth.EXTRA_ADDRESS);
            if (address == null || address.isEmpty()) {
                // Handle the case when the Bluetooth address is empty
                Toast.makeText(Diet.this, "Invalid Bluetooth address", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        } else {
            // Handle the case when the Bluetooth address is not passed
            Toast.makeText(Diet.this, "Bluetooth address not provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //receive the address of the bluetooth device



        date_pick = findViewById(R.id.date_pick);
        time_pick = findViewById(R.id.time_pick);
        btn_lvl_1 = findViewById(R.id.lvl_1);
        btn_lvl_2 = findViewById(R.id.lvl_2);
        btn_lvl_3 = findViewById(R.id.lvl_3);
        btn_lvl_4 = findViewById(R.id.lvl_4);
        btn_lvl_5 = findViewById(R.id.lvl_5);
        btn_lvl_6 = findViewById(R.id.lvl_6);
        upload = findViewById(R.id.upload);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        datePicker = findViewById(R.id.date_pick);
        timePicker = findViewById(R.id.time_pick);
        selectDate();
        new ConnectBT().execute(); //Call the class to connect

        timePicker.setOnClickListener(v -> selectTime());

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData(view);//ito binago ko 5:20
            }

        });

    }

    public void uploadData(View view) {
        //firebase upload
        String date = date_pick.getText().toString();
        String time = time_pick.getText().toString();


        Data data = new Data(date, time);
        feedRef = FirebaseDatabase.getInstance().getReference().child("Data");
        feedRef.push().setValue(data);
        Toast.makeText(Diet.this, "Data uploaded", Toast.LENGTH_SHORT).show();

        if (date.isEmpty()) {
            Toast.makeText(Diet.this, "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time.isEmpty()) {
            Toast.makeText(Diet.this, "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(Diet.this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            return;

            //Bluetooth Upload..
        }
        SimpleDateFormat myformat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat mytime = new SimpleDateFormat("HH:mm:ss", Locale.US);
        try {
            // Parse the date and time strings to Date objects
            Date parsedDate = myformat.parse(date);
            Date parsedTime = mytime.parse(time);

            // Format the Date objects to the desired formats
            String formattedDate = myformat.format(parsedDate);
            String formattedTime = mytime.format(parsedTime);

            // Prepare the data to be sent to the Arduino Bluetooth module
            String text = formattedDate + " " + formattedTime;

            // Send the data via Bluetooth
            sendTextToBluetooth(text);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(Diet.this, "Error parsing date or time", Toast.LENGTH_SHORT).show();
            return;
        }
        // Check if a specific RadioButton is selected
        String text = null;
        //ito binago ko 5:20
        if (btn_lvl_1.isChecked()) {
            // Send text for RadioButton 1
            text = "Level 1";
        } else if (btn_lvl_2.isChecked()) {
            // Send text for RadioButton 2
            text = "Level 2";
        } else if (btn_lvl_3.isChecked()) {
            // Send text for RadioButton 3
            text = "Level 3";
        } else if (btn_lvl_4.isChecked()) {
            // Send text for RadioButton 4
            text = "Level 4";
        } else if (btn_lvl_5.isChecked()) {
            // Send text for RadioButton 5
            text = "Level 5";
        } else if (btn_lvl_6.isChecked()) {
            // Send text for RadioButton 6
            text = "Level 6";
        }
        if (text != null){
            sendTextToBluetooth(text);
            Toast.makeText(Diet.this, "Data Uploaded successfully", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(Diet.this, "Please select Level", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendTextToBluetooth(String text) {
        try {
            OutputStream outputStream = btSocket.getOutputStream();
            outputStream.write(text.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(Diet.this, "Error sending data via Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectDate() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                datePicker.setText(updateDate());
            }
        };
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(Diet.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private String updateDate() {
        String myFormat = "yyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        return dateFormat.format(myCalendar.getTime());
    }

    private void selectTime() {

        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minut = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(Diet.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hour, int minut) {
                currentTime.set(Calendar.HOUR_OF_DAY, hour);
                currentTime.set(Calendar.MINUTE, minut);

                String myFormat = "HH:mm:ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                timePicker.setText(dateFormat.format(currentTime.getTime()));
            }
        }, hour, minut, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
    private class ConnectBT extends AsyncTask<Void, Void, Void> implements com.example.petfeeder.ConnectBT {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            custom_progress = ProgressDialog.show(Diet.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);
                    if (ActivityCompat.checkSelfPermission(Diet.this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Diet.this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_REQUEST_CODE);

                        return null;
                    }
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                    outputStream = btSocket.getOutputStream();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
                e.printStackTrace();//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);
            custom_progress.dismiss();

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
        }
    }
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }
}