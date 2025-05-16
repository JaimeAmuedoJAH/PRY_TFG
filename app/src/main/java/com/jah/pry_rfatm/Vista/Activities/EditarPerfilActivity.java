package com.jah.pry_rfatm.Vista.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Logica.EditarPerfilLogic;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EditarPerfilActivity extends AppCompatActivity {
    MaterialToolbar toolbar;
    EditText txtNombreUsuario, txtEstilo, txtNombreEntrenador;
    EditText txtJugador1, txtJugador2, txtJugador3, txtJugador4, txtJugador5, txtJugador6;
    ImageView imgPerfil, imgFotoPerfilEnt, imgFotoEscudoEq;
    Button btnCambiar, btnCambiarPass;

    private static final int REQUEST_CODE_GALLERY = 1;
    private Uri imagenPerfilUri;
    private Uri imagenEscudoUri;
    private int tipoImagenSeleccionada = 0;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tipoUsuario = getIntent().getStringExtra("tipoUsuario");
        if ("jugador".equals(tipoUsuario)) {
            setContentView(R.layout.activity_editar_perfil_jugador);
            initJugador();
        } else if ("entrenador".equals(tipoUsuario)) {
            setContentView(R.layout.activity_editar_perfil_entrenador);
            initEntrenador();
        }
        UtilesUI.configurarStatusBar(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            if ("jugador".equals(tipoUsuario)) {
                imagenPerfilUri = imageUri;
                imgPerfil.setImageURI(imageUri);
            } else {
                if (tipoImagenSeleccionada == 1) {
                    imagenPerfilUri = imageUri;
                    imgFotoPerfilEnt.setImageURI(imageUri);
                } else if (tipoImagenSeleccionada == 2) {
                    imagenEscudoUri = imageUri;
                    imgFotoEscudoEq.setImageURI(imageUri);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_acta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_guardar) {
            String uid = Objects.requireNonNull(FirebaseController.mAuth.getCurrentUser()).getUid();
            if ("jugador".equals(tipoUsuario)) {
                EditarPerfilLogic.guardarDatosJugador(
                        uid,
                        txtNombreUsuario.getText().toString().trim(),
                        txtEstilo.getText().toString().trim(),
                        imagenPerfilUri,
                        (success, message, urlImagen) -> {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            if (success && urlImagen != null) {
                                FirebaseController.cargarImagenDesdeStorage(imgPerfil, urlImagen, FirebaseController.imagenPerfilPorDefecto);
                            }
                            if (success) finish();
                        }
                );
            } else {
                List<String> nombresTitulares = Arrays.asList(
                        txtJugador1.getText().toString().trim(),
                        txtJugador2.getText().toString().trim(),
                        txtJugador3.getText().toString().trim()
                );
                List<String> nombresSuplentes = Arrays.asList(
                        txtJugador4.getText().toString().trim(),
                        txtJugador5.getText().toString().trim(),
                        txtJugador6.getText().toString().trim()
                );

                EditarPerfilLogic.guardarDatosEntrenador(
                        uid,
                        txtNombreEntrenador.getText().toString().trim(),
                        nombresTitulares,
                        nombresSuplentes,
                        imagenPerfilUri,
                        (success, message, urlImagenPerfil) -> {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            if (success) finish();
                        }
                );
            }
        }
        return false;
    }

    private void initJugador() {
        toolbar = findViewById(R.id.mtbBarEditarJugador);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtNombreUsuario.setText(getIntent().getStringExtra("nombreUsuario"));
        txtEstilo = findViewById(R.id.txtEstilo);
        txtEstilo.setText(getIntent().getStringExtra("estilo"));
        imgPerfil = findViewById(R.id.imgPerfil);
        FirebaseController.cargarImagenDesdeStorage(imgPerfil, getIntent().getStringExtra("imagenPerfil"), FirebaseController.imagenPerfilPorDefecto);

        btnCambiar = findViewById(R.id.btnCambiar);
        btnCambiar.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));
        imgPerfil.setOnClickListener(v -> seleccionarImagen(1));
    }

    private void initEntrenador() {
        toolbar = findViewById(R.id.mtbEditarEntrenador);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        imgFotoPerfilEnt = findViewById(R.id.imgFotoPerfilEnt);
        imgFotoEscudoEq = findViewById(R.id.imgFotoEscudoEq);
        FirebaseController.cargarImagenDesdeStorage(imgFotoPerfilEnt, getIntent().getStringExtra("imagenPerfil"), FirebaseController.imagenPerfilPorDefecto);
        FirebaseController.cargarImagenDesdeStorage(imgFotoEscudoEq, getIntent().getStringExtra("imagenEscudo"), FirebaseController.imagenPorDefecto);

        txtNombreEntrenador = findViewById(R.id.txtNombreEntrenador);
        txtNombreEntrenador.setText(getIntent().getStringExtra("nombreUsuario"));

        txtJugador1 = findViewById(R.id.txtJugador1);
        txtJugador1.setText(getIntent().getStringExtra("jugador1"));
        txtJugador2 = findViewById(R.id.txtJugador2);
        txtJugador2.setText(getIntent().getStringExtra("jugador2"));
        txtJugador3 = findViewById(R.id.txtJugador3);
        txtJugador3.setText(getIntent().getStringExtra("jugador3"));
        txtJugador4 = findViewById(R.id.txtJugador4);
        txtJugador4.setText(getIntent().getStringExtra("jugador4"));
        txtJugador5 = findViewById(R.id.txtJugador5);
        txtJugador5.setText(getIntent().getStringExtra("jugador5"));
        txtJugador6 = findViewById(R.id.txtJugador6);
        txtJugador6.setText(getIntent().getStringExtra("jugador6"));

        btnCambiarPass = findViewById(R.id.btnCambiarPass);
        btnCambiarPass.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));

        imgFotoPerfilEnt.setOnClickListener(v -> seleccionarImagen(1));
        imgFotoEscudoEq.setOnClickListener(v -> seleccionarImagen(2));
    }

    private void seleccionarImagen(int tipo) {
        tipoImagenSeleccionada = tipo;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }
}