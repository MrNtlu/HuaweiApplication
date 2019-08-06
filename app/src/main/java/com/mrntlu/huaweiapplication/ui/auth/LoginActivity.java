package com.mrntlu.huaweiapplication.ui.auth;

import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.mrntlu.huaweiapplication.R;
import com.mrntlu.huaweiapplication.ui.todo.TodoActivity;

public class LoginActivity extends BaseAuthActivity {

    @Override
    protected void setView() {
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void setClickListeners(){
        findViewById(R.id.loginButton).setOnClickListener(view -> {
            if (isEligibleLogin(emailText) && isEligibleLogin(passwordText)) {
                authViewModel.loginUser(String.valueOf(emailText.getText()), String.valueOf(passwordText.getText()));
            }else{
                Toast.makeText(this, "Error, please check credentials.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.registerButton).setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,RegisterActivity.class)));
    }

    @Override
    protected void authStateController(FirebaseUser firebaseUser) {
        if (firebaseUser!=null){
            Toast.makeText(this, "Logged in as: "+firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, TodoActivity.class));
        }else{
            Toast.makeText(this, "Error, please try again!", Toast.LENGTH_SHORT).show();
        }
    }


}

