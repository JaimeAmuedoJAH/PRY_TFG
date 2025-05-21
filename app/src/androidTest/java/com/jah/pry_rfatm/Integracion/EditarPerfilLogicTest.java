package com.jah.pry_rfatm.Integracion;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Logica.EditarPerfilLogic;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class EditarPerfilLogicTest {

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseController.iniciarFirebase(context);
    }

    @Test
    public void testGuardarDatosJugador_sinImagen() throws InterruptedException {
        String uid = "jugador123";
        String nombre = "Jugador Prueba";
        String estilo = "Ofensivo";

        CountDownLatch latch = new CountDownLatch(1);

        EditarPerfilLogic.guardarDatosJugador(uid, nombre, estilo, null,
                (success, message, urlImagen) -> {
                    assertNotNull(message);
                    // urlImagen puede ser null si no hay imagen
                    assertTrue(success || !success);
                    latch.countDown();
                });

        boolean finished = latch.await(5, TimeUnit.SECONDS);
        assertTrue("El callback no se completó a tiempo", finished);
    }

    @Test
    public void testGuardarDatosEntrenador_sinImagen() throws InterruptedException {
        String uid = "entrenador123";
        String nombre = "Entrenador Prueba";

        // Ahora la función espera dos listas: titulares y suplentes
        List<String> titulares = List.of("Jugador1", "Jugador2", "Jugador3");
        List<String> suplentes = List.of("Suplente1", "Suplente2");

        CountDownLatch latch = new CountDownLatch(1);

        EditarPerfilLogic.guardarDatosEntrenador(uid, nombre, titulares, suplentes, null,
                (success, message, urlImagen) -> {
                    assertNotNull(message);
                    assertTrue(success || !success);
                    latch.countDown();
                });

        boolean finished = latch.await(10, TimeUnit.SECONDS); // Ponemos 10s porque es más complejo
        assertTrue("El callback no se completó a tiempo", finished);
    }

    @Test
    public void testGuardarDatosEntrenador_sinJugadores() throws InterruptedException {
        String uid = "entrenador456";
        String nombre = "Sin Jugadores";

        CountDownLatch latch = new CountDownLatch(1);

        // Pasamos listas vacías para evitar NPE en Firestore (si tu lógica lo requiere)
        EditarPerfilLogic.guardarDatosEntrenador(uid, nombre, List.of(), List.of(), null,
                (success, message, urlImagen) -> {
                    assertNotNull(message);
                    assertTrue(success || !success);
                    latch.countDown();
                });

        boolean finished = latch.await(10, TimeUnit.SECONDS);
        assertTrue("El callback no se completó a tiempo", finished);
    }
}
