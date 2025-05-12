package com.jah.pry_rfatm.Vista.Adaptador;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Modelo.Partido;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Activities.VerPartidoActivity;

import java.util.List;

/**
 * Adaptador para mostrar la lista de partidos en un RecyclerView.
 * Carga los equipos desde Firebase y permite acceder a la vista detallada del partido.
 */
public class AdaptadorPartido extends RecyclerView.Adapter<AdaptadorPartido.HolderPartido> {

    private List<Partido> listaPartidos;
    private Context context;

    /**
     * Constructor del adaptador.
     * @param listaPartidos Lista de objetos Partido a mostrar.
     */
    public AdaptadorPartido(List<Partido> listaPartidos) {
        this.listaPartidos = listaPartidos;
    }

    /**
     * Crea y retorna un nuevo ViewHolder para los partidos.
     * @param parent Contenedor padre.
     * @param viewType Tipo de vista.
     * @return HolderPartido.
     */
    @NonNull
    @Override
    public HolderPartido onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.partido_adaptador, parent, false);
        return new HolderPartido(view);
    }

    /**
     * Enlaza los datos de un partido con su ViewHolder.
     * @param holder ViewHolder que contiene las vistas.
     * @param position Posición del partido.
     */
    @Override
    public void onBindViewHolder(@NonNull HolderPartido holder, int position) {
        Partido partido = listaPartidos.get(position);
        FirebaseController.iniciarFirebase(context);

        String idLocal = partido.getEquipoLocalId().substring(partido.getEquipoLocalId().lastIndexOf("/") + 1);
        String idVisitante = partido.getEquipoVisitanteId().substring(partido.getEquipoVisitanteId().lastIndexOf("/") + 1);

        // Cargar equipo local
        FirebaseController.obtenerEquipoPorId(idLocal, equipoLocal -> {
            holder.lblEquipo1.setText(equipoLocal.getNombre());
            FirebaseController.cargarImagenDesdeStorage(holder.imgEquipo1, equipoLocal.getEscudo(), FirebaseController.imagenPorDefecto);
        }, e -> {});

        // Cargar equipo visitante
        FirebaseController.obtenerEquipoPorId(idVisitante, equipoVisitante -> {
            holder.lblEquipo2.setText(equipoVisitante.getNombre());
            FirebaseController.cargarImagenDesdeStorage(holder.imgEquipo2, equipoVisitante.getEscudo(), FirebaseController.imagenPorDefecto);
        }, e -> {});

        // Fecha
        holder.lblHora.setText(FirebaseController.formatearFecha(partido.getFecha()));

        // Evento al tocar el partido
        holder.cvCardPartido.setOnClickListener(v -> {
            Intent intent = new Intent(context, VerPartidoActivity.class);
            intent.putExtra("nombreLocal", holder.lblEquipo1.getText().toString());
            intent.putExtra("nombreVisitante", holder.lblEquipo2.getText().toString());
            intent.putExtra("idLocal", partido.getEquipoLocalId().split("/")[2]);
            intent.putExtra("idVisitante", partido.getEquipoVisitanteId().split("/")[2]);
            intent.putExtra("fechaHora", holder.lblHora.getText().toString());
            intent.putExtra("partido", partido);
            context.startActivity(intent);
        });

    }

    /**
     * Devuelve el número total de partidos.
     * @return Tamaño de la lista de partidos.
     */
    @Override
    public int getItemCount() {
        return listaPartidos.size();
    }

    /**
     * ViewHolder para representar un partido en la interfaz.
     */
    public static class HolderPartido extends RecyclerView.ViewHolder {
        TextView lblEquipo1, lblEquipo2, lblHora;
        ImageView imgEquipo1, imgEquipo2;
        MaterialCardView cvCardPartido;

        /**
         * Constructor que enlaza las vistas del item.
         * @param itemView Vista inflada del ítem.
         */
        public HolderPartido(@NonNull View itemView) {
            super(itemView);
            lblEquipo1 = itemView.findViewById(R.id.lblEquipo1);
            lblEquipo2 = itemView.findViewById(R.id.lblEquipo2);
            lblHora = itemView.findViewById(R.id.lblHora);
            imgEquipo1 = itemView.findViewById(R.id.imgEquipo1);
            imgEquipo2 = itemView.findViewById(R.id.imgEquipo2);
            cvCardPartido = itemView.findViewById(R.id.cvCardPartido);
        }
    }
}
