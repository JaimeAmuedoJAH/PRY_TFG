package com.jah.pry_rfatm.Vista.Fragments;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.Modelo.Partido;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Adaptador.AdaptadorPartido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InicioFragment extends Fragment {

    RecyclerView rvPartido;
    private AdaptadorPartido adaptadorPartido;
    private List<Partido> listaPartidos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        mostrarDialogoEquipo(uid);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al verificar jugador.", Toast.LENGTH_SHORT).show();
                });

        rvPartido = view.findViewById(R.id.rvPartido);
        rvPartido.setLayoutManager(new LinearLayoutManager(getContext()));

        listaPartidos = new ArrayList<>();
        adaptadorPartido = new AdaptadorPartido(listaPartidos);
        rvPartido.setAdapter(adaptadorPartido);

        cargarPartidos();
    }

    private void cargarPartidos() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(uid).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String equipoPath = userDoc.getString("equipoId"); // Ej: "/equipos/equipo001"
                Log.d("Firestore", "Raw equipoId: " + equipoPath);

                if (equipoPath != null && !equipoPath.isEmpty()) {
                    // Buscar partidos como local
                    db.collection("partidos")
                            .whereEqualTo("equipoLocalId", equipoPath)
                            .get()
                            .addOnSuccessListener(snapshot1 -> {
                                // Buscar partidos como visitante
                                db.collection("partidos")
                                        .whereEqualTo("equipoVisitanteId", equipoPath)
                                        .get()
                                        .addOnSuccessListener(snapshot2 -> {
                                            listaPartidos.clear();

                                            for (QueryDocumentSnapshot doc : snapshot1) {
                                                listaPartidos.add(doc.toObject(Partido.class));
                                            }
                                            for (QueryDocumentSnapshot doc : snapshot2) {
                                                listaPartidos.add(doc.toObject(Partido.class));
                                            }

                                            Log.d("Firestore", "Partidos totales cargados: " + listaPartidos.size());
                                            adaptadorPartido.notifyDataSetChanged();
                                        })
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error cargando partidos visitante: ", e));
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error cargando partidos local: ", e));
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Error obteniendo usuario: ", e);
        });
    }


    private void mostrarDialogoEquipo(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) return;

                    db.collection("equipos").get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<String> listaNombresEquipos = new ArrayList<>();
                                Map<String, String> nombreToIdMap = new HashMap<>();

                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    String nombreEquipo = doc.getString("nombre");
                                    if (nombreEquipo != null) {
                                        listaNombresEquipos.add(nombreEquipo);
                                        nombreToIdMap.put(nombreEquipo, doc.getId());
                                    }
                                }

                                if (listaNombresEquipos.isEmpty()) {
                                    Toast.makeText(getContext(), "No hay equipos registrados.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                LayoutInflater inflater = LayoutInflater.from(requireContext());
                                View dialogView = inflater.inflate(R.layout.dialog_nuevo_usuario, null);
                                Spinner spinner = dialogView.findViewById(R.id.spEquipos);
                                RadioGroup rgdTipoUsuario = dialogView.findViewById(R.id.rgdTipoUsuario);
                                RadioButton rbdJugador = dialogView.findViewById(R.id.rbdJugador);
                                RadioButton rbdEntrenador = dialogView.findViewById(R.id.rbdEntrenador);

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item, listaNombresEquipos);
                                spinner.setAdapter(adapter);

                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setTitle("Registro")
                                        .setCancelable(false)
                                        .setView(dialogView)
                                        .setPositiveButton("Aceptar", (dialog, which) -> {
                                            String equipoSeleccionadoNombre = (String) spinner.getSelectedItem();
                                            String equipoDocId = nombreToIdMap.get(equipoSeleccionadoNombre);
                                            String equipoPath = "/equipos/" + equipoDocId;

                                            String tipoUsuario = rbdJugador.isChecked() ? "jugador" : "entrenador";
                                            guardarJugador(uid, equipoPath, tipoUsuario);
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.setOnShowListener(dialogInterface -> {
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
                                    Button btnPositivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    Button btnNegativo = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                                    if (btnPositivo != null) {
                                        btnPositivo.setTextColor(getResources().getColor(R.color.color_letra));
                                    }
                                    if (btnNegativo != null) {
                                        btnNegativo.setTextColor(getResources().getColor(R.color.color_letra));
                                    }
                                });
                                dialog.show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error al cargar equipos.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al verificar jugador.", Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarJugador(String uid, String equipoId, String tipoUsuario) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uid)
                .set(jugador)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Jugador registrado correctamente.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al registrar jugador: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
