package com.jah.pry_rfatm.Vista.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

public class LogInActivity extends AppCompatActivity {

    EditText txtCorreo, txtPass;
    TextView lblRecuperar;
    Button btnIni, btnIniGoogle, btnRegistrar;
    Intent intent;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        UtilesUI.configurarStatusBar(this);

        initComponents();
        //Verifica si ya hay un usuario logueado
        if (FirebaseController.mAuth.getCurrentUser() != null) {
            irAMainActivity();
            return; //Evita que cargue el login innecesariamente
        }
        //Activador para registrarnos con nuestro correo.
        btnRegistrar.setOnClickListener(v -> {
            intent = new Intent(getApplicationContext(), RegistroActivity.class);
            startActivity(intent);
        });
        btnIni.setOnClickListener(v -> iniciarSesionConCorreo());
        btnIniGoogle.setOnClickListener(v -> iniciarSesionGoogle());
        lblRecuperar.setOnClickListener(v -> {
            intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void iniciarSesionConCorreo() {
        String correo = txtCorreo.getText().toString().trim();
        String pass = txtPass.getText().toString().trim();

        if (correo.isEmpty()) {
            txtCorreo.setError("El correo es obligatorio");
            return;
        }
        if (pass.isEmpty()) {
            txtPass.setError("La contraseña es obligatoria");
            return;
        }

        FirebaseController.mAuth.signInWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        irAMainActivity();
                    } else {
                        Toast.makeText(LogInActivity.this, "Correo o contraseña incorrectos.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    //Resultado del inicio de sesión. Si es existosa cambiamos de activity, sino muestra un mensaje de error
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthConGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("GoogleSignInError", "Código: " + e.getStatusCode(), e);
                Toast.makeText(this, "Error en inicio de sesión con Google: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }
    //Autentificar usuarios. De esta forma pueden iniciar sesión en la app
    private void firebaseAuthConGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseController.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        irAMainActivity();
                    } else {
                        Toast.makeText(this, "Falló la autenticación con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //Inicia el proceso de Login, abriendo la ventana emergente para seleccionar cuenta de Google para el inicio de sesión
    private void iniciarSesionGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    //Si ha salido bien la autenticación te lleva al MainActivity
    private void irAMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    //Iniciamos componentes de la Activity
    private void initComponents() {
        txtCorreo = findViewById(R.id.txtCorreo);
        txtPass = findViewById(R.id.txtPass);
        btnIni = findViewById(R.id.btnIni);
        btnIniGoogle = findViewById(R.id.btnIniGoogle);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        lblRecuperar = findViewById(R.id.lblRecuperar);
        FirebaseController.iniciarFirebase(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
}