package com.example.gorda.snapchatclone.loginRegistration;

import android.content.Intent;
import android.media.MediaPlayer;
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

public class LoginActivity extends AppCompatActivity {

    private Button mLogin;
    private EditText mEmail, mPassword;
    private TextInputLayout emailLayout, passwordLayout;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

        mLogin = findViewById(R.id.buttonLogin);

        mEmail = findViewById(R.id.editTextEmailLogin);
        mPassword = findViewById(R.id.editTextPasswordLogin);

        emailLayout = findViewById(R.id.editTextEmailLoginLay);
        passwordLayout = findViewById(R.id.editTextPasswordLoginLay);

        mEmail.addTextChangedListener(new MyTextWatcher(mEmail));
        mPassword.addTextChangedListener(new MyTextWatcher(mPassword));

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail()) {
                    return;
                }
                
                if (!validatePassword()) {
                    return;
                }

                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Sign in ERROR", Toast.LENGTH_LONG).show();
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
                case R.id.editTextEmailLogin:
                    validateEmail();
                    break;
                case R.id.editTextPasswordLogin:
                    validatePassword();
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
