package com.jah.pry_rfatm.Funcionalidad;

import static org.junit.Assert.*;

import android.util.Pair;

import com.jah.pry_rfatm.Logica.ClasificacionLogic;
import com.jah.pry_rfatm.Modelo.Equipo;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ClasificacionLogicTest {

    @Test
    public void testObtenerClasificacionDesdeEquipos_ordenCorrecto() {
        Equipo equipo1 = new Equipo();
        equipo1.setVictorias(5);
        equipo1.setDerrotas(2);

        Equipo equipo2 = new Equipo();
        equipo2.setVictorias(3);
        equipo2.setDerrotas(4);

        List<Equipo> equipos = new ArrayList<>();
        equipos.add(equipo1);
        equipos.add(equipo2);

        ClasificacionLogic.ClasificacionCallback callback = new ClasificacionLogic.ClasificacionCallback() {
            @Override
            public void onClasificacionObtenida(List<Pair<Equipo, Integer>> equiposOrdenados) {
                assertEquals(2, equiposOrdenados.size());
                assertEquals(equipo1, equiposOrdenados.get(0).first); // equipo1 debe ser primero (más victorias)
                assertEquals(15, (int)equiposOrdenados.get(0).second); // 5*3
                assertEquals(9, (int)equiposOrdenados.get(1).second);  // 3*3
            }

            @Override
            public void onError(Exception e) {
                fail("No debería dar error");
            }
        };

        ClasificacionLogic.obtenerClasificacionDesdeEquipos(equipos, callback);
    }
}
