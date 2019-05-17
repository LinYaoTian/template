package com.example.tonjies.template;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.lib_annotations.annotation.RandomInt;
import com.example.lib_annotations.annotation.RandomUtil;
import com.example.tonjies.template.annotation.MyDagger;
import com.example.tonjies.template.net.bean.Student;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @MyDagger(name = "LinYT",size = 20)
    Student student;
    @RandomInt(maxValue = 10,minValue = 1)
    int mRandoq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RandomUtil.inject(this);
        Log.d(TAG, "onCreate: "+mRandoq);

    }

}
