package com.example.petfeeder;

public class Data {

    String date;
    String time;

    public Data(String date, String time) {
        this.date = date;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public static class PedometerData {
        int pedometer;
        String date;

        public PedometerData(int pedometer, String date) {
            this.pedometer = pedometer;
            this.date = date;
        }

        public int getPedometer() {
            return pedometer;
        }
        public String getDate() {
            return date;
        }
    }



}