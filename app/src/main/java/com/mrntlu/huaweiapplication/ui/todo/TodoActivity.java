package com.mrntlu.huaweiapplication.ui.todo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.mrntlu.huaweiapplication.R;

public class TodoActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO signout
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,new FragmentTodo());
        fragmentTransaction.commit();
    }
}
