package com.example.betre;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    android.widget.EditText username, loginpassword;
    android.widget.Button loginbtn, forgotpassword;
    android.widget.TextView signuplink;
    com.google.firebase.auth.FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
//Intialize view
        username = findViewById(R.id.usernamelogin);
        loginpassword = findViewById(R.id.passwordlogin);
        forgotpassword = findViewById(R.id.forgot_password);
        loginbtn = findViewById(R.id.login);
        signuplink = findViewById(R.id.signuplink);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        //signup link
        signuplink.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(LoginActivity.this, SignupPage.class);
                startActivity(intent);
                finish();
            }
        });

        //forgot password
        forgotpassword.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                android.content.Intent intent = new android.content.Intent(LoginActivity.this, ForgotPassword.class);
                startActivity(intent);
                finish();
            }
        });



    }
}