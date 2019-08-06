package com.mrntlu.huaweiapplication.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends ViewModel {

    private FirebaseAuth mAuth;
    private MutableLiveData<FirebaseUser> firebaseUserData;

    public AuthViewModel() {
        mAuth=FirebaseAuth.getInstance();
        firebaseUserData=new MutableLiveData<>();
    }

    public LiveData<FirebaseUser> getFirebaseUser(){
        return firebaseUserData;
    }

    public void registerUser(String email,String password){
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        firebaseUserData.setValue(mAuth.getCurrentUser());
                    }else{
                        firebaseUserData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (e.getMessage()!=null){
                        e.printStackTrace();
                    }
                });
    }

    public void loginUser(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(task->{
                    if (task.isSuccessful()){
                        firebaseUserData.setValue(mAuth.getCurrentUser());
                    }else{
                        firebaseUserData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    if (e.getMessage()!=null){
                        e.printStackTrace();
                    }
                });
    }

    public void setAuthStateListener(){
        new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!=null){
                    firebaseUserData.setValue(firebaseAuth.getCurrentUser());
                }
            }
        };
    }
}
