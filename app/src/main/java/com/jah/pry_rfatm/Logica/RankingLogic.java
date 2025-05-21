package com.jah.pry_rfatm.Logica;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Modelo.Jugador;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de lógica para la clasificación de equipos.
 */
public class RankingLogic {

    private final FirebaseFirestore db;

    public RankingLogic(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * Interfaz para manejar los resultados de la carga de jugadores
     */
    public interface JugadoresCallback {
        void onJugadoresCargados(List<Jugador> jugadores);
        void onError(Exception e);
    }

    /**
     * Carga la lista de jugadores ordenados por victorias de manera descendente
     * @param callback
     */
    public void obtenerJugadoresOrdenados(JugadoresCallback callback) {
        FirebaseController.db.collection("usuarios")
                .whereEqualTo("tipoUsuario", "jugador")
                .orderBy("victorias", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Jugador> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : snapshot) {
                        Jugador jugador = doc.toObject(Jugador.class);
                        lista.add(jugador);
                    }
                    callback.onJugadoresCargados(lista);
                })
                .addOnFailureListener(callback::onError);
    }

    /**
     * Interfaz para manejar los resultados del nombre de la liga
     */
    public interface NombreLigaCallback {
        void onNombreObtenido(String nombre);
        void onError(Exception e);
    }

    /**
     * Obtiene el nombre de la liga a la que pertenece el equipo del usuario
     * @param uid
     * @param callback
     */
    public void obtenerNombreLiga(String uid, NombreLigaCallback callback) {
        FirebaseController.db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(userDoc -> {
                    String equipoId = userDoc.getString("equipoId");
                    if (equipoId != null && !equipoId.isEmpty()) {
                        FirebaseController.db.collection("equipos").document(equipoId.split("/")[2]).get()
                                .addOnSuccessListener(equipoDoc -> {
                                    String ligaId = equipoDoc.getString("ligaId");
                                    if (ligaId != null && !ligaId.isEmpty()) {
                                        FirebaseController.db.collection("ligas").document(ligaId.split("/")[2]).get()
                                                .addOnSuccessListener(ligaDoc -> {
                                                    String nombre = ligaDoc.getString("nombre");
                                                    callback.onNombreObtenido(nombre != null ? nombre : "Sin nombre");
                                                })
                                                .addOnFailureListener(callback::onError);
                                    } else {
                                        callback.onError(new Exception("Liga no asignada"));
                                    }
                                })
                                .addOnFailureListener(callback::onError);
                    } else {
                        callback.onError(new Exception("Equipo no asignado"));
                    }
                })
                .addOnFailureListener(callback::onError);
    }
}
