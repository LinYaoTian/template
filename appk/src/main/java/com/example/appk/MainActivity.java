package com.example.appk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.appk.module.refrence.AppKMain;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            Class<?> c = Class.forName("com.example.appk.module.refrence.AppKMain");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "e = "+e.getMessage());
        }
        Log.d(TAG, "onCreate: ");
    }
}



