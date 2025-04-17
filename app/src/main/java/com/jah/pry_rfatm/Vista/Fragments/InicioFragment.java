package com.jah.pry_rfatm.Vista.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.R;

import java.util.ArrayList;
import java.util.List;

public class InicioFragment extends Fragment {

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

        db.collection("jugadores").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        mostrarDialogoEquipo(uid);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al verificar jugador.", Toast.LENGTH_SHORT).show();
                });
    }

    private void mostrarDialogoEquipo(String uid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Primero comprobamos si el jugador ya está registrado
        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        //El jugador ya esta registrado
                        return;
                    }

                    //Si no está registrado, obtenemos los equipos
                    db.collection("equipos").get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<String> listaEquipos = new ArrayList<>();
                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    String nombreEquipo = doc.getString("nombre");
                                    if (nombreEquipo != null) {
                                        listaEquipos.add(nombreEquipo);
                                    }
                                }

                                if (listaEquipos.isEmpty()) {
                                    Toast.makeText(getContext(), "No hay equipos registrados.", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                //Crear el Spinner y mostrar el diálogo
                                LayoutInflater inflater = LayoutInflater.from(requireContext());
                                View dialogView = inflater.inflate(R.layout.dialog_nuevo_usuario, null);

                                //Referencias a los elementos del layout
                                Spinner spinner = dialogView.findViewById(R.id.spEquipos);
                                RadioGroup rgdTipoUsuario = dialogView.findViewById(R.id.rgdTipoUsuario);
                                RadioButton rbdJugador = dialogView.findViewById(R.id.rbdJugador);
                                RadioButton rbdEntrenador = dialogView.findViewById(R.id.rbdEntrenador);

                                //Cargar equipos en el spinner
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                                        android.R.layout.simple_spinner_dropdown_item, listaEquipos);
                                spinner.setAdapter(adapter);

                                //Crear el diálogo
                                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                                builder.setTitle("Registro")
                                        .setCancelable(false)
                                        .setView(dialogView)
                                        .setPositiveButton("Aceptar", (dialog, which) -> {
                                            String equipoSeleccionado = (String) spinner.getSelectedItem();
                                            String tipoUsuario = rbdJugador.isChecked() ? "jugador" : "entrenador";

                                            guardarJugador(uid, equipoSeleccionado, tipoUsuario);
                                        });

                                AlertDialog dialog = builder.create();

                                    //Cambia el color de los botones después de mostrar el diálogo
                                    dialog.setOnShowListener(dialogInterface -> {
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00592d")));
                                        Button btnPositivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        Button btnNegativo = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                                    if (btnPositivo != null) {
                                        btnPositivo.setTextColor(Color.BLACK); // Texto negro
                                    }
                                    if (btnNegativo != null) {
                                        btnNegativo.setTextColor(Color.BLACK); // Texto negro
                                    }
                                });
                                // Muestra el diálogo
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

        // Construimos la referencia al documento del equipo
        DocumentReference equipoRef = FirebaseFirestore.getInstance()
                .collection("equipos")
                .document(equipoId);

        String fotoPerfil = (user != null && user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : "";
        String nombre = (user != null && user.getDisplayName() != null) ? user.getDisplayName() : "Jugador";
        String estilo = "";
        int partidosJugados = 0;
        int victorias = 0;
        int derrotas = 0;
        int porcentajeVictorias = 0;

        Jugador jugador = new Jugador(
                equipoRef,
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
