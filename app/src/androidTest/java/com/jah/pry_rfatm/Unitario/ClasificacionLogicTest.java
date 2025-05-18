package com.jah.pry_rfatm.Unitario;

import org.junit.Test;
import static org.junit.Assert.*;
import android.util.Pair;

import com.jah.pry_rfatm.Logica.ClasificacionLogic;
import com.jah.pry_rfatm.Modelo.Equipo;

import java.util.*;

public class ClasificacionLogicTest {

    @Test
    public void testObtenerClasificacionDesdeEquipos() {
        // Crear datos simulados
        Equipo equipoA = new Equipo();
        equipoA.setNombre("Equipo A");
        equipoA.setVictorias(3);
        equipoA.setDerrotas(1);

        Equipo equipoB = new Equipo();
        equipoB.setNombre("Equipo B");
        equipoB.setVictorias(2);
        equipoB.setDerrotas(2);

        Equipo equipoC = new Equipo();
        equipoC.setNombre("Equipo C");
        equipoC.setVictorias(1);
        equipoC.setDerrotas(3);

        List<Equipo> listaEquipos = Arrays.asList(equipoA, equipoB, equipoC);

        ClasificacionLogic.obtenerClasificacionDesdeEquipos(listaEquipos, new ClasificacionLogic.ClasificacionCallback() {
            @Override
            public void onClasificacionObtenida(List<Pair<Equipo, Integer>> equiposOrdenados) {
                assertEquals(3, equiposOrdenados.size());

                // Equipo A debe estar primero con 9 puntos
                assertEquals("Equipo A", equiposOrdenados.get(0).first.getNombre());
                assertEquals(Integer.valueOf(9), equiposOrdenados.get(0).second);

                // Equipo B segundo con 6 puntos
                assertEquals("Equipo B", equiposOrdenados.get(1).first.getNombre());
                assertEquals(Integer.valueOf(6), equiposOrdenados.get(1).second);

                // Equipo C tercero con 3 puntos
                assertEquals("Equipo C", equiposOrdenados.get(2).first.getNombre());
                assertEquals(Integer.valueOf(3), equiposOrdenados.get(2).second);
            }

            @Override
            public void onError(Exception e) {
                fail("No se esperaba un error: " + e.getMessage());
            }
        });
    }
}

