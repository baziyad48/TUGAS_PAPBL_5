package com.example.firebase_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth _mAuth;
    FirebaseUser _currentUser;
    Button _login, _register;
    EditText _email, _password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _login = findViewById(R.id.btn_login);
        _register = findViewById(R.id.btn_register);

        _email = findViewById(R.id.txt_email);
        _password = findViewById(R.id.txt_password);

        _mAuth = FirebaseAuth.getInstance();
        _currentUser = _mAuth.getCurrentUser();
        if(_currentUser != null) {
            if(_currentUser.isEmailVerified()) {
                Intent map = new Intent(MainActivity.this, MainActivity.class);
                startActivity(map);
            }
        }

        _login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mAuth.signInWithEmailAndPassword(_email.getText().toString(), _password.getText().toString())
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = _mAuth.getCurrentUser();
                                if(user != null) {
                                    if(user.isEmailVerified()) {
                                        Intent map = new Intent(MainActivity.this, MainActivity.class);
                                        startActivity(map);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Email not verified!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Authentication failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                );
            }
        });

        _register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _mAuth.createUserWithEmailAndPassword(_email.getText().toString(), _password.getText().toString())
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                FirebaseUser user = _mAuth.getCurrentUser();
                                if(user != null) {
                                    UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                                            .setDisplayName("Tester").build();

                                    user.updateProfile(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                //notifikasi berhasil update
                                            }
                                        }
                                    });
                                }
                                if(user.isEmailVerified()) {
                                    Intent map = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(map);
                                } else {
                                    final String _newEmail = user.getEmail();
                                    user.sendEmailVerification().addOnCompleteListener(MainActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Email verification sent to " + _newEmail, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(MainActivity.this, "Failed to sent verification email!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Authentication failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                );
            }
        });
    }
}
