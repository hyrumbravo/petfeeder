package com.example.petfeeder.Pages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petfeeder.Adapters.ScanBTListViewAdapter;
import com.example.petfeeder.Bluetooth.BluetoothObject;
import com.example.petfeeder.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class ScanBluetooth extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver receiver;
    BluetoothSocket socket;

    private ArrayList<DeviceObject> deviceObjects;
    private ArrayList<String> macAddresses;
    private ScanBTListViewAdapter adapter;

    private Boolean bluetoothProcessDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        RecyclerView btDeviceList = findViewById(R.id.recyclerview);
        deviceObjects = new ArrayList<>();
        macAddresses = new ArrayList<>();

        findViewById(R.id.scanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothProcessDone) discoverDevices();
            }
        });

        adapter = new ScanBTListViewAdapter(this, deviceObjects);
        btDeviceList.setAdapter(adapter);
        btDeviceList.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnItemClickListener(new ScanBTListViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DeviceObject deviceObject) {
                if (deviceObject.getName()!=null && deviceObject.getName().equals("HC-05")) {
                    if (!deviceObject.getPaired()) {
                        deviceObject.getDevice().createBond();
                    } else {
                        connectToBTDevice(deviceObject.getDevice());
                    }
                } else {
                    Toast.makeText(ScanBluetooth.this, deviceObject.getName() + " is not a valid Pet Feeder device.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //CHECK IF BLUETOOTH IS SUPPORTED.
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth not supported on this device", Toast.LENGTH_SHORT).show();
        } else {
            //  REQUEST FOR PERMISSION
            requestPermissions();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateRecyclerView() {
        adapter.notifyDataSetChanged();
    }

    private void connectToBTDevice(BluetoothDevice device) {
        try {
            UUID sppUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            socket = device.createRfcommSocketToServiceRecord(sppUuid);
            socket.connect();
            try {
                if (BluetoothObject.getInstance()!=null){
                    BluetoothObject instance = BluetoothObject.getInstance();
                    instance.setBluetoothDevice(device);
                    instance.setSendData(socket.getOutputStream());
                    instance.setSocket(socket);
                    instance.setBluetoothAdapter(bluetoothAdapter);
                } else {
                    new BluetoothObject(device, socket, socket.getOutputStream(), bluetoothAdapter);
                }
                startActivity(new Intent(this, Diet.class));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void requestPermissions() {
        String[] permissions = {
                android.Manifest.permission.BLUETOOTH,
                android.Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
        };
        ArrayList<String> permissionToAsk = new ArrayList<>();

        //CHECK FOR PERMISSIONS THAT NEEDS ASKING
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionToAsk.add(permission);
            }
        }

        if (permissionToAsk.isEmpty()) {
            //THERE IS NO NEED TO ASK FOR PERMISSIONS. PROCEED.
            enableBluetooth();
        } else {
            //THERE IS A NEED TO ASK FOR PERMISSIONS.
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allPermissionsGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            if (allPermissionsGranted) {
                // PERMISSIONS ARE GRANTED. PROCEED.
                enableBluetooth();
            } else {
                Toast.makeText(this, "Permissions are required for Bluetooth functionality", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, prompt the user to enable it
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            bluetoothProcessDone = true;
        } else {
            bluetoothProcessDone = true;
        }
    }

    private void discoverDevices() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // BLUETOOTH DEVICE IS FOUND.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    String mac_address = device.getAddress();
                    if (!macAddresses.contains(name)) {
                        updateRecyclerView();
                        DeviceObject deviceObject = new DeviceObject(device, name, mac_address, device.getBondState() == BluetoothDevice.BOND_BONDED);
                        deviceObjects.add(deviceObject);
                        macAddresses.add(name);
                    }

                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    // PAIRING STATE CHANGED FOR A BLUETOOTH DEVICE
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    String name = device.getName();
                    if (bondState == BluetoothDevice.BOND_BONDED){
                        Toast.makeText(ScanBluetooth.this, "Paired successfully.", Toast.LENGTH_SHORT).show();
                        for (DeviceObject deviceObject : deviceObjects){
                            if (deviceObject.getName().equals(name)){
                                deviceObject.setPaired(true);
                                connectToBTDevice(deviceObject.getDevice());
                                break;
                            }
                        }
                    } else if (bondState == BluetoothDevice.BOND_NONE){
                        Toast.makeText(ScanBluetooth.this, "Pairing failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        // REGISTER THE RECEIVER
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, filter);

        // START DISCOVERY
        if (bluetoothAdapter.isDiscovering()) { bluetoothAdapter.cancelDiscovery(); }
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                // Handle the exception
                e.printStackTrace();
            }
        }
    }

    public static class DeviceObject{
        String name;
        String MacAddress;
        Boolean isPaired;
        BluetoothDevice device;

        public DeviceObject(BluetoothDevice bluetoothDevice, String name, String MacAddress, Boolean isPaired) {
            this.name = name;
            this.MacAddress = MacAddress;
            this.isPaired = isPaired;
            this.device = bluetoothDevice;
        }

        public BluetoothDevice getDevice() {
            return device;
        }

        public String getName() {
            return name;
        }

        public String getMacAddress() {
            return MacAddress;
        }

        public Boolean getPaired() {
            return isPaired;
        }

        public void setPaired(Boolean paired) {
            isPaired = paired;
        }
    }
}