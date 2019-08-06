package com.mrntlu.huaweiapplication.ui.auth;

import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.mrntlu.huaweiapplication.R;

public class RegisterActivity extends BaseAuthActivity {

    @Override
    protected void setClickListeners() {
        findViewById(R.id.backButton).setOnClickListener(view -> onBackPressed());

        findViewById(R.id.registerButton).setOnClickListener(view -> {
            if (isEligibleLogin(emailText) && isEligibleLogin(passwordText)){
                authViewModel.registerUser(String.valueOf(emailText.getText()), String.valueOf(passwordText.getText()));
            }else{
                Toast.makeText(this, "Error, please check credentials.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void setView() {
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void authStateController(FirebaseUser firebaseUser) {
        if (firebaseUser!=null){
            Toast.makeText(this, "Registered!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        }else{
            Toast.makeText(this, "Error, please try again!", Toast.LENGTH_SHORT).show();
        }
    }
}
