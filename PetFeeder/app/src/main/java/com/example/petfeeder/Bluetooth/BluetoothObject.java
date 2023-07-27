package com.example.petfeeder.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;

public class BluetoothObject {
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket socket;
    OutputStream sendData;
    private static BluetoothObject instance;

    public BluetoothObject(BluetoothDevice bluetoothDevice, BluetoothSocket socket, OutputStream sendData, BluetoothAdapter bluetoothAdapter) {
        this.bluetoothDevice = bluetoothDevice;
        this.socket = socket;
        this.sendData = sendData;
        this.bluetoothAdapter = bluetoothAdapter;
        instance = this;
    }

    public static BluetoothObject getInstance() {
        return instance;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }
    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }
    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public OutputStream getSendData() {
        return sendData;
    }
    public void setSendData(OutputStream sendData) {
        this.sendData = sendData;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }
    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    public void sendData(String data) throws IOException {
        data += "\n";
        sendData.write(data.getBytes());
    }
}
