package com.example.petfeeder.Pages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.petfeeder.Adapters.Data;
import com.example.petfeeder.Application.PetFeeder;
import com.example.petfeeder.Bluetooth.BluetoothObject;
import com.example.petfeeder.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Diet extends AppCompatActivity {

    final Calendar myCalendar = Calendar.getInstance();
    private EditText datePicker, timePicker;

    EditText date_pick;
    EditText time_pick;
    RadioButton btn_lvl_1, btn_lvl_2, btn_lvl_3, btn_lvl_4, btn_lvl_5, btn_lvl_6;
    Button upload;
    String address = null;
    private BluetoothAdapter bluetoothAdapter;
    BluetoothSocket btSocket = null;

    DatabaseReference feedRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet);

        if (BluetoothObject.getInstance()!=null) {
            address = BluetoothObject.getInstance().getBluetoothDevice().getAddress();
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

        timePicker.setOnClickListener(v -> selectTime());

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData(view);
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
            Toast.makeText(Diet.this, "Please enter valid date!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time.isEmpty()) {
            Toast.makeText(Diet.this, "Please enter valid time!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(Diet.this, "You got disconnected to the Pet Feeder device. Please reconnect first!", Toast.LENGTH_SHORT).show();
            finish();
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
            BluetoothObject.getInstance().sendData(text);
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(Diet.this, "Error parsing date or time", Toast.LENGTH_SHORT).show();
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Check if a specific RadioButton is selected
        String text = null;
        if (btn_lvl_1.isChecked())      text = "Level 1";
        else if (btn_lvl_2.isChecked()) text = "Level 2";
        else if (btn_lvl_3.isChecked()) text = "Level 3";
        else if (btn_lvl_4.isChecked()) text = "Level 4";
        else if (btn_lvl_5.isChecked()) text = "Level 5";
        else if (btn_lvl_6.isChecked()) text = "Level 6";

        if (text != null){
            try {
                BluetoothObject.getInstance().sendData(text);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Toast.makeText(Diet.this, "Data Uploaded successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DisplayPetDetails.class);
            intent.putExtra("RECORD_ID", PetFeeder.getInstance().getPetModel().getID());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(Diet.this, "Please select Level", Toast.LENGTH_SHORT).show();
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
}