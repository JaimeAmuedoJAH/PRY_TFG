package com.jah.pry_rfatm.Controlador;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jah.pry_rfatm.Modelo.Entrenador;
import com.jah.pry_rfatm.Modelo.Equipo;
import com.jah.pry_rfatm.Modelo.Grupo;
import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.Modelo.Partido;
import com.jah.pry_rfatm.Modelo.Usuario;
import com.jah.pry_rfatm.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Controlador centralizado para manejar la interacción con Firebase en la aplicación.
 * Se encarga de la autenticación, Firestore, Storage y App Check.
 */
public class FirebaseController {

    /** Instancia global de FirebaseAuth */
    public static FirebaseAuth mAuth;
    /** Instancia global de FirebaseFirestore */
    public static FirebaseFirestore db;
    /** Instancia global de storage */
    public static FirebaseStorage storage;
    /** Indica si Firebase ya ha sido inicializado */
    private static boolean isInitialized = false;
    /** URL por defecto para el escudo de equipo */
    public static String imagenPorDefecto = "gs://pry-rfatm.firebasestorage.app/escudo_por_defecto.png";
    /** URL por defecto para la foto de perfil */
    public static String imagenPerfilPorDefecto = "gs://pry-rfatm.firebasestorage.app/foto_perfil_defecto.png";


