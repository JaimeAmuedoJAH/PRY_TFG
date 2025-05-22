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

/**
 * Adaptador para mostrar la lista de partidos en un RecyclerView.
 * Carga los equipos desde Firebase y permite acceder a la vista detallada del partido.
 */
public class AdaptadorRanking extends RecyclerView.Adapter<AdaptadorRanking.HolderRanking> {

    private List<Jugador> listaJugadores;

    /**
     * Constructor que recibe la lista de jugadores a mostrar.
     * @param listaJugadores Lista de jugadores.
     */
    public AdaptadorRanking(List<Jugador> listaJugadores) {
        this.listaJugadores = listaJugadores;
    }

    /**
     * Crea el ViewHolder para cada fila del ranking.
     * @param parent ViewGroup padre.
     * @param viewType Tipo de vista.
     * @return HolderRanking.
     */
    @NonNull
    @Override
    public AdaptadorRanking.HolderRanking onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_jugador, parent, false);
        return new HolderRanking(vista);
    }

    /**
     * Enlaza un jugador con su fila en el RecyclerView.
     * @param holder ViewHolder.
     * @param position Posición del jugador.
     */
    @Override
    public void onBindViewHolder(@NonNull AdaptadorRanking.HolderRanking holder, int position) {
        holder.bind(listaJugadores.get(position));
    }

    /**
     * Devuelve el número total de jugadores en la lista.
     * @return Cantidad de jugadores.
     */
    @Override
    public int getItemCount() {
        return listaJugadores.size();
    }

    /**
     * ViewHolder para mostrar los datos de un jugador.
     */
    public static class HolderRanking extends RecyclerView.ViewHolder {

        TextView tvNombre, tvVictorias, tvDerrotas, tvPuntos;

        /**
         * Constructor que vincula las vistas.
         * @param itemView Vista inflada del ítem.
         */
        public HolderRanking(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvVictorias = itemView.findViewById(R.id.tvVictorias);
            tvDerrotas = itemView.findViewById(R.id.tvDerrotas);
            tvPuntos = itemView.findViewById(R.id.tvPuntos);
        }

        /**
         * Asigna los datos de un jugador a las vistas del ViewHolder.
         * @param jugador Jugador cuyas estadísticas se mostrarán.
         */
        public void bind(Jugador jugador) {
            tvNombre.setText(jugador.getNombre());
            tvVictorias.setText(String.valueOf(jugador.getVictorias()));
            tvDerrotas.setText(String.valueOf(jugador.getDerrotas()));
            tvPuntos.setText(String.valueOf(jugador.getVictorias() * 3));
        }
    }
}
