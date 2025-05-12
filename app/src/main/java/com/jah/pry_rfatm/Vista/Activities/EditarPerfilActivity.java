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
import com.google.firebase.storage.StorageReference;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {

    MaterialToolbar mtbBarEditarJuagdor;
    EditText txtNombreUsuario, txtEstilo;
    ImageView imgPerfil;
    Button btnCambiar;
    private static final int REQUEST_CODE_GALLERY = 1;
    private Uri imagenSeleccionadaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_jugador);
        UtilesUI.configurarStatusBar(this);
        initComponents();
        setSupportActionBar(mtbBarEditarJuagdor);
        btnCambiar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        });
        imgPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            // Referencia en Storage donde se guardará la imagen
            String userId = FirebaseController.mAuth.getCurrentUser().getUid();
            StorageReference ref = FirebaseController.storage.getReference()
                    .child("usuarios/" + userId + "/fotoPerfil.jpg");

            // Subir la imagen
            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Obtener la URL de descarga
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();

                            // Actualizar campo fotoPerfil en Firestore
                            FirebaseController.db.collection("usuarios")
                                    .document(userId)
                                    .update("fotoPerfil", downloadUrl)
                                    .addOnSuccessListener(aVoid -> {
                                        // Opcional: feedback al usuario
                                        Toast.makeText(this, R.string.toast_foto_actualizada_correctamente, Toast.LENGTH_SHORT).show();
                                        // Cargar la imagen actualizada
                                        FirebaseController.cargarImagenDesdeStorage(imgPerfil, downloadUrl, FirebaseController.imagenPerfilPorDefecto);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, R.string.toast_error_al_actualizar_foto, Toast.LENGTH_SHORT).show();
                                    });
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, R.string.toast_error_al_subir_imagen, Toast.LENGTH_SHORT).show();
                    });
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
        if(item.getItemId() == R.id.item_guardar) {
            cambiarDatos();
        }
        return false;
    }

    private void cambiarDatos() {
        String nuevoNombre = txtNombreUsuario.getText().toString().trim();
        String nuevoEstilo = txtEstilo.getText().toString().trim();
        String uid = FirebaseController.mAuth.getCurrentUser().getUid();

        Map<String, Object> nuevosDatos = new HashMap<>();
        nuevosDatos.put("nombre", nuevoNombre);
        nuevosDatos.put("estilo", nuevoEstilo);

        // Si hay una nueva imagen seleccionada, subirla primero
        if (imagenSeleccionadaUri != null) {
            String rutaImagen = "imagenesPerfil/" + uid + ".jpg";
            FirebaseController.storage.getReference().child(rutaImagen)
                    .putFile(imagenSeleccionadaUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Obtener URL de descarga
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            nuevosDatos.put("fotoPerfil", uri.toString());

                            // Actualizar Firestore con todos los datos
                            actualizarFirestore(uid, nuevosDatos);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, R.string.toast_error_al_subir_imagen, Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Sin imagen nueva, solo actualizar los datos
            actualizarFirestore(uid, nuevosDatos);
        }
    }

    private void actualizarFirestore(String uid, Map<String, Object> nuevosDatos) {
        FirebaseController.db.collection("usuarios").document(uid)
                .update(nuevosDatos)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, R.string.toast_perfil_actualizado_correctamente, Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, R.string.toast_error_al_actualizar_perfil, Toast.LENGTH_SHORT).show();
                });
    }


    private void initComponents() {
        mtbBarEditarJuagdor = findViewById(R.id.mtbBarEditarJugador);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtNombreUsuario.setText(getIntent().getStringExtra("nombreUsuario"));
        txtEstilo = findViewById(R.id.txtEstilo);
        txtEstilo.setText(getIntent().getStringExtra("estilo"));
        imgPerfil = findViewById(R.id.imgPerfil);
        FirebaseController.cargarImagenDesdeStorage(imgPerfil, getIntent().getStringExtra("imagenPerfil"), FirebaseController.imagenPerfilPorDefecto);
        mtbBarEditarJuagdor.setBackgroundColor(getResources().getColor(R.color.color_fondos));
        btnCambiar = findViewById(R.id.btnCambiar);
    }
}