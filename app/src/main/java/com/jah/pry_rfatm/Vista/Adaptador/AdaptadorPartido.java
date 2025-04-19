package com.jah.pry_rfatm.Vista.Adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jah.pry_rfatm.Modelo.Equipo;
import com.jah.pry_rfatm.Modelo.Partido;
import com.jah.pry_rfatm.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorPartido extends RecyclerView.Adapter<AdaptadorPartido.HolderPartido> {

    private List<Partido> listaPartidos;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    public AdaptadorPartido(List<Partido> listaPartidos) {
        this.listaPartidos = listaPartidos;
        this.db = FirebaseFirestore.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public HolderPartido onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.partido_adaptador, parent, false);
        return new HolderPartido(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderPartido holder, int position) {
        Partido partido = listaPartidos.get(position);

        String idLocal = partido.getEquipoLocalId().substring(partido.getEquipoLocalId().lastIndexOf("/") + 1);
        String idVisitante = partido.getEquipoVisitanteId().substring(partido.getEquipoVisitanteId().lastIndexOf("/") + 1);

        // Escudo por defecto
        StorageReference escudoPorDefecto = storage.getReferenceFromUrl("gs://pry-rfatm.firebasestorage.app/escudo_por_defecto.png");

        // Cargar equipo local
        db.collection("equipos").document(idLocal).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Equipo equipoLocal = documentSnapshot.toObject(Equipo.class);
                holder.lblEquipo1.setText(equipoLocal.getNombre());

                cargarImagenEscudo(holder.imgEquipo1, equipoLocal.getEscudo(), escudoPorDefecto, holder);
            }
        });

        // Cargar equipo visitante
        db.collection("equipos").document(idVisitante).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Equipo equipoVisitante = documentSnapshot.toObject(Equipo.class);
                holder.lblEquipo2.setText(equipoVisitante.getNombre());

                cargarImagenEscudo(holder.imgEquipo2, equipoVisitante.getEscudo(), escudoPorDefecto, holder);
            }
        });

        // Formatear fecha
        if (partido.getFecha() != null) {
            Date fecha = partido.getFecha();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.lblHora.setText(sdf.format(fecha));
        } else {
            holder.lblHora.setText("Sin fecha");
        }
    }

    private void cargarImagenEscudo(ImageView imageView, String url, StorageReference escudoPorDefecto, RecyclerView.ViewHolder holder) {
        if (url != null && !url.isEmpty()) {
            if (url.startsWith("gs://")) {
                StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(holder.itemView.getContext())
                            .load(uri.toString())
                            .into(imageView);
                }).addOnFailureListener(e -> {
                    escudoPorDefecto.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(holder.itemView.getContext())
                                .load(uri.toString())
                                .into(imageView);
                    });
                });
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .into(imageView);
            }
        } else {
            escudoPorDefecto.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(holder.itemView.getContext())
                        .load(uri.toString())
                        .into(imageView);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaPartidos.size();
    }

    public static class HolderPartido extends RecyclerView.ViewHolder {
        TextView lblEquipo1, lblEquipo2, lblHora;
        ImageView imgEquipo1, imgEquipo2;

        public HolderPartido(@NonNull View itemView) {
            super(itemView);
            lblEquipo1 = itemView.findViewById(R.id.lblEquipo1);
            lblEquipo2 = itemView.findViewById(R.id.lblEquipo2);
            lblHora = itemView.findViewById(R.id.lblHora);
            imgEquipo1 = itemView.findViewById(R.id.imgEquipo1);
            imgEquipo2 = itemView.findViewById(R.id.imgEquipo2);
        }
    }
}
