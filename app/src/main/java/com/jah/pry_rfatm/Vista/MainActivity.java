package com.jah.pry_rfatm.Vista;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.jah.pry_rfatm.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        
    }
}