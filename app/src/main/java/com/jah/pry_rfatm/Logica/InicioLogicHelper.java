package com.jah.pry_rfatm.Logica;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InicioLogicHelper {

    /**
     * Procesa la lista de nombres de equipos y crea un mapa nombre -> ID.
     */
    public static Map<String, String> procesarEquipos(List<String> nombres, List<String> ids) {
        Map<String, String> nombreToId = new HashMap<>();
        for (int i = 0; i < nombres.size(); i++) {
            nombreToId.put(nombres.get(i), ids.get(i));
        }
        return nombreToId;
    }

    /**
     * Determina el tipo de usuario en base a selección de radio buttons.
     */
    public static String obtenerTipoUsuario(boolean jugadorChecked, boolean entrenadorChecked) {
        if (jugadorChecked) return "jugador";
        if (entrenadorChecked) return "entrenador";
        return "";
    }

    /**
     * Construye la ruta Firebase del equipo.
     */
    public static String construirPathEquipo(String idEquipo) {
        return "/equipos/" + idEquipo;
    }
}
