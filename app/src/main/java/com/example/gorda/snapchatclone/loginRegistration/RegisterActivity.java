package com.example.gorda.snapchatclone.loginRegistration;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gorda.snapchatclone.MainActivity;
import com.example.gorda.snapchatclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private Button mRegister;
    private EditText mEmail, mPassword, mUsername;
    private TextInputLayout emailLayout, passwordLayout, usernameLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        };

        mAuth = FirebaseAuth.getInstance();

        mRegister = findViewById(R.id.buttonRegister);

        mEmail = findViewById(R.id.editTextEmailRegister);
        mPassword = findViewById(R.id.editTextPasswordRegister);
        mUsername = findViewById(R.id.editTextUsernameRegister);

        passwordLayout = findViewById(R.id.editTextPasswordRegisterLay);
        emailLayout = findViewById(R.id.editTextEmailRegisterLay);
        usernameLayout = findViewById(R.id.editTextUsernameRegisterLay);

        mPassword.addTextChangedListener(new MyTextWatcher(mPassword));
        mUsername.addTextChangedListener(new MyTextWatcher(mUsername));
        mEmail.addTextChangedListener(new MyTextWatcher(mEmail));

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }

                if (!validateUsername()) {
                    return;
                }

                final String username = mUsername.getText().toString();
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplication(), "Sign in ERROR", Toast.LENGTH_LONG).show();
                        } else {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDB = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

                            Map userInfo = new HashMap();
                            userInfo.put("email", email);
                            userInfo.put("username", username);
                            userInfo.put("profileImageUrl", "default");

                            currentUserDB.updateChildren(userInfo);
                        }
                    }
                });
            }
        });
    }

    private boolean validatePassword() {
        if (mPassword.getText().toString().trim().isEmpty()) {
            passwordLayout.setError("Enter your password");
            requestFocus(mPassword);
            return false;
        }

        passwordLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateEmail() {
        if (mEmail.getText().toString().trim().isEmpty()) {
            emailLayout.setError("Enter your email");
            requestFocus(mEmail);
            return false;
        }

        emailLayout.setErrorEnabled(false);
        return true;
    }

    private boolean validateUsername() {
        if (mUsername.getText().toString().trim().isEmpty()) {
            usernameLayout.setError("Enter your username");
            requestFocus(mUsername);
            return false;
        }

        usernameLayout.setErrorEnabled(false);
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.editTextEmailRegister:
                    validateEmail();
                    break;
                case R.id.editTextPasswordRegister:
                    validatePassword();
                    break;
                case R.id.editTextUsernameRegister:
                    validateUsername();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
