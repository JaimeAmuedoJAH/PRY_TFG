package com.jah.pry_rfatm.Vista.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

public class EditarPerfilActivity extends AppCompatActivity {

    MaterialToolbar mtbBarEditarJuagdor;
    EditText txtNombreUsuario, txtEstilo;
    ImageView imgPerfil;
    Button btnCambiar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);
        UtilesUI.configurarStatusBar(this);
        mtbBarEditarJuagdor = findViewById(R.id.mtbBarEditarJugador);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtNombreUsuario.setText(getIntent().getStringExtra("nombreUsuario"));
        txtEstilo = findViewById(R.id.txtEstilo);
        txtEstilo.setText(getIntent().getStringExtra("estilo"));
        imgPerfil = findViewById(R.id.imgPerfil);
        FirebaseController.cargarImagenDesdeStorage(imgPerfil, getIntent().getStringExtra("imagenPerfil"), FirebaseController.imagenPerfilPorDefecto);
        mtbBarEditarJuagdor.setBackgroundColor(getResources().getColor(R.color.color_fondos));
        setSupportActionBar(mtbBarEditarJuagdor);
        btnCambiar = findViewById(R.id.btnCambiar);
        btnCambiar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        });
        imgPerfil.setOnClickListener(v -> {
            cambiarImagenPerfil();
        });
    }

    private void cambiarImagenPerfil() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_acta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_guardar) {
            finish();
            cambiarDatos();
        }
        return false;
    }

    private void cambiarDatos() {

    }
}