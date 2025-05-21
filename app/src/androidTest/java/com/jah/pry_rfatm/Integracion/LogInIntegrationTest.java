package com.jah.pry_rfatm.Integracion;

import static com.jah.pry_rfatm.Controlador.FirebaseController.mAuth;
import static org.junit.Assert.*;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Prueba de integración para verificar que un usuario puede iniciar sesión correctamente
 * usando Firebase Authentication con correo y contraseña.
 */
@RunWith(AndroidJUnit4.class)
public class LogInIntegrationTest {

    private final String CORREO_PRUEBA = "testuser@example.com";
    private final String PASS_PRUEBA = "Holamundo1?";

    /**
     * Configura Firebase antes de cada prueba.
     */
    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Prueba de inicio de sesión con correo y contraseña.
     * @throws InterruptedException
     */
    @Test
    public void testLoginConCorreoYContrasena() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        final boolean[] resultado = {false};
        final FirebaseUser[] usuarioResultado = {null};

            mAuth.signInWithEmailAndPassword(CORREO_PRUEBA, PASS_PRUEBA)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            resultado[0] = true;
                            usuarioResultado[0] = user;
                        }
                    }
                    latch.countDown(); // Libera el hilo principal
                });

        boolean terminado = latch.await(10, TimeUnit.SECONDS); // Espera a que termine la auth

        assertTrue("La autenticación con Firebase falló.", resultado[0]);
        assertNotNull("El usuario autenticado es nulo.", usuarioResultado[0]);
        assertNotNull("El UID del usuario es nulo.", usuarioResultado[0].getUid());
        assertEquals("El usuario autenticado no coincide con el actual.", usuarioResultado[0], mAuth.getCurrentUser());
    }

}

