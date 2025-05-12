package com.jah.pry_rfatm.Vista.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

import java.util.HashMap;
import java.util.Map;

/**
 * Actividad que permite a nuevos usuarios registrarse en la aplicación mediante correo, contraseña y nombre de usuario.
 * También verifica si el nombre de usuario ya existe y actualiza información si es necesario.
 */
public class RegistroActivity extends AppCompatActivity {

    MaterialToolbar mtbBar;
    EditText txtCorreo, txtPass, txtUsername;
    Button btnRegistrate;

    /**
     * Inicializa la actividad y configura la barra de herramientas y listener de botón.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        UtilesUI.configurarStatusBar(this);
        //FirebaseController.iniciarFirebase(this);
        initComponents();
        setSupportActionBar(mtbBar);
        mtbBar.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        btnRegistrate.setOnClickListener(view -> registrarUsuario());
    }

    /**
     * Registra un nuevo usuario o actualiza uno existente en Firebase Authentication y Firestore.
     */
    private void registrarUsuario() {
        String correo = txtCorreo.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();
        String username = txtUsername.getText().toString().trim();
        //Comprobamos que se rellenen todos los campos
        if (correo.isEmpty() || pass.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, R.string.toast_error_registro, Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseController.mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = FirebaseController.mAuth.getCurrentUser();
                    if (user == null) return;
                    //Comprobamos si el username ya existe
                    FirebaseController.db.collection("usuarios")
                            .whereEqualTo("nombre", username)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    // Ya existe, actualizar con nuevo UID y correo
                                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                    doc.getReference().update("email", correo).addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Usuario existente actualizado", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    //Si no existe, crear nuevo documento
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", correo);
                                    userData.put("name", username);

                                    FirebaseController.db.collection("usuarios")
                                            .document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, "¡Registro completo!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al buscar username", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al registrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Infla el menú de opciones en la barra de herramientas.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_registro, menu);
        return true;
    }

    /**
     * Maneja los eventos del menú, incluyendo botón de retroceso.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_atras) finish();
        return false;
    }

    /**
     * Inicializa los componentes de UI.
     */
    private void initComponents() {
        mtbBar = findViewById(R.id.mtbBar);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPass = findViewById(R.id.txtPass);
        txtUsername = findViewById(R.id.txtUsername);
        btnRegistrate = findViewById(R.id.btnRegistrate);
    }
}
