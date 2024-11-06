package com.example.betre;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    private static final int DELAY = 3000;
    private static final String TAG = "SplashScreen";

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        FirebaseApp.initializeApp(this);

        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference();

        new android.os.Handler().postDelayed(this::checkAuthentication, DELAY);
    }

    private void checkAuthentication() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Log.d(TAG, "No user logged in, redirecting to LoginActivity.");
            navigateToLogin();
        } else {
            Log.d(TAG, "User logged in, checking role.");
            setIsOnlineStatus(currentUser.getUid(), true);  // Set isOnline to true
            checkUserRole(currentUser.getUid());
        }
    }

    private void setIsOnlineStatus(String uid, boolean isOnline) {
        dbRef.child("users").child(uid).child("isOnline").setValue(isOnline)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "isOnline status updated successfully."))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update isOnline status: ", e));
    }

    private void checkUserRole(String uid) {
        dbRef.child("users").child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.getValue(String.class);
                    Log.d(TAG, "User role: " + role);

                    if ("admin".equals(role)) {
                        navigateToAdmin();
                    } else if ("moderator".equals(role)) {
                        navigateToAdmin();
                    }else{
                        navigateToMain();
                    }
                } else {
                    Log.d(TAG, "No role found, defaulting to user role.");
                    navigateToMain();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e(TAG, "Error getting role: ", error.toException());
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
    private void navigateToModerator() {
        Intent intent = new Intent(SplashScreen.this, ModeratorActivity.class);
        startActivity(intent);
        finish();
    }
}
