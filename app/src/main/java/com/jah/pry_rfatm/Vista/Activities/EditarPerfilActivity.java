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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.storage.StorageReference;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditarPerfilActivity extends AppCompatActivity {
    /**
     * Componentes para editar jugador
     */
    MaterialToolbar mtbBarEditarJugador;
    EditText txtNombreUsuario, txtEstilo;
    ImageView imgPerfil;
    Button btnCambiar;
    /**
     * Componentes para editar entrenador
     */
    MaterialToolbar mtbEditarEntrenador;
    ImageView imgFotoPerfilEnt, imgFotoEscudoEq;
    EditText txtNombreEntrenador;
    EditText txtJugador1, txtJugador2, txtJugador3, txtJugador4, txtJugador5, txtJugador6;
    Button btnCambiarPass;
    /**
     * Constantes para la selección de imagen
     */
    private static final int REQUEST_CODE_GALLERY = 1;
    private Uri imagenPerfilUri;
    private Uri imagenEscudoUri;
    private int tipoImagenSeleccionada = 0;
    String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Verificar tipo de usuario y establecer el layout correspondiente
        tipoUsuario = getIntent().getStringExtra("tipoUsuario");
        if ("jugador".equals(tipoUsuario)) {
            setContentView(R.layout.activity_editar_perfil_jugador);
            initComponentsJugador();
            setSupportActionBar(mtbBarEditarJugador);

        } else if ("entrenador".equals(tipoUsuario)) {
            setContentView(R.layout.activity_editar_perfil_entrenador);
            initComponentsEntrenador();
            setSupportActionBar(mtbEditarEntrenador);
        }
        UtilesUI.configurarStatusBar(this);
    }
    /**
     * Maneja el resultado de la actividad de selección de imagen.
     *@param requestCode El código de solicitud entero que se pasó originalmente a
     *                   startActivityForResult(), lo que te permite identificar de dónde
     *                   proviene este resultado.
     *@param resultCode El código de resultado entero devuelto por la actividad hija
     *                  a través de su método setResult().
     *@param data Un Intent que puede devolver datos de resultado al llamador
     *            (se pueden adjuntar varios datos a los "extras" del Intent).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            if ("jugador".equals(getIntent().getStringExtra("tipoUsuario"))) {
                imagenPerfilUri = imageUri;
                imgPerfil.setImageURI(imageUri);
            } else if ("entrenador".equals(getIntent().getStringExtra("tipoUsuario"))) {
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
    /**
     * Crea el menú de opciones en la barra de acción.
     * @param menu para colocar los elementos del menú.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_acta, menu);
        return true;
    }
    /**
     * Maneja la selección de elementos del menú.
     * @param item el elemento seleccionado.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_guardar) {
            if ("jugador".equals(tipoUsuario)) {
                guardarDatosJugador();
            } else if ("entrenador".equals(tipoUsuario)) {
                guardarDatosEntrenador();
            }
        }
        return false;
    }
    /**
     * Guarda los datos del entrenador en Firestore.
     */
    private void guardarDatosEntrenador() {
        String uid = Objects.requireNonNull(FirebaseController.mAuth.getCurrentUser()).getUid();
        String nombre = txtNombreEntrenador.getText().toString().trim();

        Map<String, Object> datosEntrenador = new HashMap<>();
        datosEntrenador.put("nombre", nombre);

        if (imagenPerfilUri != null) {
            subirImagen("usuarios/" + uid + "/fotoPerfil.jpg", imagenPerfilUri, uri -> {
                datosEntrenador.put("fotoPerfil", uri.toString());
                actualizarFirestore(uid, datosEntrenador);
            });
        } else {
            actualizarFirestore(uid, datosEntrenador);
        }

        // Actualizar equipo con jugadores titulares y suplentes
        actualizarEquipoEntrenador();
    }
    /**
     * Actualiza el equipo del entrenador en Firestore.
     */
    private void actualizarEquipoEntrenador() {
        String uid = Objects.requireNonNull(FirebaseController.mAuth.getCurrentUser()).getUid();

        FirebaseController.db.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("equipoId")) {
                        String equipoId = documentSnapshot.getString("equipoId").split("/")[2];

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

                        obtenerIdsDeJugadores(nombresTitulares, titularesIds -> {
                            obtenerIdsDeJugadores(nombresSuplentes, suplentesIds -> {
                                Map<String, Object> datosEquipo = new HashMap<>();
                                datosEquipo.put("jugadores", titularesIds);
                                datosEquipo.put("suplentes", suplentesIds);

                                assert equipoId != null;
                                FirebaseController.db.collection("equipos").document(equipoId)
                                        .update(datosEquipo)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(this, R.string.equipo_actualizado_correctamente, Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, R.string.error_al_actualizar_equipo, Toast.LENGTH_SHORT).show();
                                        });

                                if (imagenEscudoUri != null) {
                                    subirImagen("equipos/" + equipoId + "/escudo.jpg", imagenEscudoUri, uri -> {
                                        FirebaseController.db.collection("equipos").document(equipoId)
                                                .update("escudo", uri.toString());
                                    });
                                }
                            });
                        });

                    } else {
                        Toast.makeText(this, R.string.no_se_encontr_el_equipo_del_entrenador, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, R.string.error_al_obtener_el_equipo, Toast.LENGTH_SHORT).show();
                });
    }
    /**
     * Obtiene los IDs de los jugadores a partir de sus nombres.
     * @param nombres
     * @param callback
     */
    private void obtenerIdsDeJugadores(List<String> nombres, OnSuccessListener<List<String>> callback) {
        List<String> idsEncontrados = new ArrayList<>();
        List<String> nombresNoVacios = new ArrayList<>();
        for (String nombre : nombres) {
            if (!nombre.isEmpty()) nombresNoVacios.add(nombre);
        }

        if (nombresNoVacios.isEmpty()) {
            callback.onSuccess(idsEncontrados);
            return;
        }

        FirebaseController.db.collection("usuarios")
                .whereIn("nombre", nombresNoVacios)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (String nombre : nombresNoVacios) {
                        for (var doc : queryDocumentSnapshots.getDocuments()) {
                            if (nombre.equals(doc.getString("nombre"))) {
                                idsEncontrados.add(doc.getId());
                                break;
                            }
                        }
                    }
                    callback.onSuccess(idsEncontrados);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al buscar jugadores", Toast.LENGTH_SHORT).show();
                    callback.onSuccess(new ArrayList<>()); // vacío por error
                });
    }
    /**
     * Guarda los datos del jugador en Firestore.
     */
    private void guardarDatosJugador() {
        String uid = FirebaseController.mAuth.getCurrentUser().getUid();
        String nuevoNombre = txtNombreUsuario.getText().toString().trim();
        String nuevoEstilo = txtEstilo.getText().toString().trim();

        Map<String, Object> nuevosDatos = new HashMap<>();
        nuevosDatos.put("nombre", nuevoNombre);
        nuevosDatos.put("estilo", nuevoEstilo);

        if (imagenPerfilUri != null) {
            StorageReference ref = FirebaseController.storage.getReference()
                    .child("usuarios/" + uid + "/fotoPerfil.jpg");

            ref.putFile(imagenPerfilUri).addOnSuccessListener(task -> {
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    nuevosDatos.put("fotoPerfil", uri.toString());
                    actualizarFirestore(uid, nuevosDatos);
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
            });
        } else {
            actualizarFirestore(uid, nuevosDatos);
        }
    }
    /**
     * Actualiza los datos del usuario en Firestore.
     * @param uid
     * @param nuevosDatos
     */
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
    /**
     * Subir una imagen a Firebase Storage.
     * @param ruta
     * @param imagenUri
     * @param onSuccess
     */
    private void subirImagen(String ruta, Uri imagenUri, OnSuccessListener<Uri> onSuccess) {
        StorageReference ref = FirebaseController.storage.getReference().child(ruta);
        ref.putFile(imagenUri).addOnSuccessListener(task -> {
            ref.getDownloadUrl().addOnSuccessListener(onSuccess);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
        });
    }
    /**
     * Abre la galería para seleccionar una imagen.
     * @param tipo
     */
    private void seleccionarImagen(int tipo) {
        tipoImagenSeleccionada = tipo;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }
    /**
     * Inicializa los componentes para editar entrenador.
     */
    private void initComponentsEntrenador() {
        mtbEditarEntrenador = findViewById(R.id.mtbEditarEntrenador);
        imgFotoPerfilEnt = findViewById(R.id.imgFotoPerfilEnt);
        FirebaseController.cargarImagenDesdeStorage(imgFotoPerfilEnt, getIntent().getStringExtra("imagenPerfil"), FirebaseController.imagenPerfilPorDefecto);
        imgFotoEscudoEq = findViewById(R.id.imgFotoEscudoEq);
        FirebaseController.cargarImagenDesdeStorage(imgFotoEscudoEq, getIntent().getStringExtra("imagenEscudo"), FirebaseController.imagenPorDefecto);
        mtbEditarEntrenador.setBackgroundColor(getResources().getColor(R.color.color_fondos));
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

        btnCambiarPass.setOnClickListener(v -> {
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        });

        imgFotoPerfilEnt.setOnClickListener(v -> {
            seleccionarImagen(1);
        });

        imgFotoEscudoEq.setOnClickListener(v -> {
            seleccionarImagen(2);
        });
    }
    /**
     * Inicializa los componentes para editar jugador.
     */
    private void initComponentsJugador() {
        mtbBarEditarJugador = findViewById(R.id.mtbBarEditarJugador);
        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtNombreUsuario.setText(getIntent().getStringExtra("nombreUsuario"));
        txtEstilo = findViewById(R.id.txtEstilo);
        txtEstilo.setText(getIntent().getStringExtra("estilo"));
        imgPerfil = findViewById(R.id.imgPerfil);
        FirebaseController.cargarImagenDesdeStorage(imgPerfil, getIntent().getStringExtra("imagenPerfil"), FirebaseController.imagenPerfilPorDefecto);
        mtbBarEditarJugador.setBackgroundColor(getResources().getColor(R.color.color_fondos));
        btnCambiar = findViewById(R.id.btnCambiar);

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
}