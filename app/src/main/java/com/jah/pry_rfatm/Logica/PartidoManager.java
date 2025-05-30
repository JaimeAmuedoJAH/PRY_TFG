package com.jah.pry_rfatm.Logica;

import android.content.Context;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PartidoManager {
    private final Context context;
    private final FirebaseFirestore db;

    public PartidoManager(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Modifica las estadísticas de los equipos en Firestore.
     * @param idPartido
     * @param puntosABC
     * @param puntosXYZ
     */
    public void modificarEstadisticasEquipos(String idPartido, int puntosABC, int puntosXYZ) {
        db.collection("partidos").document(idPartido).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String idLocal = doc.getString("equipoLocalId").split("/")[2];
                        String idVisitante = doc.getString("equipoVisitanteId").split("/")[2];

                        Map<String, Object> datosABC = new HashMap<>();
                        Map<String, Object> datosXYZ = new HashMap<>();

                        datosABC.put("partidosJugados", FieldValue.increment(1));
                        datosXYZ.put("partidosJugados", FieldValue.increment(1));

                        if (puntosABC > puntosXYZ) {
                            datosABC.put("victorias", FieldValue.increment(1));
                            datosXYZ.put("derrotas", FieldValue.increment(1));
                        } else {
                            datosXYZ.put("victorias", FieldValue.increment(1));
                            datosABC.put("derrotas", FieldValue.increment(1));
                        }

                        db.collection("equipos").document(idLocal).update(datosABC);
                        db.collection("equipos").document(idVisitante).update(datosXYZ);
                    }
                });
    }
}

