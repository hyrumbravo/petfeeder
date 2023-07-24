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
import java.util.Set;
import java.util.UUID;

public class non_diet extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    final Calendar myCalendar = Calendar.getInstance();
    private EditText datePicker, timePicker;
    EditText date_pick;
    private BluetoothDevice device;
    EditText time_pick;
    RadioButton btn_lvl_1, btn_lvl_2, btn_lvl_3, btn_lvl_4, btn_lvl_5, btn_lvl_6;
    Button upload;
    BluetoothSocket btSocket = null;
    DatabaseReference feedRef;
    private BluetoothAdapter bluetoothAdapter;
    private String deviceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_diet);

        checkBluetoothAvailability();
        connectToBluetoothDevice();
        date_pick = findViewById(R.id.date_pick);
        time_pick = findViewById(R.id.time_pick);
        btn_lvl_1 = findViewById(R.id.lvl_1);
        btn_lvl_2 = findViewById(R.id.lvl_2);
        btn_lvl_3 = findViewById(R.id.lvl_3);
        btn_lvl_4 = findViewById(R.id.lvl_4);
        btn_lvl_5 = findViewById(R.id.lvl_5);
        btn_lvl_6 = findViewById(R.id.lvl_6);
        upload = findViewById(R.id.upload);
        datePicker = findViewById(R.id.date_pick);
        timePicker = findViewById(R.id.time_pick);
        selectDate();
        timePicker.setOnClickListener(v -> selectTime());

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData(view);//ito binago ko 5:20
            }

        });
    }


    // Discover and connect to the target Bluetooth device

    private void checkBluetoothAvailability() {
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported on this device
            // Handle the situation accordingly
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, request the user to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        }
    }

    private void connectToBluetoothDevice() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
        } else {
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : bondedDevices) {
                // Check if the device matches your desired criteria (e.g., device name or MAC address)
                if (device.getName().equals(deviceName)) {
                    // Connect to the device
                    ConnectThread connectThread = new ConnectThread(device);
                    connectThread.start();
                    break;
                }
            }
        }
    }

    public void uploadData(View view) {
        //firebase upload
        String date = date_pick.getText().toString();
        String time = time_pick.getText().toString();


        Data data = new Data(date, time);
        feedRef = FirebaseDatabase.getInstance().getReference().child("Data");
        feedRef.push().setValue(data);
        Toast.makeText(non_diet.this, "Data uploaded", Toast.LENGTH_SHORT).show();

        if (date.isEmpty()) {
            Toast.makeText(non_diet.this, "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time.isEmpty()) {
            Toast.makeText(non_diet.this, "Please enter text", Toast.LENGTH_SHORT).show();
            return;
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(non_diet.this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(non_diet.this, "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            return;
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
            Toast.makeText(non_diet.this, "Error parsing date or time", Toast.LENGTH_SHORT).show();
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
        if (text != null) {
            sendTextToBluetooth(text);
            Toast.makeText(non_diet.this, "Data Uploaded successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(non_diet.this, "Please select Level", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendTextToBluetooth(String text) {
        if (btSocket != null) {
            try {
                OutputStream outputStream = btSocket.getOutputStream();
                outputStream.write(text.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(non_diet.this, "Error sending data via Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(non_diet.this, "Bluetooth socket is not available", Toast.LENGTH_SHORT).show();
        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmpSocket = null;

            // Get a BluetoothSocket for a connection with the given BluetoothDevice
            try {
                UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // Standard SerialPortService ID
                if (ActivityCompat.checkSelfPermission(non_diet.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(non_diet.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                    return;
                }
                tmpSocket = device.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmSocket = tmpSocket;
        }

        public void run() {
            // Cancel discovery to speed up the connection
            if (ActivityCompat.checkSelfPermission(non_diet.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(non_diet.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_ENABLE_BT);
                return;
            }
            bluetoothAdapter.cancelDiscovery();

                try {
                    // Connect to the remote device through the socket
                    mmSocket.connect();
                    // Connection successful, perform any required operations here
                } catch (IOException connectException) {
                    // Unable to connect, handle the exception
                    connectException.printStackTrace();
                    try {
                        mmSocket.close();
                    } catch (IOException closeException) {
                        closeException.printStackTrace();
                    }
                }
            }

            public void cancel() {
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                new DatePickerDialog(non_diet.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
        int minute = currentTime.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(non_diet.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                currentTime.set(Calendar.MINUTE, minute);

                String myFormat = "HH:mm:ss";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                time_pick.setText(dateFormat.format(currentTime.getTime()));
            }
        }, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
}