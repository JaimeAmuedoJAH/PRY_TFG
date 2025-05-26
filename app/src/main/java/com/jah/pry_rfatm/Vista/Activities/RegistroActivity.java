package com.jah.pry_rfatm.Vista.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

import java.util.HashMap;
import java.util.Map;

/**
 * Actividad de registro de usuarios.
 */
public class RegistroActivity extends AppCompatActivity {

    MaterialToolbar mtbBar;
    EditText txtCorreo, txtPass, txtUsername;
    TextInputLayout inputPass;
    Button btnRegistrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        UtilesUI.configurarStatusBar(this);
        initComponents();
        setSupportActionBar(mtbBar);
        mtbBar.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        btnRegistrate.setOnClickListener(view -> registrarUsuario());

        // Limpiar errores si se edita la contraseña
        txtPass.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputPass.setError(null);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Registra un nuevo usuario en Firebase Authentication y Firestore.
     */
    private void registrarUsuario() {
        String correo = txtCorreo.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();
        String username = txtUsername.getText().toString().trim();

        if (correo.isEmpty() || pass.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, R.string.toast_error_registro, Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseController.mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = FirebaseController.mAuth.getCurrentUser();
                    if (user == null) return;

                    // Enviar correo de verificación
                    user.sendEmailVerification()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, getString(R.string.correo_de_verificaci_n_enviado_a) + user.getEmail(), Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, getString(R.string.error_al_enviar_verificaci_n) + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                    FirebaseController.db.collection("usuarios")
                            .whereEqualTo("nombre", username)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                    doc.getReference().update("email", correo).addOnSuccessListener(unused -> {
                                        Toast.makeText(this, getString(R.string.usuario_existente_actualizado), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }).addOnFailureListener(e -> {
                                        Toast.makeText(this, getString(R.string.error_al_actualizar_usuario), Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", correo);
                                    userData.put("name", username);

                                    FirebaseController.db.collection("usuarios")
                                            .document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(this, getString(R.string.registro_completo), Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, getString(R.string.error_al_guardar_datos), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, getString(R.string.error_al_buscar_username), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    String errorMsg = e.getMessage();
                    if (errorMsg != null && errorMsg.contains("The given password is invalid")) {
                        inputPass.setError(getString(R.string.la_contrase_a_debe_tener_al_menos_6_caracteres_una_may_scula_un_n_mero_y_un_car_cter_especial));
                    } else {
                        inputPass.setError(null);
                        Toast.makeText(this, getString(R.string.error_al_registrar) + errorMsg, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Crea el menú de opciones en la barra de acción.
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_registro, menu);
        return true;
    }

    /**
     * Maneja la selección de elementos del menú.
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_atras) finish();
        return false;
    }

    /**
     * Inicializa los componentes de la interfaz de usuario.
     */
    private void initComponents() {
        mtbBar = findViewById(R.id.mtbBar);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPass = findViewById(R.id.txtPass);
        txtUsername = findViewById(R.id.txtUsername);
        btnRegistrate = findViewById(R.id.btnRegistrate);
        inputPass = findViewById(R.id.inputPass);
    }
}
