package com.jah.pry_rfatm.Logica;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.Vista.Recursos.UIManager;

import java.util.Map;

public class FirestoreLoader {

    private final FirebaseFirestore db;
    private final UIManager ui;
    private final String idPartido;

    public FirestoreLoader(String idPartido, UIManager ui) {
        this.idPartido = idPartido;
        this.ui = ui;
        this.db = FirebaseFirestore.getInstance();
    }

    /**
     * Carga los datos del acta desde Firestore si el partido fue jugado.
     */
    public void cargarActaSiFueJugado() {
        db.collection("partidos").document(idPartido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String estado = documentSnapshot.getString("estado");
                        if ("jugado".equals(estado)) {
                            cargarDatosActaDesdeFirestore();
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreLoader", "Error al obtener estado del partido", e));
    }

    /**
     * Carga los datos del acta desde Firestore.
     */
    private void cargarDatosActaDesdeFirestore() {
        db.collection("actas").document(idPartido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> actaData = documentSnapshot.getData();
                        if (actaData != null) {
                            ui.cargarPartidosDesdeActa(actaData);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FirestoreLoader", "Error al cargar datos del acta", e));
    }
}

