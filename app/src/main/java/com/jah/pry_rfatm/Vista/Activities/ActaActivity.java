package com.jah.pry_rfatm.Vista.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActaActivity extends AppCompatActivity {

    EditText lblJugadorA1, lblSet11, lblSet21, lblSet31, lblSet41, lblSet51, lblJugadorY1;
    EditText lblJugadorB1, lblSet12, lblSet22, lblSet32, lblSet42, lblSet52, lblJugadorX1;
    EditText lblJugadorC1, lblSet13, lblSet23, lblSet33, lblSet43, lblSet53, lblJugadorZ1;
    EditText lblJugadorA2, lblSet14, lblSet24, lblSet34, lblSet44, lblSet54, lblJugadorX2;
    EditText lblJugadorC2, lblSet15, lblSet25, lblSet35, lblSet45, lblSet55, lblJugadorY2;
    EditText lblJugadorB2, lblSet16, lblSet26, lblSet36, lblSet46, lblSet56, lblJugadorZ2;
    EditText lblJugadorA3, lblSet17, lblSet27, lblSet37, lblSet47, lblSet57, lblJugadorX3;
    EditText txtResultado;
    EditText[] textos;
    MaterialToolbar mtbActa;
    TextView lblEquipoABC, lblEquipoXYZ;
    String idPartido;
    int puntosEquipoABC, puntosEquipoXYZ;

    /**
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acta);
        UtilesUI.configurarStatusBar(this);
        initComponents();
        setSupportActionBar(mtbActa);
        mtbActa.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        String equipoABCId = getIntent().getStringExtra("idLocal");
        String equipoXYZId = getIntent().getStringExtra("idVisitante");

        if (equipoABCId != null && equipoXYZId != null) {
            cargarNombreEquipo(equipoABCId, true);
            cargarNombreEquipo(equipoXYZId, false);
            cargarJugadoresDesdeFirestore(equipoABCId, equipoXYZId);
        }

        if(idPartido != null) {
            cargarDatosActa();
        }

        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        boolean deshabilitar = prefs.getBoolean("camposDeshabilitados", false);

       verEstadoPartido(idPartido, estado -> {
            if (deshabilitar && "jugado".equals(estado)) {
                for (EditText editText : textos) {
                    editText.setEnabled(false);
                    editText.setFocusable(false);
                    editText.setClickable(false);
                    editText.setCursorVisible(false);
                }
            }
        });
    }

    /**
     * Interfaz para obtener el estado del partido
     */
    public interface EstadoCallback {
        void onEstadoObtenido(String estado);
    }

    /**
     * Obtiene el estado del partido
     * @param idPartido
     * @param callback
     */
    private void verEstadoPartido(String idPartido, EstadoCallback callback) {
        DocumentReference partidoRef = FirebaseController.db.collection("partidos").document(idPartido);

        partidoRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String estadoPartido = documentSnapshot.getString("estado");
                callback.onEstadoObtenido(estadoPartido);
            } else {
                callback.onEstadoObtenido(null);
            }
        }).addOnFailureListener(e -> {
            Log.e("ActaActivity", getString(R.string.error_al_obtener_el_estado_del_partido), e);
            callback.onEstadoObtenido(null);
        });
    }

    /**
     * Carga los datos del acta desde Firestore, en el caso de que exista el documento
     */
    private void cargarDatosActa() {
        if (idPartido == null) return;

        DocumentReference actaRef = FirebaseFirestore.getInstance()
                .collection("actas")
                .document(idPartido);

        actaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> actaData = documentSnapshot.getData();

                if (actaData != null) {
                    // partidoAY
                    Map<String, Object> partidoAY = (Map<String, Object>) actaData.get("partidoAY");
                    if (partidoAY != null) {
                        String setsABC = (String) partidoAY.get("setsABC");
                        String setsXYZ = (String) partidoAY.get("setsXYZ");

                        if (setsABC != null && setsXYZ != null) {
                            String[] setsABCArray = setsABC.split("-");
                            String[] setsXYZArray = setsXYZ.split("-");
                            String[] sets = new String[]{
                                    setsABCArray[0] + "-" + setsXYZArray[0],
                                    setsABCArray[1] + "-" + setsXYZArray[1],
                                    setsABCArray[2] + "-" + setsXYZArray[2],
                                    setsABCArray[3] + "-" + setsXYZArray[3],
                                    setsABCArray[4] + "-" + setsXYZArray[4]
                            };
                            lblSet11.setText(sets[0]);
                            lblSet21.setText(sets[1]);
                            lblSet31.setText(sets[2]);
                            lblSet41.setText(sets[3]);
                            lblSet51.setText(sets[4]);
                        }
                    }

                    // partidoBX
                    Map<String, Object> partidoBX = (Map<String, Object>) actaData.get("partidoBX");
                    if (partidoBX != null) {
                        String setsABC = (String) partidoBX.get("setsABC");
                        String setsXYZ = (String) partidoBX.get("setsXYZ");

                        if (setsABC != null && setsXYZ != null) {
                            String[] setsABCArray = setsABC.split("-");
                            String[] setsXYZArray = setsXYZ.split("-");
                            String[] sets = new String[]{
                                    setsABCArray[0] + "-" + setsXYZArray[0],
                                    setsABCArray[1] + "-" + setsXYZArray[1],
                                    setsABCArray[2] + "-" + setsXYZArray[2],
                                    setsABCArray[3] + "-" + setsXYZArray[3],
                                    setsABCArray[4] + "-" + setsXYZArray[4]
                            };
                            lblSet12.setText(sets[0]);
                            lblSet22.setText(sets[1]);
                            lblSet32.setText(sets[2]);
                            lblSet42.setText(sets[3]);
                            lblSet52.setText(sets[4]);
                        }
                    }

                    // partidoCZ
                    Map<String, Object> partidoCZ = (Map<String, Object>) actaData.get("partidoCZ");
                    if (partidoCZ != null) {
                        String setsABC = (String) partidoCZ.get("setsABC");
                        String setsXYZ = (String) partidoCZ.get("setsXYZ");

                        if (setsABC != null && setsXYZ != null) {
                            String[] setsABCArray = setsABC.split("-");
                            String[] setsXYZArray = setsXYZ.split("-");
                            String[] sets = new String[]{
                                    setsABCArray[0] + "-" + setsXYZArray[0],
                                    setsABCArray[1] + "-" + setsXYZArray[1],
                                    setsABCArray[2] + "-" + setsXYZArray[2],
                                    setsABCArray[3] + "-" + setsXYZArray[3],
                                    setsABCArray[4] + "-" + setsXYZArray[4]
                            };
                            lblSet13.setText(sets[0]);
                            lblSet23.setText(sets[1]);
                            lblSet33.setText(sets[2]);
                            lblSet43.setText(sets[3]);
                            lblSet53.setText(sets[4]);
                        }
                    }

                    // partidoAY2
                    Map<String, Object> partidoAY2 = (Map<String, Object>) actaData.get("partidoAY2");
                    if (partidoAY2 != null) {
                        String setsABC = (String) partidoAY2.get("setsABC");
                        String setsXYZ = (String) partidoAY2.get("setsXYZ");

                        if (setsABC != null && setsXYZ != null) {
                            String[] setsABCArray = setsABC.split("-");
                            String[] setsXYZArray = setsXYZ.split("-");
                            String[] sets = new String[]{
                                    setsABCArray[0] + "-" + setsXYZArray[0],
                                    setsABCArray[1] + "-" + setsXYZArray[1],
                                    setsABCArray[2] + "-" + setsXYZArray[2],
                                    setsABCArray[3] + "-" + setsXYZArray[3],
                                    setsABCArray[4] + "-" + setsXYZArray[4]
                            };
                            lblSet14.setText(sets[0]);
                            lblSet24.setText(sets[1]);
                            lblSet34.setText(sets[2]);
                            lblSet44.setText(sets[3]);
                            lblSet54.setText(sets[4]);
                        }
                    }

                    // partidoCX
                    Map<String, Object> partidoCX = (Map<String, Object>) actaData.get("partidoCX");
                    if (partidoCX != null) {
                        String setsABC = (String) partidoCX.get("setsABC");
                        String setsXYZ = (String) partidoCX.get("setsXYZ");

                        if (setsABC != null && setsXYZ != null) {
                            String[] setsABCArray = setsABC.split("-");
                            String[] setsXYZArray = setsXYZ.split("-");
                            String[] sets = new String[]{
                                    setsABCArray[0] + "-" + setsXYZArray[0],
                                    setsABCArray[1] + "-" + setsXYZArray[1],
                                    setsABCArray[2] + "-" + setsXYZArray[2],
                                    setsABCArray[3] + "-" + setsXYZArray[3],
                                    setsABCArray[4] + "-" + setsXYZArray[4]
                            };
                            lblSet15.setText(sets[0]);
                            lblSet25.setText(sets[1]);
                            lblSet35.setText(sets[2]);
                            lblSet45.setText(sets[3]);
                            lblSet55.setText(sets[4]);
                        }
                    }

                    // partidoBZ
                    Map<String, Object> partidoBZ = (Map<String, Object>) actaData.get("partidoBZ");
                    if (partidoBZ != null) {
                        String setsABC = (String) partidoBZ.get("setsABC");
                        String setsXYZ = (String) partidoBZ.get("setsXYZ");

                        if (setsABC != null && setsXYZ != null) {
                            String[] setsABCArray = setsABC.split("-");
                            String[] setsXYZArray = setsXYZ.split("-");
                            String[] sets = new String[]{
                                    setsABCArray[0] + "-" + setsXYZArray[0],
                                    setsABCArray[1] + "-" + setsXYZArray[1],
                                    setsABCArray[2] + "-" + setsXYZArray[2],
                                    setsABCArray[3] + "-" + setsXYZArray[3],
                                    setsABCArray[4] + "-" + setsXYZArray[4]
                            };
                            lblSet16.setText(sets[0]);
                            lblSet26.setText(sets[1]);
                            lblSet36.setText(sets[2]);
                            lblSet46.setText(sets[3]);
                            lblSet56.setText(sets[4]);
                        }
                    }

                    // partidoAX2
                    Map<String, Object> partidoAX2 = (Map<String, Object>) actaData.get("partidoAX2");
                    if (partidoAX2 != null) {
                        String setsABC = (String) partidoAX2.get("setsABC");
                        String setsXYZ = (String) partidoAX2.get("setsXYZ");

                        if (setsABC != null && setsXYZ != null) {
                            String[] setsABCArray = setsABC.split("-");
                            String[] setsXYZArray = setsXYZ.split("-");
                            String[] sets = new String[]{
                                    setsABCArray[0] + "-" + setsXYZArray[0],
                                    setsABCArray[1] + "-" + setsXYZArray[1],
                                    setsABCArray[2] + "-" + setsXYZArray[2],
                                    setsABCArray[3] + "-" + setsXYZArray[3],
                                    setsABCArray[4] + "-" + setsXYZArray[4]
                            };
                            lblSet17.setText(sets[0]);
                            lblSet27.setText(sets[1]);
                            lblSet37.setText(sets[2]);
                            lblSet47.setText(sets[3]);
                            lblSet57.setText(sets[4]);
                        }
                    }
                    txtResultado.setText((String) actaData.getOrDefault("resultadoFinal", ""));
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("ActaActivity", getString(R.string.error_al_cargar_acta), e);
        });
    }

    /**
     * Crea el menú de opciones.
     * @param menu The options menu in which you place your items.
     *
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_acta, menu);
        return true;
    }

    /**
     * Maneja la selección de un elemento del menú.
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_guardar) {
            guardarActaEnFirebase();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Guarda el acta en Firebase
     */
    private void guardarActaEnFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String actaId = idPartido; // Usa el ID correspondiente del partido

        Map<String, Object> actaMap = new HashMap<>();

        // Partido AY
        actaMap.put("partidoAY", crearPartido(
                R.id.lblJugadorA1, R.id.lblJugadorY1,
                R.id.lblSet11, R.id.lblSet21, R.id.lblSet31, R.id.lblSet41, R.id.lblSet51
        ));

        // Partido BX
        actaMap.put("partidoBX", crearPartido(
                R.id.lblJugadorB1, R.id.lblJugadorX1,
                R.id.lblSet12, R.id.lblSet22, R.id.lblSet32, R.id.lblSet42, R.id.lblSet52
        ));

        // Partido CZ
        actaMap.put("partidoCZ", crearPartido(
                R.id.lblJugadorC1, R.id.lblJugadorZ1,
                R.id.lblSet13, R.id.lblSet23, R.id.lblSet33, R.id.lblSet43, R.id.lblSet53
        ));

        // Partido AY (repetido, segundo partido)
        actaMap.put("partidoAY2", crearPartido(
                R.id.lblJugadorA2, R.id.lblJugadorY2,
                R.id.lblSet14, R.id.lblSet24, R.id.lblSet34, R.id.lblSet44, R.id.lblSet54
        ));

        // Partido CX
        actaMap.put("partidoCX", crearPartido(
                R.id.lblJugadorC2, R.id.lblJugadorX2,
                R.id.lblSet15, R.id.lblSet25, R.id.lblSet35, R.id.lblSet45, R.id.lblSet55
        ));

        // Partido BZ
        actaMap.put("partidoBZ", crearPartido(
                R.id.lblJugadorB2, R.id.lblJugadorZ2,
                R.id.lblSet16, R.id.lblSet26, R.id.lblSet36, R.id.lblSet46, R.id.lblSet56
        ));

        // Partido AX2
        actaMap.put("partidoAX2", crearPartido(
                R.id.lblJugadorA3, R.id.lblJugadorX3,
                R.id.lblSet17, R.id.lblSet27, R.id.lblSet37, R.id.lblSet47, R.id.lblSet57
        ));

        // Actualizar los equipos y jugadores
        actualizarPuntuacionFinal(actaMap);
        modificarEquipos();

        // Guardar en Firestore
        verEstadoPartido(idPartido, estado -> {
            if ("pendiente".equals(estado)) {
                db.collection("actas").document(actaId).set(actaMap)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, getString(R.string.acta_guardada_con_xito), Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.error_al_guardar_el_acta), Toast.LENGTH_SHORT).show());
            }
        });
    }

    /**
     * Actualiza la puntuación final del acta
     * @param actaData
     */
    private void actualizarPuntuacionFinal(Map<String, Object> actaData) {
        puntosEquipoABC = 0;
        puntosEquipoXYZ = 0;
        boolean todosJugados = true;

        List<String> partidos = Arrays.asList("partidoAY", "partidoBX", "partidoCZ", "partidoAY2", "partidoCX", "partidoBZ", "partidoAX2");

        for (String partidoKey : partidos) {
            Map<String, Object> partido = (Map<String, Object>) actaData.get(partidoKey);
            if (partido != null) {
                String resultado = (String) partido.get("resultado");
                if (resultado != null && resultado.contains("-")) {
                    String[] sets = resultado.split("-");
                    if (sets.length == 2) {
                        try {
                            int setsABC = Integer.parseInt(sets[0].trim());
                            int setsXYZ = Integer.parseInt(sets[1].trim());

                            if (setsABC == 3) puntosEquipoABC++;
                            else if (setsXYZ == 3) puntosEquipoXYZ++;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            todosJugados = false;
                        }
                    } else {
                        todosJugados = false;
                    }
                } else {
                    todosJugados = false;
                }
            } else {
                todosJugados = false;
            }
            modificarJugadores(partidoKey);
        }

        // Mostrar resultado en UI
        String resultadoFinal = puntosEquipoABC + "-" + puntosEquipoXYZ;
        txtResultado.setText(resultadoFinal);

        // Guardar resultadoFinal en el documento del acta
        actaData.put("resultadoFinal", resultadoFinal);

        if (todosJugados) {
            actualizarEstadoPartido(resultadoFinal, puntosEquipoABC, puntosEquipoXYZ);
            SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
            prefs.edit().putBoolean("camposDeshabilitados", true).apply();
        }
    }

    /**
     * Actualiza el estado del partido
     * @param resultadoFinal
     * @param setsGanados
     * @param setsPerdidos
     */
    private void actualizarEstadoPartido(String resultadoFinal, int setsGanados, int setsPerdidos) {
        Map<String, Object> datosPartido = new HashMap<>();
        datosPartido.put("estado", "jugado");
        datosPartido.put("resultado", resultadoFinal);
        datosPartido.put("setsGanados", setsGanados);
        datosPartido.put("setsPerdidos", setsPerdidos);

        FirebaseController.db.collection("partidos").document(idPartido)
                .update(datosPartido)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ActaActivity", getString(R.string.estado_del_partido_actualizado_a_jugado));
                })
                .addOnFailureListener(e -> {
                    Log.e("ActaActivity", getString(R.string.error_al_actualizar_estado_del_partido), e);
                });
    }

    /**
     * Crea un partido con los jugadores dados
     * @param jugadorABCId
     * @param jugadorXYZId
     * @param setIds
     * @return
     */
    private Map<String, Object> crearPartido(int jugadorABCId, int jugadorXYZId, int... setIds) {
        Map<String, Object> partido = new HashMap<>();
        partido.put("jugadorABC", getTexto(jugadorABCId));
        partido.put("jugadorXYZ", getTexto(jugadorXYZId));

        List<String> setsABC = new ArrayList<>();
        List<String> setsXYZ = new ArrayList<>();
        int setsGanadosABC = 0;
        int setsGanadosXYZ = 0;

        for (int id : setIds) {
            String texto = getTexto(id);
            if (texto.contains("-")) {
                String[] partes = texto.split("-");
                if (partes.length == 2) {
                    try {
                        int puntosABC = Integer.parseInt(partes[0].trim());
                        int puntosXYZ = Integer.parseInt(partes[1].trim());
                        setsABC.add(String.valueOf(puntosABC));
                        setsXYZ.add(String.valueOf(puntosXYZ));
                        if (puntosABC > puntosXYZ) setsGanadosABC++;
                        else if (puntosXYZ > puntosABC) setsGanadosXYZ++;
                    } catch (NumberFormatException e) {
                        setsABC.add("0");
                        setsXYZ.add("0");
                    }
                } else {
                    setsABC.add("0");
                    setsXYZ.add("0");
                }
            } else {
                setsABC.add("0");
                setsXYZ.add("0");
            }
        }

        partido.put("setsABC", String.join("-", setsABC));
        partido.put("setsXYZ", String.join("-", setsXYZ));
        partido.put("resultado", setsGanadosABC + "-" + setsGanadosXYZ);

        return partido;
    }

    /**
     * Obtiene el texto de un campo de texto
     * @param id
     * @return
     */
    private String getTexto(int id) {
        EditText editText = findViewById(id);
        return editText != null ? editText.getText().toString().trim() : "";
    }

    /**
     * Carga el nombre del equipo
     * @param equipoId
     * @param esABC
     */
    private void cargarNombreEquipo(String equipoId, boolean esABC) {
        FirebaseFirestore.getInstance().collection("equipos").document(equipoId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nombre = doc.getString("nombre");
                        if (nombre != null) {
                            if (esABC) lblEquipoABC.setText(nombre);
                            else lblEquipoXYZ.setText(nombre);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", getString(R.string.error_al_cargar_nombre_de_equipo), e));
    }

    /**
     * Carga los jugadores
     * @param idABC
     * @param idXYZ
     */
    private void cargarJugadoresDesdeFirestore(String idABC, String idXYZ) {
        cargarJugadores(idABC, true);
        cargarJugadores(idXYZ, false);
    }

    /**
     * Carga los jugadores
     * @param equipoId
     * @param esABC
     */
    private void cargarJugadores(String equipoId, boolean esABC) {
        FirebaseController.db.collection("equipos").document(equipoId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Object jugadoresObj = doc.get("jugadores");
                        if (jugadoresObj instanceof List<?>) {
                            List<?> lista = (List<?>) jugadoresObj;
                            for (int i = 0; i < lista.size(); i++) {
                                Object jugador = lista.get(i);
                                if (jugador instanceof String) {
                                    String jugadorId = (String) jugador;
                                    // Obtener el documento del jugador por ID
                                    int finalI = i;
                                    FirebaseController.db.collection("usuarios").document(jugadorId)
                                            .get()
                                            .addOnSuccessListener(jugadorDoc -> {
                                                if (jugadorDoc.exists()) {
                                                    String nombre = jugadorDoc.getString("nombre");
                                                    if (nombre != null) {
                                                        if (esABC) asignarNombreJugadorABCPorIndice(finalI, nombre);
                                                        else asignarNombreJugadorXYZPorIndice(finalI, nombre);
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("Firebase", getString(R.string.error_al_obtener_jugador) + jugadorId, e));
                                } else if (jugador instanceof Map) {
                                    String nombre = extraerNombreJugador(jugador);
                                    if (nombre != null) {
                                        if (esABC) asignarNombreJugadorABCPorIndice(i, nombre);
                                        else asignarNombreJugadorXYZPorIndice(i, nombre);
                                    }
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", getString(R.string.error_al_cargar_jugadores), e));
    }

    /**
     * Modifica los equipos
     */
    private void modificarEquipos(){
        FirebaseController.db.collection("partidos").document(idPartido).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String idLocal = doc.getString("equipoLocalId");
                        String idVisitante = doc.getString("equipoVisitanteId");
                        modificarPartidosJugados(idLocal.split("/")[2], idVisitante.split("/")[2]);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, getString(R.string.error_al_cargar_equipos), Toast.LENGTH_SHORT).show());
    }

    /**
     * Modifica los partidos jugados
     * @param idLocal
     * @param idVisitante
     */
    private void modificarPartidosJugados(String idLocal, String idVisitante) {
        Map <String, Object> datosPartidoABC = new HashMap<>();
        Map <String, Object> datosPartidoXYZ = new HashMap<>();
        datosPartidoABC.put("partidosJugados", FieldValue.increment(1));
        datosPartidoXYZ.put("partidosJugados", FieldValue.increment(1));
        if(puntosEquipoABC > puntosEquipoXYZ){
            datosPartidoABC.put("victorias", FieldValue.increment(1));
            datosPartidoXYZ.put("derrotas", FieldValue.increment(1));
        }else{
            datosPartidoXYZ.put("victorias", FieldValue.increment(1));
            datosPartidoABC.put("derrotas", FieldValue.increment(1));
        }
        verEstadoPartido(idPartido, estado -> {
            if ("pendiente".equals(estado)) {
                FirebaseController.db.collection("equipos").document(idLocal).update(datosPartidoABC);
                FirebaseController.db.collection("equipos").document(idVisitante).update(datosPartidoXYZ);
            }
        });
    }

    /**
     * Modifica los jugadores
     * @param partidoKey
     */
    private void modificarJugadores(String partidoKey) {
        FirebaseController.db.collection("actas").document(idPartido).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Map<String, Object> partido = (Map<String, Object>) doc.get(partidoKey);
                        if (partido != null) {
                            String jugadorABC = (String) partido.get("jugadorABC");
                            String jugadorXYZ = (String) partido.get("jugadorXYZ");
                            String resultado = (String) partido.get("resultado");

                            if (jugadorABC != null && jugadorXYZ != null && resultado != null && resultado.contains("-")) {
                                String[] sets = resultado.split("-");
                                if (sets.length == 2) {
                                    try {
                                        int setsABC = Integer.parseInt(sets[0].trim());
                                        int setsXYZ = Integer.parseInt(sets[1].trim());

                                        boolean abcGana = setsABC == 3;
                                        boolean xyzGana = setsXYZ == 3;

                                        verEstadoPartido(idPartido, estado -> {
                                            if ("pendiente".equals(estado)) {
                                                // Actualizar estadísticas para ABC
                                                actualizarEstadisticasJugador(jugadorABC, abcGana);
                                                // Actualizar estadísticas para XYZ
                                                actualizarEstadisticasJugador(jugadorXYZ, !xyzGana);
                                            }
                                        });
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, getString(R.string.error_al_cargar_jugadores), Toast.LENGTH_SHORT).show());
    }

    /**
     * Actualiza las estadísticas de un jugador
     * @param nombreUsuario
     * @param victoria
     */
    private void actualizarEstadisticasJugador(String nombreUsuario, boolean victoria) {
        FirebaseController.db.collection("usuarios")
                .whereEqualTo("nombre", nombreUsuario)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        DocumentReference userRef = doc.getReference();

                        Long victorias = doc.getLong("victorias");
                        Long derrotas = doc.getLong("derrotas");
                        Long partidosJugados = doc.getLong("partidosJugados");

                        if (victorias == null) victorias = 0L;
                        if (derrotas == null) derrotas = 0L;
                        if (partidosJugados == null) partidosJugados = 0L;

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("partidosJugados", partidosJugados + 1);
                        if (victoria) {
                            updates.put("victorias", victorias + 1);
                        } else {
                            updates.put("derrotas", derrotas + 1);
                        }

                        userRef.update(updates);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, getString(R.string.error_al_actualizar_estad_sticas), Toast.LENGTH_SHORT).show());
    }

    /**
     * Extrae el nombre de un jugador
     * @param jugador
     * @return
     */
    private String extraerNombreJugador(Object jugador) {
        if (jugador instanceof String) return (String) jugador;
        if (jugador instanceof Map) {
            Object nombre = ((Map<?, ?>) jugador).get("nombre");
            if (nombre instanceof String) return (String) nombre;
        }
        return null;
    }

    /**
     * Asigna el nombre del jugador a la lista
     * @param i
     * @param nombre
     */
    private void asignarNombreJugadorABCPorIndice(int i, String nombre) {
        switch (i) {
            case 0:
                lblJugadorA1.setText(nombre);
                lblJugadorA2.setText(nombre);
                lblJugadorA3.setText(nombre);
            break;
            case 1:
                lblJugadorB1.setText(nombre);
                lblJugadorB2.setText(nombre);
                break;
            case 2:
                lblJugadorC1.setText(nombre);
                lblJugadorC2.setText(nombre);
            break;
        }
    }

    /**
     * Asigna el nombre del jugador a la lista
     * @param i
     * @param nombre
     */
    private void asignarNombreJugadorXYZPorIndice(int i, String nombre) {
        switch (i) {
            case 0:
                lblJugadorY1.setText(nombre);
                lblJugadorY2.setText(nombre);
            break;
            case 1:
                lblJugadorX1.setText(nombre);
                lblJugadorX2.setText(nombre);
                lblJugadorX3.setText(nombre);
                break;
            case 2:
                lblJugadorZ1.setText(nombre);
                lblJugadorZ2.setText(nombre);
                break;
        }
    }

    /**
     * Inicializa los componentes de la UI
     */
    private void initComponents() {
        mtbActa = findViewById(R.id.mtbActa);
        lblEquipoABC = findViewById(R.id.lblEquipoABC);
        lblEquipoXYZ = findViewById(R.id.lblEquipoXYZ);

        lblJugadorA1 = findViewById(R.id.lblJugadorA1);
        lblSet11 = findViewById(R.id.lblSet11);
        lblSet21 = findViewById(R.id.lblSet21);
        lblSet31 = findViewById(R.id.lblSet31);
        lblSet41 = findViewById(R.id.lblSet41);
        lblSet51 = findViewById(R.id.lblSet51);
        lblJugadorY1 = findViewById(R.id.lblJugadorY1);

        lblJugadorB1 = findViewById(R.id.lblJugadorB1);
        lblSet12 = findViewById(R.id.lblSet12);
        lblSet22 = findViewById(R.id.lblSet22);
        lblSet32 = findViewById(R.id.lblSet32);
        lblSet42 = findViewById(R.id.lblSet42);
        lblSet52 = findViewById(R.id.lblSet52);
        lblJugadorX1 = findViewById(R.id.lblJugadorX1);

        lblJugadorC1 = findViewById(R.id.lblJugadorC1);
        lblSet13 = findViewById(R.id.lblSet13);
        lblSet23 = findViewById(R.id.lblSet23);
        lblSet33 = findViewById(R.id.lblSet33);
        lblSet43 = findViewById(R.id.lblSet43);
        lblSet53 = findViewById(R.id.lblSet53);
        lblJugadorZ1 = findViewById(R.id.lblJugadorZ1);

        lblJugadorA2 = findViewById(R.id.lblJugadorA2);
        lblSet14 = findViewById(R.id.lblSet14);
        lblSet24 = findViewById(R.id.lblSet24);
        lblSet34 = findViewById(R.id.lblSet34);
        lblSet44 = findViewById(R.id.lblSet44);
        lblSet54 = findViewById(R.id.lblSet54);
        lblJugadorX2 = findViewById(R.id.lblJugadorX2);

        lblJugadorC2 = findViewById(R.id.lblJugadorC2);
        lblSet15 = findViewById(R.id.lblSet15);
        lblSet25 = findViewById(R.id.lblSet25);
        lblSet35 = findViewById(R.id.lblSet35);
        lblSet45 = findViewById(R.id.lblSet45);
        lblSet55 = findViewById(R.id.lblSet55);
        lblJugadorY2 = findViewById(R.id.lblJugadorY2);

        lblJugadorB2 = findViewById(R.id.lblJugadorB2);
        lblSet16 = findViewById(R.id.lblSet16);
        lblSet26 = findViewById(R.id.lblSet26);
        lblSet36 = findViewById(R.id.lblSet36);
        lblSet46 = findViewById(R.id.lblSet46);
        lblSet56 = findViewById(R.id.lblSet56);
        lblJugadorZ2 = findViewById(R.id.lblJugadorZ2);

        lblJugadorA3 = findViewById(R.id.lblJugadorA3);
        lblSet17 = findViewById(R.id.lblSet17);
        lblSet27 = findViewById(R.id.lblSet27);
        lblSet37 = findViewById(R.id.lblSet37);
        lblSet47 = findViewById(R.id.lblSet47);
        lblSet57 = findViewById(R.id.lblSet57);
        lblJugadorX3 = findViewById(R.id.lblJugadorX3);

        txtResultado = findViewById(R.id.txtResultado);

        textos = new EditText[]{
                lblJugadorA1, lblSet11, lblSet21, lblSet31, lblSet41, lblSet51, lblJugadorY1,
                lblJugadorB1, lblSet12, lblSet22, lblSet32, lblSet42, lblSet52, lblJugadorX1,
                lblJugadorC1, lblSet13, lblSet23, lblSet33, lblSet43, lblSet53, lblJugadorZ1,
                lblJugadorA2, lblSet14, lblSet24, lblSet34, lblSet44, lblSet54, lblJugadorX2,
                lblJugadorC2, lblSet15, lblSet25, lblSet35, lblSet45, lblSet55, lblJugadorY2,
                lblJugadorB2, lblSet16, lblSet26, lblSet36, lblSet46, lblSet56, lblJugadorZ2,
                lblJugadorA3, lblSet17, lblSet27, lblSet37, lblSet47, lblSet57, lblJugadorX3,
                txtResultado
        };
        idPartido = getIntent().getStringExtra("idDocumentoPartido");
    }
}
