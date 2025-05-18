package com.jah.pry_rfatm.Logica;

import android.util.Pair;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.Modelo.Equipo;
import com.jah.pry_rfatm.Modelo.Grupo;

import java.util.ArrayList;
import java.util.List;

public class ClasificacionLogic {

    /**
     * Interfaz para manejar los resultados de la obtención de la clasificación.
     */
    public interface ClasificacionCallback {
        void onClasificacionObtenida(List<Pair<Equipo, Integer>> equiposOrdenados);
        void onError(Exception e);
    }

    /**
     * Obtiene la clasificación de equipos para un grupo dado.
     * @param grupo
     * @param db
     * @param callback
     */
    public static void obtenerClasificacion(Grupo grupo, FirebaseFirestore db, ClasificacionCallback callback) {
        List<String> idsEquipos = grupo.getEquipos();
        if (idsEquipos == null || idsEquipos.isEmpty()) {
            callback.onClasificacionObtenida(new ArrayList<>());
            return;
        }

        List<Pair<Equipo, Integer>> listaEquipos = new ArrayList<>();

        for (String idRef : idsEquipos) {
            String idEquipo = idRef.split("/")[1];

            db.collection("equipos").document(idEquipo).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Equipo equipo = documentSnapshot.toObject(Equipo.class);
                            if (equipo != null) {
                                int puntos = equipo.getVictorias() * 3;
                                listaEquipos.add(new Pair<>(equipo, puntos));

                                if (listaEquipos.size() == idsEquipos.size()) {
                                    listaEquipos.sort((e1, e2) -> Integer.compare(e2.second, e1.second));
                                    callback.onClasificacionObtenida(listaEquipos);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(callback::onError);
        }
    }

    /**
     * Obtiene la clasificación de equipos desde una lista de equipos.
     * @param equipos
     * @param callback
     */
    public static void obtenerClasificacionDesdeEquipos(List<Equipo> equipos, ClasificacionLogic.ClasificacionCallback callback) {
        // Calcula los puntos por cada equipo
        List<Pair<Equipo, Integer>> equiposOrdenados = new ArrayList<>();

        for (Equipo equipo : equipos) {
            int puntos = equipo.getVictorias() * 3 + equipo.getDerrotas() * 0;
            equiposOrdenados.add(new Pair<>(equipo, puntos));
        }

        // Ordenar por puntos de mayor a menor
        equiposOrdenados.sort((e1, e2) -> Integer.compare(e2.second, e1.second));

        callback.onClasificacionObtenida(equiposOrdenados);
    }
}

