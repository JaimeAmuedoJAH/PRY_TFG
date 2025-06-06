package com.jah.pry_rfatm.Logica;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.DocumentSnapshot;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jah.pry_rfatm.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase de lógica para la edición de perfil.
 */
public class EditarPerfilLogic {

    /**
     * Interfaz para manejar los resultados de la actualización de datos.
     */
    public interface CallbackGuardar {
        void onComplete(boolean success, String message, @Nullable String downloadUrl);
    }

    /**
     * Guarda los datos del jugador en Firestore.
     * @param uid
     * @param nombreUsuario
     * @param estilo
     * @param imagenUri
     * @param callback
     */
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
                            datosJugador.put("fotoPerfil", downloadUrl); // Actualizar la URL de la imagen
                        }
                        // Actualizar datos del jugador
                        FirebaseController.db.collection("usuarios").document(uid)
                                .update(datosJugador)
                                .addOnSuccessListener(unused -> callback.onComplete(true, String.valueOf(R.string.datos_actualizados_correctamente), downloadUrl))
                                .addOnFailureListener(e -> callback.onComplete(false, String.valueOf(R.string.error_al_actualizar_los_datos), null));
                    },
                    e -> callback.onComplete(false, "Error al subir imagen", null)
            );
        } else {
            FirebaseController.db.collection("usuarios").document(uid)
                    .update(datosJugador)
                    .addOnSuccessListener(unused -> callback.onComplete(true, String.valueOf(R.string.datos_actualizados_correctamente), null))
                    .addOnFailureListener(e -> callback.onComplete(false, String.valueOf(R.string.error_al_actualizar_los_datos), null));
        }
    }

    /**
     * Guarda los datos del entrenador en Firestore.
     * @param uid
     * @param nombreUsuario
     * @param titulares
     * @param suplentes
     * @param imagenUri
     * @param callback
     */
    public static void guardarDatosEntrenador(String uid, String nombreUsuario, List<String> titulares, List<String> suplentes, Uri imagenUri, CallbackGuardar callback) {
        FirebaseController.db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String equipoId = documentSnapshot.getString("equipoId");
                    if (equipoId == null || equipoId.isEmpty()) {
                        callback.onComplete(false, String.valueOf(R.string.no_se_encontr_el_id_del_equipo), null);
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
                                            Map<String, Object> datosEquipo = new HashMap<>(); // Crear mapa para los datos del equipo
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
                                                            // Actualizar datos del usuario y equipo
                                                            actualizarUsuarioYEquipo(uid, equipoId, datosUsuario, datosEquipo, callback, downloadUrl);
                                                        },
                                                        e -> callback.onComplete(false, String.valueOf(R.string.error_al_subir_la_imagen), null)
                                                );
                                            } else {
                                                actualizarUsuarioYEquipo(uid, equipoId, datosUsuario, datosEquipo, callback, null);
                                            }
                                        },
                                        e -> callback.onComplete(false, String.valueOf(R.string.error_al_obtener_ids_suplentes), null)
                                );
                            },
                            e -> callback.onComplete(false, String.valueOf(R.string.error_al_obtener_ids_titulares), null)
                    );
                })
                .addOnFailureListener(e -> callback.onComplete(false, String.valueOf(R.string.error_al_obtener_los_datos_del_usuario), null));
    }

    public static void actualizarFotoEscudoEquipo(String equipoId, Uri nuevaImagenUri, CallbackGuardar callback) {
        if (equipoId == null || equipoId.isEmpty()) {
            callback.onComplete(false, String.valueOf(R.string.id_del_equipo_inv_lido), null);
            return;
        }
        if (nuevaImagenUri == null) {
            return;
        }

        String rutaStorage = "equipos/" + equipoId + "/escudo.jpg";

        FirebaseController.subirImagenAFirebaseStorage(
                rutaStorage,
                nuevaImagenUri,
                downloadUrl -> {
                    if (downloadUrl != null) {
                        // Actualizar el campo "escudo" en Firestore
                        FirebaseController.db.collection("equipos").document(equipoId)
                                .update("escudo", downloadUrl)
                                .addOnSuccessListener(aVoid -> {
                                    callback.onComplete(true, "Escudo actualizado correctamente", downloadUrl);
                                })
                                .addOnFailureListener(e -> {
                                    callback.onComplete(false, "Error al actualizar el escudo en Firestore: " + e.getMessage(), null);
                                });
                    } else {
                        callback.onComplete(false, "Error al obtener URL de la imagen", null);
                    }
                },
                e -> callback.onComplete(false, "Error al subir la imagen: " + e.getMessage(), null)
        );
    }


    /**
     * Obtiene los IDs de usuarios por nombres.
     * @param nombres
     * @param onSuccess
     * @param onFailure
     */
    public static void obtenerIdsUsuariosPorNombres(List<String> nombres, OnSuccessListener<List<String>> onSuccess, OnFailureListener onFailure) {
        if (nombres == null || nombres.isEmpty()) {
            onSuccess.onSuccess(new ArrayList<>());
            return;
        }

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

    /**
     * Actualiza los datos del usuario y del equipo en Firestore.
     * @param uid
     * @param equipoId
     * @param datosUsuario
     * @param datosEquipo
     * @param callback
     * @param downloadUrl
     */
    private static void actualizarUsuarioYEquipo(String uid, String equipoId, Map<String, Object> datosUsuario, Map<String, Object> datosEquipo, CallbackGuardar callback, String downloadUrl) {
        FirebaseController.db.collection("usuarios").document(uid)
                .update(datosUsuario)
                .addOnSuccessListener(unused -> {
                    FirebaseController.db.collection("equipos").document(equipoId.split("/")[2])
                            .update(datosEquipo)
                            .addOnSuccessListener(unused2 -> callback.onComplete(true, String.valueOf(R.string.datos_actualizados_correctamente), downloadUrl))
                            .addOnFailureListener(e -> callback.onComplete(false, String.valueOf(R.string.error_al_actualizar_los_datos), null));
                })
                .addOnFailureListener(e -> callback.onComplete(false, String.valueOf(R.string.error_al_actualizar_el_usuario), null));
    }
}
