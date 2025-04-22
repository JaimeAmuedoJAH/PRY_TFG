package com.jah.pry_rfatm.Vista.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

public class VerPartidoActivity extends AppCompatActivity {

    MaterialToolbar mtbBarVerPartido;
    ImageView imgEquipoLocal, imgEquipoVisitante;
    TextView lblNombreEquipoLocal, lblNombreEquipoVis, lblLocalizacion, lblFechaHora;
    Button btnActa;
    String nombreEquipoLocal, nombreEquipoVisitante, fechaHora, idLocal, idVisitante, urlEscudoLocal, urlEscudoVisitante;

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
            //ver que datos pasar entre activities
            startActivity(intent);
        });
    }

    private void asginarInformacion() {
        lblNombreEquipoLocal.setText(nombreEquipoLocal);
        lblNombreEquipoVis.setText(nombreEquipoVisitante);
        lblFechaHora.setText(fechaHora);
        FirebaseController.obtenerEquipoPorId(idLocal, equipo -> {
            lblLocalizacion.setText(equipo.getLocalizacion());
            urlEscudoLocal = equipo.getEscudo();
            FirebaseController.cargarImagenDesdeStorage(imgEquipoLocal, urlEscudoLocal, FirebaseController.imagenPorDefecto);
        }, e -> {
            lblLocalizacion.setText("No disponible");
        });

        FirebaseController.obtenerEquipoPorId(idVisitante, equipo -> {
            urlEscudoVisitante = equipo.getEscudo();
            FirebaseController.cargarImagenDesdeStorage(imgEquipoVisitante, urlEscudoVisitante, FirebaseController.imagenPorDefecto);
        }, e -> {
            lblLocalizacion.setText("No disponible");
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bar_registro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_atras) finish();
        return false;
    }

    private void initComponents() {
        FirebaseController.iniciarFirebase(this);
        mtbBarVerPartido = findViewById(R.id.mtbBarVerPartido);
        imgEquipoLocal = findViewById(R.id.imgEquipoLocal);
        imgEquipoVisitante = findViewById(R.id.imgEquipoVisitante);
        lblNombreEquipoLocal = findViewById(R.id.lblNombreEquipoLocal);
        lblNombreEquipoVis = findViewById(R.id.lblNombreEquipoVis);
        lblLocalizacion = findViewById(R.id.lblLocalizacion);
        lblFechaHora = findViewById(R.id.lblFechaHora);
        btnActa = findViewById(R.id.btnActa);
        nombreEquipoLocal = getIntent().getStringExtra("nombreLocal");
        nombreEquipoVisitante = getIntent().getStringExtra("nombreVisitante");
        fechaHora = getIntent().getStringExtra("fechaHora");
        idLocal = getIntent().getStringExtra("idLocal");
        idVisitante = getIntent().getStringExtra("idVisitante");
    }
}