package com.jah.pry_rfatm.Vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.FirebaseApp;
import com.jah.pry_rfatm.R;

public class LogInActivity extends AppCompatActivity {

    EditText txtCorreo, txtPass;
    Button btnIni, btnIniGoogle, btnRegistrar;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        FirebaseApp.initializeApp(this);
        initComponents();
        btnRegistrar.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), RegistroActivity.class);
            startActivity(intent);
        });
        btnIni.setOnClickListener(v -> {

        });
        btnIniGoogle.setOnClickListener(v -> {
            iniciarSesionGoogle();
        });
    }

    private void iniciarSesionGoogle() {
    }

    private void initComponents() {
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPass = findViewById(R.id.txtPass);
        btnIni = findViewById(R.id.btnIni);
        btnIniGoogle = findViewById(R.id.btnIniGoogle);
        btnRegistrar = findViewById(R.id.btnRegistrar);
    }
}