package com.jah.pry_rfatm.Vista.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Activities.EditarPerfilActivity;
import com.jah.pry_rfatm.Vista.Activities.LogInActivity;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

public class PerfilFragment extends Fragment {

    Button btnCerrar;
    MaterialToolbar mtbBarJugador;
    TextView lblNombreJugador, lblTipo, lblEstilo, lblNombreEquipo, lblVictorias, lblDerrotas, lblPorcentaje, lblPartidosJugados;
    ImageView imgJugador, imgEscudo;
    private GoogleSignInClient mGoogleSignInClient;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_perfil, container, false);
        setHasOptionsMenu(true);
        initComponents(view);
        //Para poder mostrar correctamente el menu y se asocie al materialToolBar
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mtbBarJugador);

        btnCerrar = view.findViewById(R.id.btnCerrar);

        btnCerrar.setOnClickListener(v -> {
            cerrarSesion();
        });

        return view;
    }
    //Cierra la sesión de usuario y te devuelve al Login
    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bar_perfil, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_editar) {
            Intent intent = new Intent(requireContext(), EditarPerfilActivity.class);
            //pasar datos a traves de intent.
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents(View view) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        mtbBarJugador = view.findViewById(R.id.mtbBarJugador);
        mtbBarJugador.setBackgroundColor(getResources().getColor(R.color.color_fondos));
        lblNombreJugador = view.findViewById(R.id.lblNombreJugador);
        lblPartidosJugados = view.findViewById(R.id.lblPartidosJugados);
        lblTipo = view.findViewById(R.id.lblTipo);
        lblEstilo = view.findViewById(R.id.lblEstilo);
        lblNombreEquipo = view.findViewById(R.id.lblNombreEquipo);
        lblVictorias = view.findViewById(R.id.lblVictorias);
        lblDerrotas = view.findViewById(R.id.lblDerrotas);
        lblPorcentaje = view.findViewById(R.id.lblPorcentaje);
        imgJugador = view.findViewById(R.id.imgJugador);
        imgEscudo = view.findViewById(R.id.imgEscudo);

        FirebaseController.obtenerDatosJugador(jugador -> {
            lblNombreJugador.setText(jugador.getNombre());
            lblPartidosJugados.setText(String.valueOf(jugador.getPartidosJugados()));
            lblTipo.setText(jugador.getTipoUsuario());
            lblEstilo.setText(jugador.getEstilo());
            lblVictorias.setText(String.valueOf(jugador.getVictorias()));
            lblDerrotas.setText(String.valueOf(jugador.getDerrotas()));
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseController.actualizarPorcentajeVictorias(uid, jugador.getVictorias(), jugador.getPartidosJugados(), lblPorcentaje);

            // Si necesitas cargar la imagen de perfil
            if (jugador.getFotoPerfil() != null && !jugador.getFotoPerfil().isEmpty()) {
                FirebaseController.cargarImagenDesdeStorage(imgJugador, jugador.getFotoPerfil(), FirebaseController.imagenPerfilPorDefecto);
            }

            // Cargar datos del equipo también
            FirebaseController.obtenerEquipoPorId(jugador.getEquipoId().split("/")[2], equipo -> {
                lblNombreEquipo.setText(equipo.getNombre());
                if (equipo.getEscudo() != null && !equipo.getEscudo().isEmpty()) {
                    FirebaseController.cargarImagenDesdeStorage(imgEscudo, equipo.getEscudo(), FirebaseController.imagenPorDefecto);
                }
            }, e -> {
                lblNombreEquipo.setText("Equipo no encontrado");
            });

        }, e -> {
            lblNombreJugador.setText("Error al cargar datos");
        });

    }
}

