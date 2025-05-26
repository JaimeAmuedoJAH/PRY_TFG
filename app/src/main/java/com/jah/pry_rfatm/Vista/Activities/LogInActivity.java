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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity que permite a los usuarios iniciar sesión mediante correo y contraseña o con Google.
 * Verifica si ya hay un usuario autenticado para omitir el inicio de sesión.
 * Usa Firebase para la autenticación.
 */
public class LogInActivity extends AppCompatActivity {

    public EditText txtCorreo;
    public EditText txtPass;
    TextView lblRecuperar;
    Button btnIni, btnIniGoogle, btnRegistrar;
    Intent intent;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    /**
     * Inicializa la actividad, configura UI y listeners.
     */
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

    /**
     * Inicia sesión usando correo y contraseña mediante Firebase.
     */
    public void iniciarSesionConCorreo() {
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
                        FirebaseUser user = FirebaseController.mAuth.getCurrentUser();

                        if (user != null && !user.isEmailVerified()) {
                            Toast.makeText(this, getString(R.string.debes_verificar_tu_correo_electr_nico_antes_de_continuar), Toast.LENGTH_LONG).show();
                            FirebaseController.mAuth.signOut();  // importante para cerrar sesión del usuario no verificado
                            return;
                        }

                        irAMainActivity();
                    } else {
                        Toast.makeText(LogInActivity.this, getString(R.string.correo_o_contrase_a_incorrectos), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Procesa el resultado del intento de inicio de sesión con Google.
     * @param requestCode Código de solicitud
     * @param resultCode Resultado del intent
     * @param data Datos del intent
     */
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
                Toast.makeText(this, getString(R.string.error_en_inicio_de_sesi_n_con_google) + e.getMessage(), Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Autentica al usuario en Firebase usando las credenciales de Google.
     * @param idToken Token de ID proporcionado por Google
     */
    private void firebaseAuthConGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseController.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        irAMainActivity();
                    } else {
                        Toast.makeText(this, R.string.fall_la_autenticaci_n_con_firebase, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Lanza el intent para iniciar sesión con una cuenta de Google.
     */
    private void iniciarSesionGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Redirige al usuario a la actividad principal.
     */
    private void irAMainActivity() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();
            String nombre = firebaseUser.getDisplayName(); // nombre del correo si está disponible
            String fotoPerfil = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "";

            DocumentReference userRef = FirebaseController.db.collection("usuarios").document(uid);
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (!documentSnapshot.exists()) {
                    // Crear el documento si no existe
                    Map<String, Object> data = new HashMap<>();
                    data.put("nombre", nombre != null ? nombre : "");
                    data.put("fotoPerfil", fotoPerfil);
                    userRef.set(data);
                } else {
                    // Si existe, actualizar si no están presentes
                    Map<String, Object> updates = new HashMap<>();
                    if (!documentSnapshot.contains("nombre")) {
                        updates.put("nombre", nombre != null ? nombre : "");
                    }
                    if (!documentSnapshot.contains("fotoPerfil")) {
                        updates.put("fotoPerfil", fotoPerfil);
                    }
                    if (!updates.isEmpty()) {
                        userRef.update(updates);
                    }
                }
            });
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Inicializa los componentes de la interfaz de usuario y Firebase.
     */
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