package com.jah.pry_rfatm.Vista.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Modelo.Partido;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

/**
 * Actividad que muestra la información detallada de un partido, incluyendo equipos, fecha y estado.
 * También permite acceder al acta del partido.
 */
public class VerPartidoActivity extends AppCompatActivity {

    MaterialToolbar mtbBarVerPartido;
    ImageView imgEquipoLocal, imgEquipoVisitante;
    TextView lblNombreEquipoLocal, lblNombreEquipoVis, lblLocalizacion, lblFechaHora, lblEstado, lblResultadoPartido;
    Button btnActa;
    String nombreEquipoLocal, nombreEquipoVisitante, fechaHora, idLocal, idVisitante, urlEscudoLocal, urlEscudoVisitante;
    Partido partido;
    String partidoId;

    /**
     * Inicializa la actividad, componentes UI y configura la barra superior.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_partido);
        initComponents();
        UtilesUI.configurarStatusBar(this);
        setSupportActionBar(mtbBarVerPartido);
        mtbBarVerPartido.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        asginarInformacion();

        btnActa.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActaActivity.class);
            intent.putExtra("idLocal", partido.getEquipoLocalId().split("/")[2]);
            intent.putExtra("idVisitante", partido.getEquipoVisitanteId().split("/")[2]);
            intent.putExtra("idDocumentoPartido", partidoId);
            startActivity(intent);
        });
    }

    /**
     * Asigna la información del partido a los elementos visuales.
     * Carga imágenes y datos de los equipos desde Firebase.
     */
    private void asginarInformacion() {
        lblNombreEquipoLocal.setText(nombreEquipoLocal);
        lblNombreEquipoVis.setText(nombreEquipoVisitante);
        lblFechaHora.setText(fechaHora);
        lblEstado.setText(partido.getEstado());
        if(partido.getEstado().equals("pendiente")){
            lblResultadoPartido.setVisibility(View.INVISIBLE);
        }else{
            lblResultadoPartido.setVisibility(View.VISIBLE);
            lblResultadoPartido.setText(partido.getResultado());
        }
        FirebaseController.obtenerEquipoPorId(idLocal, equipoLocal -> {
            lblLocalizacion.setText(equipoLocal.getLocalizacion());
            urlEscudoLocal = equipoLocal.getEscudo();
            FirebaseController.cargarImagenDesdeStorage(imgEquipoLocal, urlEscudoLocal, FirebaseController.imagenPorDefecto);
        }, e -> {

        });

        FirebaseController.obtenerEquipoPorId(idVisitante, equipoVisitante -> {
            urlEscudoVisitante = equipoVisitante.getEscudo();
            FirebaseController.cargarImagenDesdeStorage(imgEquipoVisitante, urlEscudoVisitante, FirebaseController.imagenPorDefecto);
        }, e -> {

        });
    }

    /**
     * Infla el menú en la barra de herramientas.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_registro, menu);
        return true;
    }

    /**
     * Maneja el evento del botón de menú para regresar.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_atras) finish();
        return false;
    }

    /**
     * Inicializa todos los componentes visuales y recupera extras del intent.
     */
    private void initComponents() {
        FirebaseController.iniciarFirebase(this);
        mtbBarVerPartido = findViewById(R.id.mtbBarVerPartido);
        imgEquipoLocal = findViewById(R.id.imgEquipoLocal);
        imgEquipoVisitante = findViewById(R.id.imgEquipoVisitante);
        lblNombreEquipoLocal = findViewById(R.id.lblNombreEquipoLocal);
        lblNombreEquipoVis = findViewById(R.id.lblNombreEquipoVis);
        lblLocalizacion = findViewById(R.id.lblLocalizacion);
        lblFechaHora = findViewById(R.id.lblFechaHora);
        lblEstado = findViewById(R.id.lblEstado);
        lblResultadoPartido = findViewById(R.id.lblResultadoPartido);
        btnActa = findViewById(R.id.btnActa);
        nombreEquipoLocal = getIntent().getStringExtra("nombreLocal");
        nombreEquipoVisitante = getIntent().getStringExtra("nombreVisitante");
        fechaHora = getIntent().getStringExtra("fechaHora");
        idLocal = getIntent().getStringExtra("idLocal");
        idVisitante = getIntent().getStringExtra("idVisitante");
        partido = (Partido) getIntent().getSerializableExtra("partido");
        partidoId = getIntent().getStringExtra("idDocumentoPartido");
    }
}