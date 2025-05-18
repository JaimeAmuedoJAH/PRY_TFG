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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Logica.InicioLogicHelper;
import com.jah.pry_rfatm.Modelo.Partido;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Adaptador.AdaptadorPartido;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

        FirebaseController.iniciarFirebase(requireContext());
        FirebaseUser user = FirebaseController.mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        FirebaseController.db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    String equipoId = document.getString("equipoId");
                    String tipoUsuario = document.getString("tipoUsuario");

                    if (equipoId == null || equipoId.isEmpty() || tipoUsuario == null || tipoUsuario.isEmpty()) {
                        mostrarDialogoEquipo(uid);
                    } else {
                        cargarPartidos(); // ✅ Ahora sí, si ya está registrado
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), R.string.toast_error_al_verificar_jugador, Toast.LENGTH_SHORT).show();
                });

        rvPartido = view.findViewById(R.id.rvPartido);
        rvPartido.setLayoutManager(new LinearLayoutManager(getContext()));
        listaPartidos = new ArrayList<>();
        adaptadorPartido = new AdaptadorPartido(listaPartidos);
        rvPartido.setAdapter(adaptadorPartido);
    }

    /**
     * Carga los partidos desde Firebase y actualiza el adaptador.
     */
    private void cargarPartidos() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        FirebaseController.obtenerPartidos(uid, partidos -> {
            listaPartidos.clear();
            listaPartidos.addAll(partidos);
            adaptadorPartido.notifyDataSetChanged();
        }, error -> {
            Toast.makeText(getContext(), R.string.toast_error_al_cargar_partidos, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Muestra un diálogo para seleccionar un equipo.
     * @param uid
     */
    private void mostrarDialogoEquipo(String uid) {
        FirebaseController.db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String equipo = documentSnapshot.getString("equipoId");
                        String tipoUsuario = documentSnapshot.getString("tipoUsuario");

                        // Si ya tiene ambos campos, no mostramos el diálogo
                        if (equipo != null && !equipo.isEmpty() && tipoUsuario != null && !tipoUsuario.isEmpty()) {
                            return;
                        }
                    }

                    FirebaseController.db.collection("equipos").get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<String> listaNombresEquipos = new ArrayList<>();
                                List<String> listaIds = new ArrayList<>();

                                for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                    String nombreEquipo = doc.getString("nombre");
                                    if (nombreEquipo != null) {
                                        listaNombresEquipos.add(nombreEquipo);
                                        listaIds.add(doc.getId());
                                    }
                                }

                                if (listaNombresEquipos.isEmpty()) {
                                    Toast.makeText(getContext(), R.string.toast_no_hay_equipos_registrados, Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Map<String, String> nombreToIdMap = InicioLogicHelper.procesarEquipos(listaNombresEquipos, listaIds);

                                LayoutInflater inflater = LayoutInflater.from(requireContext());
                                View dialogView = inflater.inflate(R.layout.dialog_nuevo_usuario, null);
                                Spinner spinner = dialogView.findViewById(R.id.spEquipos);
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
                                            String equipoPath = InicioLogicHelper.construirPathEquipo(equipoDocId);
                                            String tipoUsuario = InicioLogicHelper.obtenerTipoUsuario(
                                                    rbdJugador.isChecked(),
                                                    rbdEntrenador.isChecked()
                                            );
                                            guardarUsuario(uid, equipoPath, tipoUsuario);
                                            cargarPartidos();
                                        });

                                AlertDialog dialog = builder.create();
                                dialog.setOnShowListener(dialogInterface -> {
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));
                                    Button btnPositivo = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                    if (btnPositivo != null) {
                                        btnPositivo.setTextColor(getResources().getColor(R.color.color_letra));
                                    }
                                });
                                dialog.show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), R.string.toast_error_al_cargar_equipos, Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), R.string.toast_error_al_verificar_jugador, Toast.LENGTH_SHORT).show();
                });
    }


    /**
     * Guarda un usuario en Firebase.
     * @param uid
     * @param equipoId
     * @param tipoUsuario
     */
    private void guardarUsuario(String uid, String equipoId, String tipoUsuario) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseController.guardarUsuario(uid, equipoId, tipoUsuario, user,
                unused -> {
                    Toast.makeText(getContext(), R.string.toast_registro_completado, Toast.LENGTH_SHORT).show();
                    cargarPartidos();
                },
                e -> Toast.makeText(getContext(), R.string.toast_error_al_registrar + " " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }

}
