package com.mrntlu.huaweiapplication.ui.auth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseUser;
import com.mrntlu.huaweiapplication.R;
import com.mrntlu.huaweiapplication.viewmodels.AuthViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseAuthActivity extends AppCompatActivity {

    @BindView(R.id.emailText)
    AppCompatEditText emailText;

    @BindView(R.id.passwordText)
    AppCompatEditText passwordText;

    protected AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView();
        ButterKnife.bind(this);
        authViewModel= ViewModelProviders.of(this).get(AuthViewModel.class);
        setClickListeners();
        setAuthListener();

        authViewModel.setAuthStateListener();
    }

    protected void setAuthListener(){
        authViewModel.getFirebaseUser().observe(this, this::authStateController);
    }

    protected boolean isEligibleLogin(AppCompatEditText editText){
        String value=String.valueOf(editText.getText());
        if (value==null) return false;
        else if (value.trim().isEmpty()) return false;
        else if (value.length()<6) return false;
        else return true;
    }

    protected abstract void setView();

    protected abstract void setClickListeners();

    protected abstract void authStateController(FirebaseUser firebaseUser);


}
