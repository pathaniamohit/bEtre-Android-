package com.example.betre;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SplashScreen extends AppCompatActivity {

    private static final int DELAY = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        android.os.Handler handler =  new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                android.content.Intent intent =new android.content.Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);
            }
        },DELAY);
    }
}