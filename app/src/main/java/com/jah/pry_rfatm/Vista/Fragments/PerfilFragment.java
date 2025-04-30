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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Activities.EditarPerfilActivity;
import com.jah.pry_rfatm.Vista.Activities.LogInActivity;

public class PerfilFragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Mostrar layout temporal mientras se determina el tipo de usuario
        rootView = inflater.inflate(R.layout.fragment_loader, container, false);

        // Configurar cliente Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        // Obtener usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String tipoUsuario = documentSnapshot.getString("tipoUsuario");

                        if ("jugador".equals(tipoUsuario)) {
                            mostrarLayoutJugador(inflater, container);
                        } else if ("entrenador".equals(tipoUsuario)) {
                            mostrarLayoutEntrenador(inflater, container);
                        } else {
                            mostrarError("Tipo de usuario no reconocido");
                        }
                    })
                    .addOnFailureListener(e -> {
                        mostrarError("Error al obtener tipo de usuario");
                    });
        } else {
            mostrarError("Usuario no autenticado");
        }

        return rootView;
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    private void mostrarLayoutJugador(LayoutInflater inflater, ViewGroup container) {
        View jugadorView = inflater.inflate(R.layout.fragment_perfil, container, false);
        FrameLayout frameLayout = rootView.findViewById(R.id.container);
        frameLayout.removeAllViews();
        frameLayout.addView(jugadorView);
        configurarVistaJugador(jugadorView);
    }

    private void mostrarLayoutEntrenador(LayoutInflater inflater, ViewGroup container) {
        View entrenadorView = inflater.inflate(R.layout.fragment_perfil_entrenador, container, false);
        FrameLayout frameLayout = rootView.findViewById(R.id.container);
        frameLayout.removeAllViews();
        frameLayout.addView(entrenadorView);
        configurarVistaEntrenador(entrenadorView);
    }

    private void configurarVistaEntrenador(View view) {
        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }

    private void configurarVistaJugador(View view) {
        setHasOptionsMenu(true);

        MaterialToolbar mtbBarJugador = view.findViewById(R.id.mtbBarJugador);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mtbBarJugador);
        mtbBarJugador.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        Button btnCerrar = view.findViewById(R.id.btnCerrar);
        TextView lblNombreJugador = view.findViewById(R.id.lblNombreJugador);
        TextView lblTipo = view.findViewById(R.id.lblTipo);
        TextView lblEstilo = view.findViewById(R.id.lblEstilo);
        TextView lblNombreEquipo = view.findViewById(R.id.lblNombreEquipo);
        TextView lblVictorias = view.findViewById(R.id.lblVictorias);
        TextView lblDerrotas = view.findViewById(R.id.lblDerrotas);
        TextView lblPorcentaje = view.findViewById(R.id.lblPorcentaje);
        TextView lblPartidosJugados = view.findViewById(R.id.lblPartidosJugados);
        ImageView imgJugador = view.findViewById(R.id.imgJugador);
        ImageView imgEscudo = view.findViewById(R.id.imgEscudo);

        btnCerrar.setOnClickListener(v -> cerrarSesion());

        FirebaseController.obtenerDatosJugador(jugador -> {
            lblNombreJugador.setText(jugador.getNombre());
            lblPartidosJugados.setText(String.valueOf(jugador.getPartidosJugados()));
            lblTipo.setText(jugador.getTipoUsuario());
            lblEstilo.setText(jugador.getEstilo());
            lblVictorias.setText(String.valueOf(jugador.getVictorias()));
            lblDerrotas.setText(String.valueOf(jugador.getDerrotas()));

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseController.actualizarPorcentajeVictorias(uid, jugador.getVictorias(), jugador.getPartidosJugados(), lblPorcentaje);

            if (jugador.getFotoPerfil() != null && !jugador.getFotoPerfil().isEmpty()) {
                FirebaseController.cargarImagenDesdeStorage(imgJugador, jugador.getFotoPerfil(), FirebaseController.imagenPerfilPorDefecto);
            }

            FirebaseController.obtenerEquipoPorId(jugador.getEquipoId().split("/")[2], equipo -> {
                lblNombreEquipo.setText(equipo.getNombre());
                if (equipo.getEscudo() != null && !equipo.getEscudo().isEmpty()) {
                    FirebaseController.cargarImagenDesdeStorage(imgEscudo, equipo.getEscudo(), FirebaseController.imagenPorDefecto);
                }
            }, e -> lblNombreEquipo.setText("Equipo no encontrado"));

        }, e -> lblNombreJugador.setText("Error al cargar datos"));
    }

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
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}