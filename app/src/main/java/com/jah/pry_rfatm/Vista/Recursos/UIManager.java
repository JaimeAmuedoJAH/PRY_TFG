package com.jah.pry_rfatm.Vista.Recursos;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import com.jah.pry_rfatm.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIManager {
    private final Activity activity;

    public UIManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * Deshabilita todos los campos de la UI.
     */
    public void deshabilitarCampos() {
        for (EditText campo : obtenerTodosLosCampos()) {
            campo.setEnabled(false);
            campo.setFocusable(false);
            campo.setClickable(false);
            campo.setCursorVisible(false);
        }
    }

    /**
     * Establece el resultado final en el campo correspondiente.
     * @param resultado
     */
    public void setResultadoFinal(String resultado) {
        getCampo(R.id.txtResultado).setText(resultado);
    }

    /**
     * Establece el nombre del equipo en el campo correspondiente.
     * @param esABC
     * @param nombre
     */
    public void setNombreEquipo(boolean esABC, String nombre) {
        int id = esABC ? R.id.lblEquipoABC : R.id.lblEquipoXYZ;
        ((TextView) activity.findViewById(id)).setText(nombre);
    }

    /**
     * Establece el nombre del jugador en el campo correspondiente.
     * @param esABC
     * @param i
     * @param nombre
     */
    public void setNombreJugador(boolean esABC, int i, String nombre) {
        if (esABC) {
            switch (i) {
                case 0:
                    getCampo(R.id.lblJugadorA1).setText(nombre);
                    getCampo(R.id.lblJugadorA2).setText(nombre);
                    getCampo(R.id.lblJugadorA3).setText(nombre);
                    break;
                case 1:
                    getCampo(R.id.lblJugadorB1).setText(nombre);
                    getCampo(R.id.lblJugadorB2).setText(nombre);
                    break;
                case 2:
                    getCampo(R.id.lblJugadorC1).setText(nombre);
                    getCampo(R.id.lblJugadorC2).setText(nombre);
                    break;
            }
        } else {
            switch (i) {
                case 0:
                    getCampo(R.id.lblJugadorY1).setText(nombre);
                    getCampo(R.id.lblJugadorY2).setText(nombre);
                    break;
                case 1:
                    getCampo(R.id.lblJugadorX1).setText(nombre);
                    getCampo(R.id.lblJugadorX2).setText(nombre);
                    getCampo(R.id.lblJugadorX3).setText(nombre);
                    break;
                case 2:
                    getCampo(R.id.lblJugadorZ1).setText(nombre);
                    getCampo(R.id.lblJugadorZ2).setText(nombre);
                    break;
            }
        }
    }

    /**
     * Obtiene el acta de la UI.
     * @return
     */
    public Map<String, Object> obtenerActaDeUI() {
        Map<String, Object> actaMap = new HashMap<>();

        actaMap.put("partidoAY", crearPartido(R.id.lblJugadorA1, R.id.lblJugadorY1, R.id.lblSet11, R.id.lblSet21, R.id.lblSet31, R.id.lblSet41, R.id.lblSet51));
        actaMap.put("partidoBX", crearPartido(R.id.lblJugadorB1, R.id.lblJugadorX1, R.id.lblSet12, R.id.lblSet22, R.id.lblSet32, R.id.lblSet42, R.id.lblSet52));
        actaMap.put("partidoCZ", crearPartido(R.id.lblJugadorC1, R.id.lblJugadorZ1, R.id.lblSet13, R.id.lblSet23, R.id.lblSet33, R.id.lblSet43, R.id.lblSet53));
        actaMap.put("partidoAY2", crearPartido(R.id.lblJugadorA2, R.id.lblJugadorY2, R.id.lblSet14, R.id.lblSet24, R.id.lblSet34, R.id.lblSet44, R.id.lblSet54));
        actaMap.put("partidoCX", crearPartido(R.id.lblJugadorC2, R.id.lblJugadorX2, R.id.lblSet15, R.id.lblSet25, R.id.lblSet35, R.id.lblSet45, R.id.lblSet55));
        actaMap.put("partidoBZ", crearPartido(R.id.lblJugadorB2, R.id.lblJugadorZ2, R.id.lblSet16, R.id.lblSet26, R.id.lblSet36, R.id.lblSet46, R.id.lblSet56));
        actaMap.put("partidoAX2", crearPartido(R.id.lblJugadorA3, R.id.lblJugadorX3, R.id.lblSet17, R.id.lblSet27, R.id.lblSet37, R.id.lblSet47, R.id.lblSet57));

        return actaMap;
    }

    /**
     * Crea un partido en el acta.
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
            if (setsGanadosABC == 3 || setsGanadosXYZ == 3) {
                // Rellenar con ceros el resto
                setsABC.add("0");
                setsXYZ.add("0");
                continue;
            }

            String texto = getTexto(id);
            if (texto.contains("-")) {
                String[] partes = texto.split("-");
                if (partes.length == 2) {
                    try {
                        int puntosABC = Integer.parseInt(partes[0].trim());
                        int puntosXYZ = Integer.parseInt(partes[1].trim());

                        setsABC.add(String.valueOf(puntosABC));
                        setsXYZ.add(String.valueOf(puntosXYZ));

                        if (puntosABC == 11 && puntosXYZ < 11) setsGanadosABC++;
                        else if (puntosXYZ == 11 && puntosABC < 11) setsGanadosXYZ++;
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
     * Carga los datos del acta desde Firestore.
     * @param actaData
     */
    public void cargarPartidosDesdeActa(Map<String, Object> actaData) {
        if (actaData == null) return;

        Map<String, int[]> mapeo = new HashMap<>();
        mapeo.put("partidoAY", new int[]{R.id.lblJugadorA1, R.id.lblJugadorY1, R.id.lblSet11, R.id.lblSet21, R.id.lblSet31, R.id.lblSet41, R.id.lblSet51});
        mapeo.put("partidoBX", new int[]{R.id.lblJugadorB1, R.id.lblJugadorX1, R.id.lblSet12, R.id.lblSet22, R.id.lblSet32, R.id.lblSet42, R.id.lblSet52});
        mapeo.put("partidoCZ", new int[]{R.id.lblJugadorC1, R.id.lblJugadorZ1, R.id.lblSet13, R.id.lblSet23, R.id.lblSet33, R.id.lblSet43, R.id.lblSet53});
        mapeo.put("partidoAY2", new int[]{R.id.lblJugadorA2, R.id.lblJugadorY2, R.id.lblSet14, R.id.lblSet24, R.id.lblSet34, R.id.lblSet44, R.id.lblSet54});
        mapeo.put("partidoCX", new int[]{R.id.lblJugadorC2, R.id.lblJugadorX2, R.id.lblSet15, R.id.lblSet25, R.id.lblSet35, R.id.lblSet45, R.id.lblSet55});
        mapeo.put("partidoBZ", new int[]{R.id.lblJugadorB2, R.id.lblJugadorZ2, R.id.lblSet16, R.id.lblSet26, R.id.lblSet36, R.id.lblSet46, R.id.lblSet56});
        mapeo.put("partidoAX2", new int[]{R.id.lblJugadorA3, R.id.lblJugadorX3, R.id.lblSet17, R.id.lblSet27, R.id.lblSet37, R.id.lblSet47, R.id.lblSet57});

        for (Map.Entry<String, int[]> entry : mapeo.entrySet()) {
            String clavePartido = entry.getKey();
            int[] ids = entry.getValue();

            if (actaData.containsKey(clavePartido)) {
                Map<String, Object> partido = (Map<String, Object>) actaData.get(clavePartido);

                String jugadorABC = (String) partido.get("jugadorABC");
                String jugadorXYZ = (String) partido.get("jugadorXYZ");
                String setsABCStr = (String) partido.get("setsABC");
                String setsXYZStr = (String) partido.get("setsXYZ");

                // Seteamos los nombres de jugadores
                getCampo(ids[0]).setText(jugadorABC != null ? jugadorABC : "");
                getCampo(ids[1]).setText(jugadorXYZ != null ? jugadorXYZ : "");

                // Seteamos los sets (máximo 5)
                String[] setsABC = setsABCStr != null ? setsABCStr.split("-") : new String[0];
                String[] setsXYZ = setsXYZStr != null ? setsXYZStr.split("-") : new String[0];

                for (int i = 0; i < 5; i++) {
                    String textoSet = "0-0";
                    if (i < setsABC.length && i < setsXYZ.length) {
                        textoSet = setsABC[i] + "-" + setsXYZ[i];
                    }
                    getCampo(ids[i + 2]).setText(textoSet);
                }
            }
        }

        // Finalmente, si hay resultado final
        if (actaData.containsKey("resultadoFinal")) {
            setResultadoFinal((String) actaData.get("resultadoFinal"));
        }
    }

    /**
     * Obtiene un campo de la UI.
     * @param id
     * @return
     */
    private EditText getCampo(int id) {
        return activity.findViewById(id);
    }

    /**
     * Obtiene el texto de un campo.
     * @param id
     * @return
     */
    private String getTexto(int id) {
        EditText campo = getCampo(id);
        return campo != null ? campo.getText().toString().trim() : "";
    }

    /**
     * Obtiene todos los campos de la UI.
     * @return
     */
    private List<EditText> obtenerTodosLosCampos() {
        List<EditText> campos = new ArrayList<>();
        int[] ids = {
                R.id.lblJugadorA1, R.id.lblSet11, R.id.lblSet21, R.id.lblSet31, R.id.lblSet41, R.id.lblSet51, R.id.lblJugadorY1,
                R.id.lblJugadorB1, R.id.lblSet12, R.id.lblSet22, R.id.lblSet32, R.id.lblSet42, R.id.lblSet52, R.id.lblJugadorX1,
                R.id.lblJugadorC1, R.id.lblSet13, R.id.lblSet23, R.id.lblSet33, R.id.lblSet43, R.id.lblSet53, R.id.lblJugadorZ1,
                R.id.lblJugadorA2, R.id.lblSet14, R.id.lblSet24, R.id.lblSet34, R.id.lblSet44, R.id.lblSet54, R.id.lblJugadorY2,
                R.id.lblJugadorC2, R.id.lblSet15, R.id.lblSet25, R.id.lblSet35, R.id.lblSet45, R.id.lblSet55, R.id.lblJugadorX2,
                R.id.lblJugadorB2, R.id.lblSet16, R.id.lblSet26, R.id.lblSet36, R.id.lblSet46, R.id.lblSet56, R.id.lblJugadorZ2,
                R.id.lblJugadorA3, R.id.lblSet17, R.id.lblSet27, R.id.lblSet37, R.id.lblSet47, R.id.lblSet57, R.id.lblJugadorX3,
                R.id.txtResultado
        };
        for (int id : ids) {
            EditText campo = getCampo(id);
            if (campo != null) campos.add(campo);
        }
        return campos;
    }
}
