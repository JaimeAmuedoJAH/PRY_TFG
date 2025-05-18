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
import com.jah.pry_rfatm.Modelo.Entrenador;
import com.jah.pry_rfatm.Modelo.Jugador;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Activities.EditarPerfilActivity;
import com.jah.pry_rfatm.Vista.Activities.LogInActivity;

import java.util.List;

/**
 * Fragmento que representa el perfil del usuario autenticado.
 * Carga el layout adecuado según el tipo de usuario (jugador o entrenador).
 * Permite cerrar sesión, ver estadísticas y acceder a la edición de perfil (jugadores).
 */
public class PerfilFragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private View rootView;
    String imagenPerfil, nombreUsuario, estilo, tipoUsuario, imagenEscudo;
    String[] jugadores;

    /**
     * Infla una vista de carga mientras se determina el tipo de usuario.
     *
     * @param inflater           El LayoutInflater.
     * @param container          El contenedor padre.
     * @param savedInstanceState Datos guardados del estado anterior.
     * @return Vista inicial.
     */
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
                        String tipoUser = documentSnapshot.getString("tipoUsuario");

                        if ("jugador".equals(tipoUser)) {
                            tipoUsuario = "jugador";
                            mostrarLayoutJugador(inflater, container);
                        } else if ("entrenador".equals(tipoUser)) {
                            tipoUsuario = "entrenador";
                            mostrarLayoutEntrenador(inflater, container);
                        } else {
                            mostrarError(getString(R.string.toast_tipo_de_usuario_no_reconocido));
                        }
                    })
                    .addOnFailureListener(e -> {
                        mostrarError(getString(R.string.toast_error_al_obtener_tipo_de_usuario));
                    });
        } else {
            mostrarError(getString(R.string.toast_usuario_no_autenticado));
        }

        return rootView;
    }

    /**
     * Recarga los datos del jugador al volver al fragmento.
     */
    @Override
    public void onResume() {
        super.onResume();

        // Forzar recarga de los datos del jugador al volver
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore.getInstance().collection("usuarios").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String tipoUser = documentSnapshot.getString("tipoUsuario");

                        if ("jugador".equals(tipoUsuario)) {
                            mostrarLayoutJugador(LayoutInflater.from(getContext()), (ViewGroup) rootView.findViewById(R.id.container));
                        } else if ("entrenador".equals(tipoUsuario)) {
                            mostrarLayoutEntrenador(LayoutInflater.from(getContext()), (ViewGroup) rootView.findViewById(R.id.container));
                        }
                    });
        }
    }
    /**
     * Muestra un mensaje de error mediante un Toast.
     *
     * @param mensaje Mensaje de error.
     */
    private void mostrarError(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
    }

    /**
     * Muestra el layout de perfil para usuarios tipo jugador y configura sus vistas.
     *
     * @param inflater  LayoutInflater para inflar el layout.
     * @param container Contenedor padre.
     */
    private void mostrarLayoutJugador(LayoutInflater inflater, ViewGroup container) {
        View jugadorView = inflater.inflate(R.layout.fragment_perfil_jugador, container, false);
        FrameLayout frameLayout = rootView.findViewById(R.id.container);
        frameLayout.removeAllViews();
        frameLayout.addView(jugadorView);
        configurarVistaJugador(jugadorView);
    }

    /**
     * Muestra el layout de perfil para usuarios tipo entrenador y configura sus vistas.
     *
     * @param inflater  LayoutInflater para inflar el layout.
     * @param container Contenedor padre.
     */
    private void mostrarLayoutEntrenador(LayoutInflater inflater, ViewGroup container) {
        View entrenadorView = inflater.inflate(R.layout.fragment_perfil_entrenador, container, false);
        FrameLayout frameLayout = rootView.findViewById(R.id.container);
        frameLayout.removeAllViews();
        frameLayout.addView(entrenadorView);
        configurarVistaEntrenador(entrenadorView);
    }
    /**
     * Configura la vista de perfil del entrenador.
     *
     * @param view Vista raíz del layout de entrenador.
     */
    private void configurarVistaEntrenador(View view) {
        setHasOptionsMenu(true);

        Button btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());

        MaterialToolbar mtbBarEntrenador = view.findViewById(R.id.mtbBarEntrenador);
        if (isAdded()) {
            ((AppCompatActivity) requireActivity()).setSupportActionBar(mtbBarEntrenador);
            mtbBarEntrenador.setBackgroundColor(getResources().getColor(R.color.color_fondos));
        }

        TextView lblNombreEntrenador = view.findViewById(R.id.lblNombreEntrenador);
        TextView lblNombreEquipoEnt = view.findViewById(R.id.lblNombreEquipoEnt);
        TextView lblJugador1 = view.findViewById(R.id.lblJugador1);
        TextView lblJugador2 = view.findViewById(R.id.lblJugador2);
        TextView lblJugador3 = view.findViewById(R.id.lblJugador3);
        TextView lblJugador4 = view.findViewById(R.id.lblJugador4);
        TextView lblJugador5 = view.findViewById(R.id.lblJugador5);
        TextView lblJugador6 = view.findViewById(R.id.lblJugador6);
        ImageView imgFotoEntrenador = view.findViewById(R.id.imgFotoEntrenador);
        ImageView imgFotoEscudo = view.findViewById(R.id.imgFotoEscudo);

        jugadores = new String[6]; // Guardará los nombres para futuras acciones

        FirebaseController.obtenerDatosUsuario(usuario -> {
            if (usuario instanceof Entrenador) {
                Entrenador entrenador = (Entrenador) usuario;

                lblNombreEntrenador.setText(entrenador.getNombre());
                nombreUsuario = entrenador.getNombre();
                imagenPerfil = entrenador.getFotoPerfil();

                if (entrenador.getFotoPerfil() != null && !entrenador.getFotoPerfil().isEmpty()) {
                    FirebaseController.cargarImagenDesdeStorage(
                            imgFotoEntrenador,
                            entrenador.getFotoPerfil(),
                            FirebaseController.imagenPerfilPorDefecto
                    );
                }

                String equipoId = entrenador.getEquipoId().split("/")[2]; // Formato esperado: "equipos/{id}"

                FirebaseController.obtenerEquipoPorId(equipoId, equipo -> {
                    lblNombreEquipoEnt.setText(equipo.getNombre());
                    imagenEscudo = equipo.getEscudo();
                    if (equipo.getEscudo() != null && !equipo.getEscudo().isEmpty()) {
                        FirebaseController.cargarImagenDesdeStorage(
                                imgFotoEscudo,
                                equipo.getEscudo(),
                                FirebaseController.imagenPorDefecto
                        );
                    }

                    //FirebaseFirestore db = FirebaseFirestore.getInstance();

                    List<String> titulares = equipo.getJugadores();
                    List<String> suplentes = equipo.getSuplentes();

                    TextView[] lblTitulares = {lblJugador1, lblJugador2, lblJugador3};
                    TextView[] lblSuplentes = {lblJugador4, lblJugador5, lblJugador6};

                    // Titulares
                    for (int i = 0; i < titulares.size() && i < lblTitulares.length; i++) {
                        final int index = i;
                        String jugadorId = titulares.get(i);

                        if (jugadorId != null && !jugadorId.isEmpty()) {
                            FirebaseController.db.collection("usuarios")
                                    .document(jugadorId)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) {
                                            Jugador jugador = snapshot.toObject(Jugador.class);
                                            String nombre = (jugador != null && jugador.getNombre() != null)
                                                    ? jugador.getNombre()
                                                    : getString(R.string.jugador_desconocido);
                                            lblTitulares[index].setText(nombre);
                                            jugadores[index] = nombre;
                                        } else {
                                            lblTitulares[index].setText(R.string.no_encontrado);
                                        }
                                    })
                                    .addOnFailureListener(e -> lblTitulares[index].setText(R.string.error));
                        }
                    }

                    // Suplentes
                    for (int i = 0; i < suplentes.size() && i < lblSuplentes.length; i++) {
                        final int index = i;
                        String jugadorId = suplentes.get(i);

                        if (jugadorId != null && !jugadorId.isEmpty()) {
                            FirebaseController.db.collection("usuarios")
                                    .document(jugadorId)
                                    .get()
                                    .addOnSuccessListener(snapshot -> {
                                        if (snapshot.exists()) {
                                            Jugador jugador = snapshot.toObject(Jugador.class);
                                            String nombre = (jugador != null && jugador.getNombre() != null)
                                                    ? jugador.getNombre()
                                                    : getString(R.string.jugador_desconocido);
                                            lblSuplentes[index].setText(nombre);
                                            jugadores[index + 3] = nombre;
                                        }
                                    })
                                    .addOnFailureListener(e -> lblSuplentes[index].setText(R.string.error));
                        }
                    }

                }, e -> lblNombreEquipoEnt.setText(R.string.toast_equipo_no_encontrado));

            } else {
                lblNombreEntrenador.setText(R.string.no_es_un_entrenador);
            }
        }, e -> lblNombreEntrenador.setText(R.string.toast_error_al_cargar_datos));
    }

    /**
     * Configura la vista de perfil del jugador.
     * Muestra datos personales, equipo, estadísticas y permite cerrar sesión.
     *
     * @param view Vista raíz del layout de jugador.
     */
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

        FirebaseController.obtenerDatosUsuario(usuario -> {
            Jugador jugador = (Jugador) usuario;

            lblNombreJugador.setText(jugador.getNombre());
            lblPartidosJugados.setText(String.valueOf(jugador.getPartidosJugados()));
            lblTipo.setText(jugador.getTipoUsuario());
            lblEstilo.setText(jugador.getEstilo());
            lblVictorias.setText(String.valueOf(jugador.getVictorias()));
            lblDerrotas.setText(String.valueOf(jugador.getDerrotas()));
            imagenPerfil = jugador.getFotoPerfil();
            nombreUsuario = jugador.getNombre();
            estilo = jugador.getEstilo();

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
            }, e -> lblNombreEquipo.setText(R.string.toast_equipo_no_encontrado));

        }, e -> lblNombreJugador.setText(R.string.toast_error_al_cargar_datos));
    }

    /**
     * Cierra la sesión actual de Firebase y Google Sign-In.
     */
    private void cerrarSesion() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent intent = new Intent(getActivity(), LogInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    /**
     * Infla el menú superior del fragmento (jugador).
     *
     * @param menu     El menú en el que inflar los items.
     * @param inflater El inflador del menú.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_bar_perfil, menu);
    }

    /**
     * Maneja los eventos de selección del menú (botón de editar perfil).
     *
     * @param item Item seleccionado.
     * @return true si se manejó el evento.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_editar) {
            Intent intent = new Intent(requireContext(), EditarPerfilActivity.class);
            if(tipoUsuario.equals("jugador")){
                intent.putExtra("imagenPerfil", imagenPerfil);
                intent.putExtra("nombreUsuario", nombreUsuario);
                intent.putExtra("estilo", estilo);
                intent.putExtra("tipoUsuario", tipoUsuario);
            }else if(tipoUsuario.equals("entrenador")){
                intent.putExtra("imagenPerfil", imagenPerfil);
                intent.putExtra("imagenEscudo", imagenEscudo);
                intent.putExtra("nombreUsuario", nombreUsuario);
                intent.putExtra("tipoUsuario", tipoUsuario);
                intent.putExtra("jugador1", jugadores[0]);
                intent.putExtra("jugador2", jugadores[1]);
                intent.putExtra("jugador3", jugadores[2]);
                intent.putExtra("jugador4", jugadores[3]);
                intent.putExtra("jugador5", jugadores[4]);
                intent.putExtra("jugador6", jugadores[5]);
            }
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}