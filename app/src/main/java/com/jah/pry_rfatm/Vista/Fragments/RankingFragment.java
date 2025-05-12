package com.jah.pry_rfatm.Vista.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Adaptador.AdaptadorRanking;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra el ranking de jugadores.
 * Los jugadores son ordenados por número de victorias descendente.
 * Se obtienen desde Firestore y se muestran en un RecyclerView.
 */
public class RankingFragment extends Fragment {

    private RecyclerView recyclerJugadores;
    private AdaptadorRanking adapter;
    private List<Jugador> listaJugadores = new ArrayList<>();
    private FirebaseFirestore db;
    TextView lblLiga;

    /**
     * Crea e infla la vista del fragmento.
     * Inicializa el RecyclerView y carga los jugadores desde Firebase.
     *
     * @param inflater           El LayoutInflater.
     * @param container          El contenedor padre.
     * @param savedInstanceState Datos guardados del estado anterior.
     * @return La vista creada.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        recyclerJugadores = view.findViewById(R.id.rvRanking);
        recyclerJugadores.setLayoutManager(new LinearLayoutManager(getContext()));
        lblLiga = view.findViewById(R.id.lblLiga);

        nombreLiga(lblLiga);

        db = FirebaseFirestore.getInstance();
        cargarJugadores();

        adapter = new AdaptadorRanking(listaJugadores);
        recyclerJugadores.setAdapter(adapter);

        return view;
    }
    /**
     * Asigna el nombre de la liga a la etiqueta, segun al que pertenezca su equipo
     * @param lblLiga etiqueta donde se mostrara el nombre de la liga
     * */
    public void nombreLiga(TextView lblLiga) {
        FirebaseUser user = FirebaseController.mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();

            FirebaseController.db.collection("usuarios").document(uid).get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            String equipoId = userDoc.getString("equipoId");

                            if (equipoId != null && !equipoId.isEmpty()) {
                                FirebaseController.db.collection("equipos").document(equipoId.split("/")[2]).get()
                                        .addOnSuccessListener(equipoDoc -> {
                                            if (equipoDoc.exists()) {
                                                String ligaId = equipoDoc.getString("ligaId");

                                                if (ligaId != null && !ligaId.isEmpty()) {
                                                    FirebaseController.db.collection("ligas").document(ligaId.split("/")[2]).get()
                                                            .addOnSuccessListener(ligaDoc -> {
                                                                if (ligaDoc.exists()) {
                                                                    String nombre = ligaDoc.getString("nombre");
                                                                    lblLiga.setText(nombre != null ? nombre : "Sin nombre");
                                                                } else {
                                                                    lblLiga.setText(R.string.liga_no_encontrada);
                                                                }
                                                            })
                                                            .addOnFailureListener(e -> lblLiga.setText(R.string.error_al_obtener_liga));
                                                } else {
                                                    Toast.makeText(requireContext(), "Liga no asignada", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                lblLiga.setText(R.string.equipo_no_encontrado);
                                            }
                                        })
                                        .addOnFailureListener(e -> lblLiga.setText(R.string.error_al_obtener_equipo));
                            } else {
                                lblLiga.setText(R.string.equipo_no_asignado);
                            }
                        } else {
                            lblLiga.setText(R.string.usuario_no_encontrado);
                        }
                    })
                    .addOnFailureListener(e -> lblLiga.setText(R.string.error_al_obtener_usuario));
        } else {
            lblLiga.setText(R.string.no_autenticado);
        }
    }

    /**
     * Carga la lista de jugadores desde Firestore ordenados por número de victorias.
     * Los jugadores se muestran en el RecyclerView.
     */
    private void cargarJugadores() {
        db.collection("usuarios")
                .whereEqualTo("tipoUsuario", "jugador")
                .orderBy("victorias", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaJugadores.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Jugador jugador = doc.toObject(Jugador.class);
                        listaJugadores.add(jugador);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Log.e("RankingFragment", "Error cargando jugadores", e)
                );
    }

}
