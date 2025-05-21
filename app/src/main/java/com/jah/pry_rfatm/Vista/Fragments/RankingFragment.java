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
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Adaptador.AdaptadorRanking;
import com.jah.pry_rfatm.Logica.RankingLogic;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragmento que muestra el ranking de jugadores.
 */
public class RankingFragment extends Fragment {

    private RecyclerView recyclerJugadores;
    private AdaptadorRanking adapter;
    private List<Jugador> listaJugadores = new ArrayList<>();
    private TextView lblLiga;
    private RankingLogic rankingLogic;

    /**
     * Carga el fragment
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);

        recyclerJugadores = view.findViewById(R.id.rvRanking);
        recyclerJugadores.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdaptadorRanking(listaJugadores);
        recyclerJugadores.setAdapter(adapter);

        lblLiga = view.findViewById(R.id.lblLiga);

        rankingLogic = new RankingLogic(FirebaseController.db);

        cargarJugadores();
        cargarNombreLiga();

        return view;
    }

    /**
     * Carga la lista de jugadores desde Firebase y actualiza el adaptador
     */
    private void cargarJugadores() {
        rankingLogic.obtenerJugadoresOrdenados(new RankingLogic.JugadoresCallback() {
            @Override
            public void onJugadoresCargados(List<Jugador> jugadores) {
                listaJugadores.clear();
                listaJugadores.addAll(jugadores);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Log.e("RankingFragment", getString(R.string.error_cargando_jugadores), e);
            }
        });
    }

    /**
     * Carga el nombre de la liga del usuario actual
     */
    private void cargarNombreLiga() {
        FirebaseUser user = FirebaseController.mAuth.getCurrentUser();
        if (user != null) {
            rankingLogic.obtenerNombreLiga(user.getUid(), new RankingLogic.NombreLigaCallback() {
                @Override
                public void onNombreObtenido(String nombre) {
                    lblLiga.setText(nombre);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getContext(), R.string.error_al_obtener_liga, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
