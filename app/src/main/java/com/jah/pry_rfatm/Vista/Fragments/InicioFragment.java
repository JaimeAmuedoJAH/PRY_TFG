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

import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.jah.pry_rfatm.Controlador.FirebaseController;
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
        FirebaseController.iniciarFirebase(requireContext());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        FirebaseController.db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(document -> {
                    if (!document.exists()) {
                        mostrarDialogoEquipo(uid);
                    }else{
                        cargarPartidos();
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
    }

    private void cargarPartidos() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseController.obtenerPartidosPorUsuario(uid, partidos -> {
            listaPartidos.clear();
            listaPartidos.addAll(partidos);
            adaptadorPartido.notifyDataSetChanged();

        }, error -> {
            Toast.makeText(getContext(), "Error al cargar partidos.", Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarDialogoEquipo(String uid) {
        FirebaseController.db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) return;

                    FirebaseController.db.collection("equipos").get()
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
                                            String tipoUsuario = "";
                                            if(rbdJugador.isChecked()){
                                                tipoUsuario = "jugador";
                                            }else if(rbdEntrenador.isChecked()){
                                                tipoUsuario = "entrenador";
                                            }
                                            Log.i("entrenador", tipoUsuario);
                                            guardarJugador(uid, equipoPath, tipoUsuario);
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
                                Toast.makeText(getContext(), "Error al cargar equipos.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al verificar jugador.", Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarJugador(String uid, String equipoId, String tipoUsuario) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseController.guardarJugador(uid, equipoId, tipoUsuario, user,
                unused -> Toast.makeText(getContext(), "Registro completado.", Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(getContext(), "Error al registrar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
