package com.jah.pry_rfatm.Unitario;

import static org.junit.Assert.assertEquals;

import com.jah.pry_rfatm.Logica.InicioLogicHelper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InicioLogicHelperTest {
    @Test
    public void testObtenerTipoUsuario() {
        assertEquals("jugador", InicioLogicHelper.obtenerTipoUsuario(true, false));
        assertEquals("entrenador", InicioLogicHelper.obtenerTipoUsuario(false, true));
        assertEquals("", InicioLogicHelper.obtenerTipoUsuario(false, false));
    }

    @Test
    public void testProcesarEquipos() {
        List<String> nombres = Arrays.asList("Equipo A", "Equipo B");
        List<String> ids = Arrays.asList("idA", "idB");

        Map<String, String> resultado = InicioLogicHelper.procesarEquipos(nombres, ids);
        assertEquals("idA", resultado.get("Equipo A"));
        assertEquals("idB", resultado.get("Equipo B"));
    }

    @Test
    public void testConstruirPathEquipo() {
        assertEquals("/equipos/123abc", InicioLogicHelper.construirPathEquipo("123abc"));
    }
}
