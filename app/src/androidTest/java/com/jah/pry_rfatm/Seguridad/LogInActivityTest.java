package com.jah.pry_rfatm.Seguridad;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class LogInActivityTest {

    @Test
    public void testUsuarioNoLogueadoNoPuedeModificarEquipo() throws InterruptedException {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signOut();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final boolean[] falloCapturado = {false};
        final Object lock = new Object();

        db.collection("equipos").document("equipo123")
                .update("jugadores", Arrays.asList("nuevoJugador"))
                .addOnSuccessListener(unused -> {
                    fail("Un usuario no logueado no debería poder modificar equipo");
                    synchronized (lock) { lock.notify(); }
                })
                .addOnFailureListener(e -> {
                    falloCapturado[0] = true;
                    synchronized (lock) { lock.notify(); }
                });

        synchronized (lock) { lock.wait(5000); }

        assertTrue("La modificación debería fallar para usuario no logueado", falloCapturado[0]);
    }

}

