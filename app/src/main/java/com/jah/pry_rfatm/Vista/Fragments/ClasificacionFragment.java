package com.jah.pry_rfatm.Vista.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Modelo.Grupo;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Adaptador.AdaptadorClasficicacion;

import java.util.ArrayList;
import java.util.List;

public class ClasificacionFragment extends Fragment {

    View view;
    RecyclerView rvClasificacion;
    AdaptadorClasficicacion adaptadorClasficicacion;
    List<Grupo> listaGrupos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FirebaseController.obtenerGrupos(grupos -> {
            listaGrupos.clear();
            listaGrupos.addAll(grupos);
            adaptadorClasficicacion.notifyDataSetChanged();
        }, e -> {
            Log.e("Firebase", "Error al obtener grupos", e);
        });

        view = inflater.inflate(R.layout.fragment_clasificacion, container, false);
        rvClasificacion = view.findViewById(R.id.rvClasificacion);
        rvClasificacion.setLayoutManager(new LinearLayoutManager(getContext()));
        listaGrupos = new ArrayList<>();
        adaptadorClasficicacion = new AdaptadorClasficicacion(listaGrupos);
        rvClasificacion.setAdapter(adaptadorClasficicacion);
        return view;
    }
}