package com.jah.pry_rfatm.Logica;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UIManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JugadorManager {
    private final Context context;
    private final UIManager ui;
    private final FirebaseFirestore db;

    public JugadorManager(Context context, UIManager ui) {
        this.context = context;
        this.ui = ui;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Carga el nombre de los equipos desde Firestore.
     * @param idABC
     * @param idXYZ
     */
    public void cargarNombreEquipos(String idABC, String idXYZ) {
        cargarNombre(idABC, true);
        cargarNombre(idXYZ, false);
    }

    /**
     * Carga el nombre de los jugadores desde Firestore.
     * @param equipoId
     * @param esABC
     */
    private void cargarNombre(String equipoId, boolean esABC) {
        db.collection("equipos").document(equipoId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null) ui.setNombreEquipo(esABC, nombre);
                    }
                })
                .addOnFailureListener(e -> Log.e("JugadorManager", context.getString(R.string.error_al_cargar_nombre_de_equipo), e));
    }

    /**
     * Carga los nombres de los jugadores desde Firestore.
     * @param equipoId
     * @param esABC
     */
    public void cargarJugadores(String equipoId, boolean esABC) {
        db.collection("equipos").document(equipoId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Object jugadoresObj = doc.get("jugadores");
                        if (jugadoresObj instanceof List<?>) {
                            List<?> lista = (List<?>) jugadoresObj;
                            for (int i = 0; i < lista.size(); i++) {
                                Object jugador = lista.get(i);
                                if (jugador instanceof String) {
                                    String jugadorId = (String) jugador;
                                    int finalI = i;
                                    db.collection("usuarios").document(jugadorId).get()
                                            .addOnSuccessListener(jugadorDoc -> {
                                                if (jugadorDoc.exists()) {
                                                    String nombre = jugadorDoc.getString("nombre");
                                                    if (nombre != null) ui.setNombreJugador(esABC, finalI, nombre);
                                                }
                                            });
                                } else if (jugador instanceof Map) {
                                    String nombre = extraerNombreJugador(jugador);
                                    if (nombre != null) ui.setNombreJugador(esABC, i, nombre);
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("JugadorManager", context.getString(R.string.error_al_cargar_jugadores), e));
    }

    /**
     * Extrae el nombre de un jugador de un objeto.
     * @param jugador
     * @return
     */
    private String extraerNombreJugador(Object jugador) {
        if (jugador instanceof String) return (String) jugador;
        if (jugador instanceof Map) {
            Object nombre = ((Map<?, ?>) jugador).get("nombre");
            if (nombre instanceof String) return (String) nombre;
        }
        return null;
    }

    /**
     * Actualiza las estadísticas de los jugadores en Firestore.
     * @param nombre
     * @param victoria
     */
    public void actualizarEstadisticasJugador(String nombre, boolean victoria) {
        db.collection("usuarios")
                .whereEqualTo("nombre", nombre)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        Map<String, Object> updates = new HashMap<>();

                        long victorias = doc.getLong("victorias") != null ? doc.getLong("victorias") : 0;
                        long derrotas = doc.getLong("derrotas") != null ? doc.getLong("derrotas") : 0;
                        long jugados = doc.getLong("partidosJugados") != null ? doc.getLong("partidosJugados") : 0;

                        updates.put("partidosJugados", jugados + 1);
                        if (victoria) updates.put("victorias", victorias + 1);
                        else updates.put("derrotas", derrotas + 1);

                        doc.getReference().update(updates);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(context, context.getString(R.string.error_al_actualizar_estad_sticas), Toast.LENGTH_SHORT).show()
                );
    }
}
