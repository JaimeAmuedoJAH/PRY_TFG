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
import com.jah.pry_rfatm.Modelo.Partido;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Adaptador.AdaptadorPartido;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragmento que se muestra al inicio de la aplicación.
 * Verifica si el usuario está registrado y asociado a un equipo.
 * Si no está registrado, muestra un diálogo para seleccionar equipo y tipo de usuario.
 * Muestra una lista de partidos asociados al usuario autenticado.
 */
public class InicioFragment extends Fragment {

    RecyclerView rvPartido;
    private AdaptadorPartido adaptadorPartido;
    private List<Partido> listaPartidos;

    /**
     * Infla el layout del fragmento.
     *
     * @param inflater           El LayoutInflater.
     * @param container          El contenedor padre.
     * @param savedInstanceState Datos guardados del estado anterior.
     * @return La vista inflada.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }


    /**
     * Método llamado cuando la vista ha sido creada.
     * Inicializa la interfaz de usuario y verifica el registro del usuario.
     *
     * @param view               La vista raíz.
     * @param savedInstanceState Datos guardados del estado anterior.
     */
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
                    Toast.makeText(getContext(), R.string.toast_error_al_verificar_jugador, Toast.LENGTH_SHORT).show();
                });

        rvPartido = view.findViewById(R.id.rvPartido);
        rvPartido.setLayoutManager(new LinearLayoutManager(getContext()));

        listaPartidos = new ArrayList<>();
        adaptadorPartido = new AdaptadorPartido(listaPartidos);
        rvPartido.setAdapter(adaptadorPartido);
    }

    /**
     * Carga los partidos asociados al usuario autenticado desde Firebase.
     */
    private void cargarPartidos() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseController.obtenerPartidos(uid, partidos -> {
            listaPartidos.clear();
            listaPartidos.addAll(partidos);
            adaptadorPartido.notifyDataSetChanged();

        }, error -> {
            Toast.makeText(getContext(), R.string.toast_error_al_cargar_partidos, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Muestra un diálogo que permite al usuario seleccionar equipo y tipo de usuario
     * si aún no está registrado.
     *
     * @param uid UID del usuario autenticado.
     */
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
                                    Toast.makeText(getContext(), R.string.toast_no_hay_equipos_registrados, Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), R.string.toast_error_al_cargar_equipos, Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), R.string.toast_error_al_verificar_jugador, Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Guarda los datos del nuevo jugador o entrenador en Firebase.
     *
     * @param uid         UID del usuario.
     * @param equipoId    ID del equipo seleccionado.
     * @param tipoUsuario Tipo de usuario ("jugador" o "entrenador").
     */
    private void guardarJugador(String uid, String equipoId, String tipoUsuario) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseController.guardarJugador(uid, equipoId, tipoUsuario, user,
                unused -> Toast.makeText(getContext(), R.string.toast_registro_completado, Toast.LENGTH_SHORT).show(),
                e -> Toast.makeText(getContext(), R.string.toast_error_al_registrar + " " + e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }
}