    /**
     * Inicializa Firebase con el contexto dado.
     * También configura App Check con el proveedor de depuración.
     *
     * @param context Contexto de la aplicación
     */
    public static void iniciarFirebase(Context context){
        if (!isInitialized) {
            FirebaseApp.initializeApp(context);

            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
            );


            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            storage = FirebaseStorage.getInstance();
            isInitialized = true;
        }
    }

    /**
     * Obtiene los partidos en los que participa el equipo del usuario actual.
     *
     * @param uid ID del usuario autenticado
     * @param onSuccess Callback en caso de éxito con la lista de partidos
     * @param onFailure Callback en caso de fallo
     */
    public static void obtenerPartidos(String uid, OnSuccessListener<List<Partido>> onSuccess, OnFailureListener onFailure) {
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String equipoPath = userDoc.getString("equipoId");

                        if (equipoPath != null && !equipoPath.isEmpty()) {
                            List<Partido> partidos = new ArrayList<>();

                            db.collection("partidos")
                                    .whereEqualTo("equipoLocalId", equipoPath)
                                    .get()
                                    .addOnSuccessListener(snapshot1 -> {
                                        for (QueryDocumentSnapshot doc : snapshot1) {
                                            partidos.add(doc.toObject(Partido.class));
                                        }

                                        db.collection("partidos")
                                                .whereEqualTo("equipoVisitanteId", equipoPath)
                                                .get()
                                                .addOnSuccessListener(snapshot2 -> {
                                                    for (QueryDocumentSnapshot doc : snapshot2) {
                                                        partidos.add(doc.toObject(Partido.class));
                                                    }
                                                    onSuccess.onSuccess(partidos);
                                                })
                                                .addOnFailureListener(onFailure);
                                    })
                                    .addOnFailureListener(onFailure);
                        } else {
                            onFailure.onFailure(new Exception(String.valueOf(R.string.exception_equipoid_vac_o_o_nulo)));
                        }
                    } else {
                        onFailure.onFailure(new Exception(String.valueOf(R.string.exception_documento_de_usuario_no_existe)));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Guarda la información de un jugador o entrenador en Firestore.
     * También actualiza las referencias del equipo correspondiente.
     *
     * @param uid ID del usuario
     * @param equipoId Ruta completa del equipo (e.g., "equipos/123")
     * @param tipoUsuario Tipo de usuario: "jugador" o "entrenador"
     * @param user Usuario autenticado de Firebase
     * @param onSuccess Callback en caso de éxito
     * @param onFailure Callback en caso de fallo
     */
    public static void guardarUsuario(String uid, String equipoId, String tipoUsuario,
                                      FirebaseUser user,
                                      OnSuccessListener<Void> onSuccess,
                                      OnFailureListener onFailure) {

        String fotoPerfil = (user != null && user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "";
        String nombre = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "Jugador";

        if (tipoUsuario.equals("jugador")) {
            Jugador jugador = new Jugador(
                    equipoId,
                    fotoPerfil,
                    nombre,
                    tipoUsuario,
                    "", // estilo
                    0, // partidos
                    0, // victorias
                    0, // derrotas
                    0  // porcentaje
            );

            db.collection("usuarios").document(uid).set(jugador)
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure);

            // Añadir a suplentes
            DocumentReference equipoRef = db.collection("equipos").document(equipoId.split("/")[2]);
            equipoRef.update("suplentes", FieldValue.arrayUnion(uid))
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", String.valueOf((R.string.jugador_a_adido_como_suplente))))
                    .addOnFailureListener(e -> Log.w("Firestore", String.valueOf(R.string.error_al_a_adir_suplente), e));

        } else if (tipoUsuario.equals("entrenador")) {
            Entrenador entrenador = new Entrenador(
                    equipoId,
                    fotoPerfil,
                    nombre,
                    tipoUsuario
            );

            db.collection("usuarios").document(uid).set(entrenador)
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener(onFailure);

            // Añadir como entrenador del equipo
            DocumentReference equipoRef = db.collection("equipos").document(equipoId.split("/")[2]);
            equipoRef.update("entrenadorId", FieldValue.arrayUnion(uid))
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", String.valueOf(R.string.entrenador_a_adido)))
                    .addOnFailureListener(e -> Log.w("Firestore", String.valueOf(R.string.error_al_a_adir_entrenador), e));

        }
    }

    /**
     * Obtiene los datos de un equipo dado su ID.
     *
     * @param equipoId ID del equipo
     * @param onSuccess Callback en caso de éxito con el objeto Equipo
     * @param onFailure Callback en caso de fallo
     */
    public static void obtenerEquipoPorId(String equipoId,
                                          OnSuccessListener<Equipo> onSuccess,
                                          OnFailureListener onFailure) {

        db.collection("equipos").document(equipoId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Equipo equipo = documentSnapshot.toObject(Equipo.class);
                        onSuccess.onSuccess(equipo);
                    }
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Carga una imagen en un ImageView desde Firebase Storage.
     * Si falla, carga una imagen por defecto.
     *
     * @param imageView ImageView donde se mostrará la imagen
     * @param url URL de la imagen en Storage
     * @param urlEscudoDefecto URL de la imagen por defecto
     */
    public static void cargarImagenDesdeStorage(ImageView imageView, String url, String urlEscudoDefecto) {
        StorageReference defaultRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlEscudoDefecto);

        if (url != null && !url.isEmpty()) {
            if (url.startsWith("gs://")) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                ref.getDownloadUrl().addOnSuccessListener(uri ->
                        Glide.with(imageView.getContext()).load(uri.toString()).into(imageView)
                ).addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", String.valueOf(R.string.fallo_al_cargar_imagen_gs_usando_por_defecto), e);
                    defaultRef.getDownloadUrl().addOnSuccessListener(uri ->
                            Glide.with(imageView.getContext()).load(uri.toString()).into(imageView)
                    ).addOnFailureListener(ex -> {
                        Log.e("FirebaseStorage", String.valueOf(R.string.fallo_al_cargar_imagen_por_defecto), ex);
                    });
                });
            } else {
                // Si no es gs://, asumir que es URL pública directa
                Glide.with(imageView.getContext()).load(url).into(imageView);
            }
        } else {
            defaultRef.getDownloadUrl().addOnSuccessListener(uri ->
                    Glide.with(imageView.getContext()).load(uri.toString()).into(imageView)
            ).addOnFailureListener(e -> {
                Log.e("FirebaseStorage", String.valueOf(R.string.fallo_al_cargar_imagen_por_defecto), e);
            });
        }
    }


    /**
     * Obtiene los datos del usuario actual.
     *
     * @param onSuccess Callback en caso de éxito con el objeto Jugador
     * @param onFailure Callback en caso de fallo
     */
    public static void obtenerDatosUsuario(OnSuccessListener<Usuario> onSuccess, OnFailureListener onFailure) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String tipoUsuario = documentSnapshot.getString("tipoUsuario");

                            if ("jugador".equalsIgnoreCase(tipoUsuario)) {
                                Jugador jugador = documentSnapshot.toObject(Jugador.class);
                                onSuccess.onSuccess(jugador);
                            } else if ("entrenador".equalsIgnoreCase(tipoUsuario)) {
                                Entrenador entrenador = documentSnapshot.toObject(Entrenador.class);
                                onSuccess.onSuccess(entrenador);
                            } else {
                                onFailure.onFailure(new Exception(String.valueOf(R.string.tipo_de_usuario_no_reconocido)));
                            }
                        } else {
                            onFailure.onFailure(new Exception(String.valueOf(R.string.usuario_no_encontrado_en_firestore)));
                        }
                    })
                    .addOnFailureListener(onFailure);
        } else {
            onFailure.onFailure(new Exception(String.valueOf(R.string.usuario_no_autenticado)));
        }
    }
    /**
     * Recupera todos los grupos disponibles desde Firestore.
     *
     * @param onSuccess Callback con lista de grupos
     * @param onFailure Callback en caso de error
     */
    public static void obtenerGrupos(OnSuccessListener<List<Grupo>> onSuccess, OnFailureListener onFailure) {
        db.collection("grupos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Grupo> listaGrupos = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Grupo grupo = document.toObject(Grupo.class);
                        listaGrupos.add(grupo);
                    }
                    onSuccess.onSuccess(listaGrupos);
                })
                .addOnFailureListener(onFailure);
    }

    /**
     * Envía un correo para recuperación de contraseña al usuario.
     *
     * @param email Correo electrónico del usuario
     * @param onSuccess Callback si el envío fue exitoso
     * @param onFailure Callback si ocurrió un error
     */
    public static void enviarCorreoRecuperacion(String email,
                                                OnSuccessListener<Void> onSuccess,
                                                OnFailureListener onFailure) {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

    /**
     * Subir una imagen a Firebase Storage.
     * @param pathEnStorage
     * @param uriImagen
     * @param onSuccess
     * @param onFailure
     */
    public static void subirImagenAFirebaseStorage(
            String pathEnStorage,
            Uri uriImagen,
            OnSuccessListener<String> onSuccess,
            OnFailureListener onFailure
    ) {
        StorageReference storageRef = storage.getReference().child(pathEnStorage);

        storageRef.putFile(uriImagen)
                .addOnSuccessListener(taskSnapshot ->
                        storageRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> onSuccess.onSuccess(uri.toString()))
                                .addOnFailureListener(onFailure)
                )
                .addOnFailureListener(onFailure);
    }


    /**
     * Formatea una fecha en el formato "dd/MM/yyyy HH:mm".
     *
     * @param fecha Objeto Date
     * @return Fecha formateada como string
     */
    public static String formatearFecha(Date fecha) {
        if (fecha == null) return "Sin fecha";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(fecha);
    }

    /**
     * Calcula y actualiza el porcentaje de victorias de un jugador.
     * También actualiza el TextView con el nuevo valor.
     *
     * @param uid ID del usuario
     * @param victorias Número de victorias
     * @param partidosJugados Número total de partidos jugados
     * @param lblPorcentaje TextView donde se muestra el porcentaje
     */
    public static void actualizarPorcentajeVictorias(String uid, int victorias, int partidosJugados, TextView lblPorcentaje) {
        int porcentaje;
        if(partidosJugados > 0){
            porcentaje = (victorias/partidosJugados) * 100;
        }else{
            porcentaje = 0;
        }

        Map<String, Object> actualizacion = new HashMap<>();
        actualizacion.put("porcentajeVictorias", porcentaje);

        db.collection("usuarios").document(uid)
                .update(actualizacion)
                .addOnSuccessListener(aVoid -> Log.d("Firebase", String.valueOf(R.string.porcentaje_actualizado)))
                .addOnFailureListener(e -> Log.e("Firebase", String.valueOf(R.string.error_al_actualizar_porcentaje), e));
        lblPorcentaje.setText(String.valueOf(porcentaje + "%"));
    }
}