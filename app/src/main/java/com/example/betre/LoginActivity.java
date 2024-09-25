package com.example.betre;

import android.os.Bundle;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class LoginActivity extends AppCompatActivity {

    android.widget.EditText username, loginpassword;
    android.widget.Button loginbtn, forgotpassword;
    android.widget.TextView signuplink;
    com.google.firebase.auth.FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
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

        //login button
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkuser();
            }
        });

    }

    //check user and  Authication method
    public void checkuser(){
        String email = username.getText().toString().trim();
        String password = loginpassword.getText().toString().trim();
         if (android.text.TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
             username.setError("Valid email is required");
             return;
         }
        if (android.text.TextUtils.isEmpty(password)) {
            this.loginpassword.setError("Password is required");
            return;
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(LoginActivity.this, email, Toast.LENGTH_SHORT).show();
                    if(email.equals("manager@gmail.com")){
                        startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                    }else {
                        startActivity(new android.content.Intent(LoginActivity.this, MainActivity.class));
                    }
                    finish();

                }else {
                    String errorMessage = "Authentication failed.";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        errorMessage = "Weak password.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        errorMessage = "Invalid email.";
                    } catch (FirebaseAuthUserCollisionException e) {
                        errorMessage = "User with this email already exists.";
                    } catch (Exception e) {
                        errorMessage = e.getMessage();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                android.widget.Toast.makeText(LoginActivity.this, "Login failed!", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}