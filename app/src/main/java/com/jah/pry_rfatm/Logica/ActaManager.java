package com.jah.pry_rfatm.Logica;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UIManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActaManager {

    private final Context context;
    private final UIManager ui;
    private JugadorManager jugadorManager;
    private PartidoManager partidoManager;
    private final FirebaseFirestore db;
    private String idPartido;
    private int puntosABC, puntosXYZ;

    public ActaManager(Context context, UIManager ui) {
        this.context = context;
        this.ui = ui;
        this.db = FirebaseFirestore.getInstance();
    }

    public void setIdPartido(String idPartido) {
        this.idPartido = idPartido;
        this.jugadorManager = new JugadorManager(context, ui);
        this.partidoManager = new PartidoManager(context);
    }

    /**
     * Carga los datos de la acta desde Firestore.
     */
    public void cargarDatosActa() {
        if (idPartido == null) return;
        db.collection("actas").document(idPartido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> actaData = documentSnapshot.getData();
                        if (actaData != null) ui.cargarPartidosDesdeActa(actaData);
                    }
                })
                .addOnFailureListener(e -> Log.e("ActaManager", context.getString(R.string.error_al_cargar_acta), e));
    }

    /**
     * Guarda los datos de la acta en Firestore.
     */
    public void guardarActa() {
        Map<String, Object> actaMap = ui.obtenerActaDeUI();
        boolean todosJugados = actualizarPuntuacionFinal(actaMap);

        db.collection("partidos").document(idPartido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String estado = documentSnapshot.getString("estado");

                    boolean esPendiente = "pendiente".equalsIgnoreCase(estado != null ? estado.trim() : "");

                    if (!todosJugados) {
                        Toast.makeText(context, context.getString(R.string.necesitas_rellenar_todos_los_campos), Toast.LENGTH_LONG).show();
                    } else if (!esPendiente) {
                        Toast.makeText(context, context.getString(R.string.el_partido_ya_fue_jugado), Toast.LENGTH_LONG).show();
                    }

                    db.collection("actas").document(idPartido).set(actaMap)
                            .addOnSuccessListener(unused -> {
                                if (todosJugados && esPendiente) {
                                    actualizarEstadisticasJugadores(actaMap);
                                    partidoManager.modificarEstadisticasEquipos(idPartido, puntosABC, puntosXYZ);
                                    actualizarEstadoPartido((String) actaMap.get("resultadoFinal"));
                                    SharedPreferences prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE);
                                    prefs.edit().putBoolean("camposDeshabilitados", true).apply();
                                    Toast.makeText(context, R.string.acta_guardada_con_xito, Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, R.string.error_al_guardar_el_acta, Toast.LENGTH_SHORT).show();
                                Log.e("ActaManager", context.getString(R.string.error_al_guardar_acta), e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, context.getString(R.string.error_al_obtener_el_estado_del_partido), Toast.LENGTH_SHORT).show();
                    Log.e("ActaManager", context.getString(R.string.error_al_verificar_estado_del_partido), e);
                });
    }

    /**
     * Actualiza el estado del partido en Firestore.
     * @param resultadoFinal
     */
    private void actualizarEstadoPartido(String resultadoFinal) {
        Map<String, Object> update = new HashMap<>();
        update.put("estado", "jugado");
        update.put("resultado", resultadoFinal);
        update.put("setsGanados", puntosABC);
        update.put("setsPerdidos", puntosXYZ);

        db.collection("partidos").document(idPartido)
                .update(update)
                .addOnSuccessListener(unused -> Log.d("ActaManager", context.getString(R.string.partido_actualizado)))
                .addOnFailureListener(e -> Log.e("ActaManager", context.getString(R.string.error_al_actualizar_estado_del_partido), e));
    }

    /**
     * Verifica si los campos están deshabilitados y si el partido ya fue jugado.
     */
    public void verEstadoPartidoYDeshabilitarCamposSiEsNecesario() {
        SharedPreferences prefs = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean deshabilitar = prefs.getBoolean("camposDeshabilitados", false);
        db.collection("partidos").document(idPartido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String estado = documentSnapshot.getString("estado");
                        if (deshabilitar && "jugado".equals(estado)) ui.deshabilitarCampos();
                    }
                })
                .addOnFailureListener(e -> Log.e("ActaManager", context.getString(R.string.error_al_obtener_el_estado_del_partido), e));
    }

    /**
     * Actualiza la puntuación final del partido.
     * @param actaData
     * @return
     */
    private boolean actualizarPuntuacionFinal(Map<String, Object> actaData) {
        puntosABC = 0;
        puntosXYZ = 0;
        boolean todosJugados = true;

        List<String> partidos = Arrays.asList("partidoAY", "partidoBX", "partidoCZ", "partidoAY2", "partidoCX", "partidoBZ", "partidoAX2");

        for (String key : partidos) {
            Map<String, Object> partido = (Map<String, Object>) actaData.get(key);
            if (partido != null && partido.containsKey("resultado")) {
                String resultado = (String) partido.get("resultado");
                if (resultado != null && resultado.contains("-")) {
                    String[] sets = resultado.split("-");
                    try {
                        int a = Integer.parseInt(sets[0].trim());
                        int b = Integer.parseInt(sets[1].trim());
                        if (a == 3) puntosABC++;
                        else if (b == 3) puntosXYZ++;
                        else todosJugados = false;
                    } catch (Exception e) {
                        todosJugados = false;
                    }
                } else {
                    todosJugados = false;
                }
            } else {
                todosJugados = false;
            }
        }

        String resultadoFinal = puntosABC + "-" + puntosXYZ;
        actaData.put("resultadoFinal", resultadoFinal);
        ui.setResultadoFinal(resultadoFinal);

        return todosJugados;
    }

    /**
     * Actualiza las estadísticas de los jugadores en Firestore.
     * @param actaData
     */
    private void actualizarEstadisticasJugadores(Map<String, Object> actaData) {
        List<String> partidos = Arrays.asList("partidoAY", "partidoBX", "partidoCZ", "partidoAY2", "partidoCX", "partidoBZ", "partidoAX2");

        // Contadores por jugador
        Map<String, Integer> victoriasPorJugador = new HashMap<>();
        Map<String, Integer> derrotasPorJugador = new HashMap<>();

        for (String key : partidos) {
            Map<String, Object> partido = (Map<String, Object>) actaData.get(key);
            if (partido != null) {
                String nombreABC = (String) partido.get("jugadorABC");
                String nombreXYZ = (String) partido.get("jugadorXYZ");
                String resultado = (String) partido.get("resultado");

                if (nombreABC != null && nombreXYZ != null && resultado != null && resultado.contains("-")) {
                    String[] sets = resultado.trim().split("-");

                    if (sets.length == 2) {
                        try {
                            int setsABC = Integer.parseInt(sets[0].trim());
                            int setsXYZ = Integer.parseInt(sets[1].trim());

                            if (setsABC == 3 && setsXYZ < 3) {
                                // Gana ABC
                                victoriasPorJugador.put(nombreABC, victoriasPorJugador.getOrDefault(nombreABC, 0) + 1);
                                derrotasPorJugador.put(nombreXYZ, derrotasPorJugador.getOrDefault(nombreXYZ, 0) + 1);
                            } else if (setsXYZ == 3 && setsABC < 3) {
                                // Gana XYZ
                                victoriasPorJugador.put(nombreXYZ, victoriasPorJugador.getOrDefault(nombreXYZ, 0) + 1);
                                derrotasPorJugador.put(nombreABC, derrotasPorJugador.getOrDefault(nombreABC, 0) + 1);
                            }

                        } catch (NumberFormatException e) {
                            Log.e("ActaManager", "Resultado inválido en " + key + ": " + resultado);
                        }
                    }
                }
            }
        }
        for (Map.Entry<String, Integer> entry : victoriasPorJugador.entrySet()) {
            String jugador = entry.getKey();
            int victorias = entry.getValue();
            jugadorManager.actualizarEstadisticasJugadorPorVictorias(jugador, victorias);
        }

        for (Map.Entry<String, Integer> entry : derrotasPorJugador.entrySet()) {
            String jugador = entry.getKey();
            int derrotas = entry.getValue();
            jugadorManager.actualizarEstadisticasJugadorPorDerrotas(jugador, derrotas);
        }
    }

    /**
     * Crea la acta inicial si no existe.
     */
    public void crearActaSiNoExiste() {
        if (idPartido == null) return;
        db.collection("partidos").document(idPartido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String estado = documentSnapshot.getString("estado");
                        if ("pendiente".equalsIgnoreCase(estado != null ? estado.trim() : "")) {
                            db.collection("actas").document(idPartido).get()
                                    .addOnSuccessListener(actaDoc -> {
                                        if (!actaDoc.exists()) {
                                            Map<String, Object> actaInicial = ui.obtenerActaDeUI();
                                            actaInicial.put("resultadoFinal", "0-0");
                                            db.collection("actas").document(idPartido).set(actaInicial)
                                                    .addOnSuccessListener(unused -> Log.d("ActaManager", context.getString(R.string.acta_inicial_creada)))
                                                    .addOnFailureListener(e -> Log.e("ActaManager", context.getString(R.string.error_al_crear_acta_inicial), e));
                                        }
                                    });
                        }
                    }
                });
    }
}
