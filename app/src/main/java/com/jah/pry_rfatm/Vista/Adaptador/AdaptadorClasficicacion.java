package com.jah.pry_rfatm.Vista.Adaptador;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.Modelo.Equipo;
import com.jah.pry_rfatm.Modelo.Grupo;
import com.jah.pry_rfatm.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para mostrar la clasificación de equipos en un RecyclerView.
 * Utiliza información de Firebase Firestore para cargar dinámicamente los equipos
 * y sus estadísticas (victorias, derrotas, puntos) dentro de cada grupo.
 */
public class AdaptadorClasficicacion extends RecyclerView.Adapter<AdaptadorClasficicacion.holderClasificacion> {

    List<Grupo> dataSet;
    Context context;

    /**
     * Constructor del adaptador.
     * @param dataSet Lista de objetos Grupo que contienen los equipos a mostrar.
     */
    public AdaptadorClasficicacion(List<Grupo> dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Crea y devuelve una nueva instancia del ViewHolder.
     * @param parent El ViewGroup padre al que se adjunta el nuevo View.
     * @param viewType Tipo de vista (no utilizado en este caso).
     * @return Un nuevo holderClasificacion.
     */
    @NonNull
    @Override
    public AdaptadorClasficicacion.holderClasificacion onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.grupo_adaptador, parent, false);
        return new holderClasificacion(view);
    }

    /**
     * Enlaza los datos de un Grupo con su ViewHolder correspondiente.
     * @param holder ViewHolder que se está configurando.
     * @param position Posición del ítem en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull AdaptadorClasficicacion.holderClasificacion holder, int position) {
        Grupo grupo = dataSet.get(position);
        holder.lblGrupo.setText(grupo.getNombre());

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        obtenerDatosClasificacion(holder, grupo, db);
    }

    /**
     * Obtiene los datos de los equipos desde Firestore y los muestra ordenados por puntos.
     * @param holder ViewHolder donde se mostrarán los datos.
     * @param grupo Grupo que contiene los IDs de los equipos.
     * @param db Instancia de FirebaseFirestore.
     */
    private static void obtenerDatosClasificacion(@NonNull AdaptadorClasficicacion.holderClasificacion holder, Grupo grupo, FirebaseFirestore db) {
        List<String> idsEquipos = grupo.getEquipos();
        if (idsEquipos == null || idsEquipos.isEmpty()) return;

        List<Pair<Equipo, Integer>> listaEquipos = new ArrayList<>();

        for (String idRef : idsEquipos) {
            String idEquipo = idRef.split("/")[1];

            db.collection("equipos").document(idEquipo).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Equipo equipo = documentSnapshot.toObject(Equipo.class);
                    if (equipo != null) {
                        int puntos = equipo.getVictorias() * 3;
                        listaEquipos.add(new Pair<>(equipo, puntos));

                        if (listaEquipos.size() == idsEquipos.size()) {
                            // Ordenar por puntos descendente
                            listaEquipos.sort((e1, e2) -> Integer.compare(e2.second, e1.second));

                            // Mostrar en los TextViews
                            for (int i = 0; i < listaEquipos.size(); i++) {
                                Equipo eq = listaEquipos.get(i).first;
                                int puntosEq = listaEquipos.get(i).second;

                                switch (i) {
                                    case 0:
                                        holder.lblEq1.setText(eq.getNombre());
                                        holder.lblVic1.setText(String.valueOf(eq.getVictorias()));
                                        holder.lblDer1.setText(String.valueOf(eq.getDerrotas()));
                                        holder.lblPuntos1.setText(String.valueOf(puntosEq));
                                        break;
                                    case 1:
                                        holder.lblEq2.setText(eq.getNombre());
                                        holder.lblVic2.setText(String.valueOf(eq.getVictorias()));
                                        holder.lblDer2.setText(String.valueOf(eq.getDerrotas()));
                                        holder.lblPuntos2.setText(String.valueOf(puntosEq));
                                        break;
                                    case 2:
                                        holder.lblEq3.setText(eq.getNombre());
                                        holder.lblVic3.setText(String.valueOf(eq.getVictorias()));
                                        holder.lblDer3.setText(String.valueOf(eq.getDerrotas()));
                                        holder.lblPuntos3.setText(String.valueOf(puntosEq));
                                        break;
                                    case 3:
                                        holder.lblEq4.setText(eq.getNombre());
                                        holder.lblVic4.setText(String.valueOf(eq.getVictorias()));
                                        holder.lblDer4.setText(String.valueOf(eq.getDerrotas()));
                                        holder.lblPuntos4.setText(String.valueOf(puntosEq));
                                        break;
                                    case 4:
                                        holder.lblEq5.setText(eq.getNombre());
                                        holder.lblVic5.setText(String.valueOf(eq.getVictorias()));
                                        holder.lblDer5.setText(String.valueOf(eq.getDerrotas()));
                                        holder.lblPuntos5.setText(String.valueOf(puntosEq));
                                        break;
                                    case 5:
                                        holder.lblEq6.setText(eq.getNombre());
                                        holder.lblVic6.setText(String.valueOf(eq.getVictorias()));
                                        holder.lblDer6.setText(String.valueOf(eq.getDerrotas()));
                                        holder.lblPuntos6.setText(String.valueOf(puntosEq));
                                        break;
                                    case 6:
                                        holder.lblEq7.setText(eq.getNombre());
                                        holder.lblVic7.setText(String.valueOf(eq.getVictorias()));
                                        holder.lblDer7.setText(String.valueOf(eq.getDerrotas()));
                                        holder.lblPuntos7.setText(String.valueOf(puntosEq));
                                        break;
                                    case 7:
                                        holder.lblEq8.setText(eq.getNombre());
                                        holder.lblVic8.setText(String.valueOf(eq.getVictorias()));
                                        holder.lblDer8.setText(String.valueOf(eq.getDerrotas()));
                                        holder.lblPuntos8.setText(String.valueOf(puntosEq));
                                        break;
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * Retorna el número total de elementos en el dataset.
     * @return Cantidad de grupos.
     */
    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    /**
     * ViewHolder que representa cada tarjeta de grupo con estadísticas de hasta 8 equipos.
     */
    public static class holderClasificacion extends RecyclerView.ViewHolder {

        MaterialCardView cvCardGrupo;
        TextView lblGrupo;
        // Fila 1
        TextView lblEq1, lblVic1, lblDer1, lblPuntos1;
        // Fila 2
        TextView lblEq2, lblVic2, lblDer2, lblPuntos2;
        // Fila 3
        TextView lblEq3, lblVic3, lblDer3, lblPuntos3;
        // Fila 4
        TextView lblEq4, lblVic4, lblDer4, lblPuntos4;
        // Fila 5
        TextView lblEq5, lblVic5, lblDer5, lblPuntos5;
        // Fila 6
        TextView lblEq6, lblVic6, lblDer6, lblPuntos6;
        // Fila 7
        TextView lblEq7, lblVic7, lblDer7, lblPuntos7;
        // Fila 8
        TextView lblEq8, lblVic8, lblDer8, lblPuntos8;

        /**
         * Constructor que inicializa las vistas del item.
         * @param itemView Vista inflada del ítem.
         */
        public holderClasificacion(@NonNull View itemView) {
            super(itemView);

            lblGrupo = itemView.findViewById(R.id.lblGrupo);
            cvCardGrupo = itemView.findViewById(R.id.cvCardGrupo);
            // Fila 1
            lblEq1 = itemView.findViewById(R.id.lblEq1);
            lblVic1 = itemView.findViewById(R.id.lblvic1);
            lblDer1 = itemView.findViewById(R.id.lblDer1);
            lblPuntos1 = itemView.findViewById(R.id.lblPuntos1);
            // Fila 2
            lblEq2 = itemView.findViewById(R.id.lblEq2);
            lblVic2 = itemView.findViewById(R.id.lblVic2);
            lblDer2 = itemView.findViewById(R.id.lblDer2);
            lblPuntos2 = itemView.findViewById(R.id.lblPuntos2);
            // Fila 3
            lblEq3 = itemView.findViewById(R.id.lblEq3);
            lblVic3 = itemView.findViewById(R.id.lblVic3);
            lblDer3 = itemView.findViewById(R.id.lblDer3);
            lblPuntos3 = itemView.findViewById(R.id.lblPuntos3);
            // Fila 4
            lblEq4 = itemView.findViewById(R.id.lblEq4);
            lblVic4 = itemView.findViewById(R.id.lblVic4);
            lblDer4 = itemView.findViewById(R.id.lblDer4);
            lblPuntos4 = itemView.findViewById(R.id.lblPuntos4);
            // Fila 5
            lblEq5 = itemView.findViewById(R.id.lblEq5);
            lblVic5 = itemView.findViewById(R.id.lblVic5);
            lblDer5 = itemView.findViewById(R.id.lblDer5);
            lblPuntos5 = itemView.findViewById(R.id.lblPuntos5);
            // Fila 6
            lblEq6 = itemView.findViewById(R.id.lblEq6);
            lblVic6 = itemView.findViewById(R.id.lblVic6);
            lblDer6 = itemView.findViewById(R.id.lblDer6);
            lblPuntos6 = itemView.findViewById(R.id.lblPuntos6);
            // Fila 7
            lblEq7 = itemView.findViewById(R.id.lblEq7);
            lblVic7 = itemView.findViewById(R.id.lblVic7);
            lblDer7 = itemView.findViewById(R.id.lblDer7);
            lblPuntos7 = itemView.findViewById(R.id.lblPuntos7);
            // Fila 8
            lblEq8 = itemView.findViewById(R.id.lblEq8);
            lblVic8 = itemView.findViewById(R.id.lblVic8);
            lblDer8 = itemView.findViewById(R.id.lblDer8);
            lblPuntos8 = itemView.findViewById(R.id.lblPuntos8);
        }
    }
}
