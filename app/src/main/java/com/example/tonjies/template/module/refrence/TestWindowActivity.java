package com.example.tonjies.template.module.refrence;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.tonjies.template.R;

/**
 * @author linyaotian
 */
public class TestWindowActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_window);
        final WindowManager windowManager = getWindowManager();
        final Button btn = new Button(this);
        final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        layoutParams.height = 200;
        layoutParams.width = 200;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                windowManager.addView(btn,layoutParams);
                Looper.loop();
            }
        }).start();
    }
}
