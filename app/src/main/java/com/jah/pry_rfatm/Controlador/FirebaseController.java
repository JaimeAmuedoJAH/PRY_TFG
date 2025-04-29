package com.jah.pry_rfatm.Controlador;

import android.content.Context;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jah.pry_rfatm.Modelo.Equipo;
import com.jah.pry_rfatm.Modelo.Grupo;
import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.Modelo.Partido;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FirebaseController {


    public static FirebaseAuth mAuth;
    public static FirebaseFirestore db;
    private static boolean isInitialized = false;
    public static String imagenPorDefecto = "gs://pry-rfatm.firebasestorage.app/escudo_por_defecto.png";
    public static String imagenPerfilPorDefecto = "gs://pry-rfatm.firebasestorage.app/foto_perfil_defecto.png";

    public static void iniciarFirebase(Context context){
        if (!isInitialized) {
            FirebaseApp.initializeApp(context);

            FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
            firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
            );


            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            isInitialized = true;
        }
    }

    public static void obtenerPartidosPorUsuario(String uid, OnSuccessListener<List<Partido>> onSuccess, OnFailureListener onFailure) {
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
                            onFailure.onFailure(new Exception("EquipoId vacío o nulo"));
                        }
                    } else {
                        onFailure.onFailure(new Exception("Documento de usuario no existe"));
                    }
                })
                .addOnFailureListener(onFailure);
    }

    public static void guardarJugador(String uid, String equipoId, String tipoUsuario,
                                      FirebaseUser user,
                                      OnSuccessListener<Void> onSuccess,
                                      OnFailureListener onFailure) {

        String fotoPerfil = (user != null && user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "";
        String nombre = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "Jugador";
        String estilo = "";
        int partidosJugados = 0;
        int victorias = 0;
        int derrotas = 0;
        int porcentajeVictorias = 0;

        Jugador jugador = new Jugador(
                equipoId,
                fotoPerfil,
                nombre,
                tipoUsuario,
                estilo,
                partidosJugados,
                victorias,
                derrotas,
                porcentajeVictorias
        );

        db.collection("usuarios").document(uid).set(jugador)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }

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

    public static void cargarImagenDesdeStorage(ImageView imageView, String url, String urlEscudoDefecto) {
        StorageReference defaultRef = FirebaseStorage.getInstance().getReferenceFromUrl(urlEscudoDefecto);

        if (url != null && !url.isEmpty()) {
            if (url.startsWith("gs://")) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                ref.getDownloadUrl().addOnSuccessListener(uri ->
                        Glide.with(imageView).load(uri.toString()).into(imageView)
                ).addOnFailureListener(e ->
                        defaultRef.getDownloadUrl().addOnSuccessListener(uri ->
                                Glide.with(imageView).load(uri.toString()).into(imageView)
                        )
                );
            } else {
                Glide.with(imageView).load(url).into(imageView);
            }
        } else {
            defaultRef.getDownloadUrl().addOnSuccessListener(uri ->
                    Glide.with(imageView).load(uri.toString()).into(imageView)
            );
        }
    }

    public static void obtenerDatosJugador(OnSuccessListener<Jugador> onSuccess, OnFailureListener onFailure) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Jugador jugador = documentSnapshot.toObject(Jugador.class);
                            onSuccess.onSuccess(jugador);
                        } else {
                            onFailure.onFailure(new Exception("Jugador no encontrado."));
                        }
                    })
                    .addOnFailureListener(onFailure);
        } else {
            onFailure.onFailure(new Exception("Usuario no autenticado."));
        }
    }

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

    public static String formatearFecha(Date fecha) {
        if (fecha == null) return "Sin fecha";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(fecha);
    }

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
                .addOnSuccessListener(aVoid -> Log.d("Firebase", "Porcentaje actualizado"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error al actualizar porcentaje", e));
        lblPorcentaje.setText(String.valueOf(porcentaje + "%"));
    }
}
