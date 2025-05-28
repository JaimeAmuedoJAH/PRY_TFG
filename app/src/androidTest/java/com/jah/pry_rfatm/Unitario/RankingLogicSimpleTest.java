package com.jah.pry_rfatm.Unitario;

import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Modelo.Jugador;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

public class RankingLogicSimpleTest {

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseController.iniciarFirebase(context);
    }

    @Test
    public void testJugador() {
        Jugador jugadorSimulado = new Jugador();
        jugadorSimulado.setNombre("Prueba");
        jugadorSimulado.setVictorias(5);
        jugadorSimulado.setDerrotas(2);

        assertEquals("Prueba", jugadorSimulado.getNombre());
        assertEquals(Integer.valueOf(5), jugadorSimulado.getVictorias());
        assertEquals(Integer.valueOf(2), jugadorSimulado.getDerrotas());
    }
}

