package com.example.betre;

import android.content.Intent;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    private static final int DELAY = 3000;
    private static final String TAG = "SplashScreen";

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        new android.os.Handler().postDelayed(this::checkAuthentication, DELAY);

    }

    private void checkAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "No user logged in, redirecting to LoginActivity.");
            navigateToLogin();
        } else {
            Log.d(TAG, "User logged in, checking role.");
            checkUserRole(currentUser.getUid());
        }
    }

    private void checkUserRole(String uid) {
        db.collection("users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            Log.d(TAG, "User role: " + role);
                            if ("admin".equals(role)) {
                                navigateToAdmin();
                            } else {
                                navigateToMain();
                            }
                        } else {
                            Log.d(TAG, "No such document, defaulting to user role.");
                            navigateToMain();
                        }
                    } else {
                        Log.e(TAG, "Error getting role: ", task.getException());
                        navigateToLogin();
                    }
                });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToMain() {
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToAdmin() {
        Intent intent = new Intent(SplashScreen.this, AdminActivity.class);
        startActivity(intent);
        finish();
    }
}