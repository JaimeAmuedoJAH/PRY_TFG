package com.jah.pry_rfatm.Vista.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    MaterialToolbar mtbBar;
    EditText txtCorreo, txtPass, txtUsername;
    RadioGroup rgTipoUsuario;
    RadioButton rbEntrenador, rbJugador;
    Button btnRegistrate;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        UtilesUI.configurarStatusBar(this);
        mAuth = FirebaseAuth.getInstance();
        initComponents();
        setSupportActionBar(mtbBar);

        btnRegistrate.setOnClickListener(view -> registrarUsuario());
    }

    private void registrarUsuario() {
        String correo = txtCorreo.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();
        String username = txtUsername.getText().toString().trim();
        String tipoUsuario = rbJugador.isChecked() ? "Jugador" : "Entrenador";
        //Comprobamos que se rellenen todos los campos
        if (correo.isEmpty() || pass.isEmpty() || username.isEmpty() || rgTipoUsuario.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.toast_error_registro, Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user == null) return;

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    //Comprobamos si el username ya existe
                    db.collection("usuarios")
                            .whereEqualTo("username", username)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    // Ya existe, actualizar con nuevo UID y correo
                                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                    doc.getReference().update(
                                            "email", correo,
                                            "uid", user.getUid()
                                    ).addOnSuccessListener(unused -> {
                                        Toast.makeText(this, "Usuario existente actualizado", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al actualizar usuario", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    //Si no existe, crear nuevo documento
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", correo);
                                    userData.put("username", username);
                                    userData.put("tipoUsuario", tipoUsuario);
                                    userData.put("uid", user.getUid());

                                    db.collection("usuarios")
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_registro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_atras) finish();
        return false;
    }

    private void initComponents() {
        mtbBar = findViewById(R.id.mtbBar);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPass = findViewById(R.id.txtPass);
        txtUsername = findViewById(R.id.txtUsername);
        rgTipoUsuario = findViewById(R.id.rgTipoUsuario);
        rbEntrenador = findViewById(R.id.rbEntrenador);
        rbJugador = findViewById(R.id.rbJugador);
        btnRegistrate = findViewById(R.id.btnRegistrate);
    }
}
