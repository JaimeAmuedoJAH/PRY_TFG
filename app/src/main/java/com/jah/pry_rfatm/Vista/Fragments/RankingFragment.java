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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Adaptador.AdaptadorRanking;

import java.util.ArrayList;
import java.util.List;

public class RankingFragment extends Fragment {

    private RecyclerView recyclerJugadores;
    private AdaptadorRanking adapter;
    private List<Jugador> listaJugadores = new ArrayList<>();
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        recyclerJugadores = view.findViewById(R.id.rvRanking);
        recyclerJugadores.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        cargarJugadores();

        adapter = new AdaptadorRanking(listaJugadores);
        recyclerJugadores.setAdapter(adapter);

        return view;
    }

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
