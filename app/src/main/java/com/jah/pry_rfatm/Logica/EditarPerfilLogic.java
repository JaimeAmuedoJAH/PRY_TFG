package com.jah.pry_rfatm.Logica;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarPerfilLogic {

    public interface CallbackGuardar {
        void onComplete(boolean success, String message, @Nullable String downloadUrl);
    }

    public static void guardarDatosJugador(String uid, String nombreUsuario, String estilo, Uri imagenUri, CallbackGuardar callback) {
        Map<String, Object> datosJugador = new HashMap<>();
        datosJugador.put("nombre", nombreUsuario);
        datosJugador.put("estilo", estilo);

        if (imagenUri != null) {
            FirebaseController.subirImagenAFirebaseStorage(
                    "usuarios/" + uid + "/fotoPerfil.jpg",
                    imagenUri,
                    downloadUrl -> {
                        if (downloadUrl != null) {
                            datosJugador.put("fotoPerfil", downloadUrl);
                        }
                        FirebaseController.db.collection("usuarios").document(uid)
                                .update(datosJugador)
                                .addOnSuccessListener(unused -> callback.onComplete(true, "Datos actualizados correctamente", downloadUrl))
                                .addOnFailureListener(e -> callback.onComplete(false, "Error al actualizar los datos", null));
                    },
                    e -> callback.onComplete(false, "Error al subir imagen", null)
            );
        } else {
            FirebaseController.db.collection("usuarios").document(uid)
                    .update(datosJugador)
                    .addOnSuccessListener(unused -> callback.onComplete(true, "Datos actualizados correctamente", null))
                    .addOnFailureListener(e -> callback.onComplete(false, "Error al actualizar los datos", null));
        }
    }

    public static void guardarDatosEntrenador(String uid, String nombreUsuario, List<String> titulares, List<String> suplentes, Uri imagenUri, CallbackGuardar callback) {
        FirebaseController.db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String equipoId = documentSnapshot.getString("equipoId");
                    if (equipoId == null || equipoId.isEmpty()) {
                        callback.onComplete(false, "No se encontró el ID del equipo", null);
                        return;
                    }

                    Map<String, Object> datosUsuario = new HashMap<>();
                    datosUsuario.put("nombre", nombreUsuario);

                    // Obtener IDs de titulares
                    obtenerIdsUsuariosPorNombres(titulares,
                            titularesIds -> {
                                // Obtener IDs de suplentes
                                obtenerIdsUsuariosPorNombres(suplentes,
                                        suplentesIds -> {
                                            Map<String, Object> datosEquipo = new HashMap<>();
                                            datosEquipo.put("jugadores", titularesIds);
                                            datosEquipo.put("suplentes", suplentesIds);

                                            if (imagenUri != null) {
                                                FirebaseController.subirImagenAFirebaseStorage(
                                                        "usuarios/" + uid + "/fotoPerfil.jpg",
                                                        imagenUri,
                                                        downloadUrl -> {
                                                            if (downloadUrl != null) {
                                                                datosUsuario.put("fotoPerfil", downloadUrl);
                                                            }
                                                            actualizarUsuarioYEquipo(uid, equipoId, datosUsuario, datosEquipo, callback, downloadUrl);
                                                        },
                                                        e -> callback.onComplete(false, "Error al subir la imagen", null)
                                                );
                                            } else {
                                                actualizarUsuarioYEquipo(uid, equipoId, datosUsuario, datosEquipo, callback, null);
                                            }
                                        },
                                        e -> callback.onComplete(false, "Error al obtener IDs suplentes", null)
                                );
                            },
                            e -> callback.onComplete(false, "Error al obtener IDs titulares", null)
                    );
                })
                .addOnFailureListener(e -> callback.onComplete(false, "Error al obtener los datos del usuario", null));
    }

    // Método para obtener los IDs de usuarios por sus nombres
    public static void obtenerIdsUsuariosPorNombres(List<String> nombres, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        if (nombres == null || nombres.isEmpty()) {
            onSuccess.onSuccess(new ArrayList<>());
            return;
        }

        // Firestore tiene limitaciones en whereIn (máximo 10 elementos), asegúrate que la lista no sea muy larga
        FirebaseController.db.collection("usuarios")
                .whereEqualTo("tipoUsuario", "jugador")
                .whereIn("nombre", nombres)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> ids = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        ids.add(doc.getId());
                    }
                    onSuccess.onSuccess(ids);
                })
                .addOnFailureListener(onFailure);
    }

    private static void actualizarUsuarioYEquipo(String uid, String equipoId, Map<String, Object> datosUsuario, Map<String, Object> datosEquipo, CallbackGuardar callback, String downloadUrl) {
        FirebaseController.db.collection("usuarios").document(uid)
                .update(datosUsuario)
                .addOnSuccessListener(unused -> {
                    FirebaseController.db.collection("equipos").document(equipoId.split("/")[2])
                            .update(datosEquipo)
                            .addOnSuccessListener(unused2 -> callback.onComplete(true, "Datos actualizados correctamente", downloadUrl))
                            .addOnFailureListener(e -> callback.onComplete(false, "Error al actualizar el equipo", null));
                })
                .addOnFailureListener(e -> callback.onComplete(false, "Error al actualizar el usuario", null));
    }
}
