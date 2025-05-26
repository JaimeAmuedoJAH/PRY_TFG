package com.jah.pry_rfatm.Seguridad;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.Controlador.FirebaseController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(AndroidJUnit4.class)
public class ModificarEquipoSecurityTest {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseController.iniciarFirebase(context);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void jugadorNoPuedeModificarEquipo() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            fail("No hay usuario autenticado. Asegúrate de haber iniciado sesión con un jugador.");
            return;
        }

        db.collection("equipos").document("equipo123")
                .update("jugadores", Arrays.asList("nuevoJugador"))
                .addOnSuccessListener(unused -> {
                    fail("Un jugador no debería poder modificar un equipo si las reglas de seguridad están bien configuradas.");
                })
                .addOnFailureListener(e -> {
                    // Fallo esperado
                    assertTrue(true);
                });

        try {
            Thread.sleep(2000); // Espera breve para que Firebase responda (mejor usar IdlingResource en producción)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
