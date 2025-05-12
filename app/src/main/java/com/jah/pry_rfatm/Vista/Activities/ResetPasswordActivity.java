package com.jah.pry_rfatm.Vista.Activities;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;

/**
 * Actividad que permite a los usuarios recuperar su contraseña enviando un correo a través de Firebase.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    EditText txtMail;
    Button btnReset;
    MaterialToolbar mtbBarReset;
    private FirebaseAuth mAuth;

    /**
     * Inicializa la actividad, Firebase y la interfaz de usuario.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        txtMail = findViewById(R.id.txtMail);
        btnReset = findViewById(R.id.btnReset);
        mtbBarReset = findViewById(R.id.mtbBarReset);
        FirebaseController.iniciarFirebase(this);
        setSupportActionBar(mtbBarReset);
        mtbBarReset.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        btnReset.setOnClickListener(view -> recuperarContrasenia());
    }

    /**
     * Envía un correo de recuperación de contraseña si el email ingresado es válido.
     */
    private void recuperarContrasenia() {
        String email = txtMail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseController.enviarCorreoRecuperacion(email,
                aVoid -> Toast.makeText(this, "Correo enviado. Revisa tu bandeja.", Toast.LENGTH_LONG).show(),
                e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
        );
    }

    /**
     * Infla el menú en la barra superior.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_registro, menu);
        return true;
    }

    /**
     * Maneja eventos del menú como botón de retroceso.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_atras) finish();
        return false;
    }
}
