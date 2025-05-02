package com.jah.pry_rfatm.Vista.Adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.R;

import java.util.List;

public class AdaptadorRanking extends RecyclerView.Adapter<AdaptadorRanking.HolderRanking> {

    private List<Jugador> listaJugadores;

    public AdaptadorRanking(List<Jugador> listaJugadores) {
        this.listaJugadores = listaJugadores;
    }

    @NonNull
    @Override
    public AdaptadorRanking.HolderRanking onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_jugador, parent, false);
        return new HolderRanking(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorRanking.HolderRanking holder, int position) {
        holder.bind(listaJugadores.get(position));
    }

    @Override
    public int getItemCount() {
        return listaJugadores.size();
    }

    public static class HolderRanking extends RecyclerView.ViewHolder {

        TextView tvNombre, tvVictorias, tvDerrotas, tvPorcentaje;

        public HolderRanking(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvVictorias = itemView.findViewById(R.id.tvVictorias);
            tvDerrotas = itemView.findViewById(R.id.tvDerrotas);
            tvPorcentaje = itemView.findViewById(R.id.tvPorcentaje);
        }

        public void bind(Jugador jugador) {
            tvNombre.setText(jugador.getNombre());
            tvVictorias.setText(String.valueOf(jugador.getVictorias()));
            tvDerrotas.setText(String.valueOf(jugador.getDerrotas()));
            tvPorcentaje.setText(String.valueOf(jugador.getPorcentajeVictorias()));
        }
    }
}
