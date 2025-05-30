package com.jah.pry_rfatm.Vista.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.jah.pry_rfatm.Logica.ActaManager;
import com.jah.pry_rfatm.Logica.FirestoreLoader;
import com.jah.pry_rfatm.Logica.JugadorManager;
import com.jah.pry_rfatm.Logica.PartidoManager;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UIManager;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

public class ActaActivity extends AppCompatActivity {

    private UIManager uiManager;
    private ActaManager actaManager;
    private JugadorManager jugadorManager;
    private PartidoManager partidoManager;

    private String idPartido;
    private String equipoABCId;
    private String equipoXYZId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acta);

        UtilesUI.configurarStatusBar(this);

        // Inicializar UIManager
        uiManager = new UIManager(this);

        // Inicializar lógica separada
        actaManager = new ActaManager(this, uiManager);
        jugadorManager = new JugadorManager(this, uiManager);
        partidoManager = new PartidoManager(this);

        // Configurar toolbar
        MaterialToolbar mtbActa = findViewById(R.id.mtbActa);
        setSupportActionBar(mtbActa);
        mtbActa.setBackgroundColor(getResources().getColor(R.color.color_fondos));

        // Obtener IDs
        equipoABCId = getIntent().getStringExtra("idLocal");
        equipoXYZId = getIntent().getStringExtra("idVisitante");
        idPartido = getIntent().getStringExtra("idDocumentoPartido");
        actaManager.setIdPartido(idPartido);
        actaManager.crearActaSiNoExiste(); // Crear acta si no existe y el partido está pendiente


        // Cargar datos si existen
        if (equipoABCId != null && equipoXYZId != null) {
            jugadorManager.cargarNombreEquipos(equipoABCId, equipoXYZId);
            jugadorManager.cargarJugadores(equipoABCId, true);
            jugadorManager.cargarJugadores(equipoXYZId, false);
        }

        if (idPartido != null) {
            actaManager.cargarDatosActa();
        }

        SharedPreferences prefs = getSharedPreferences("config", MODE_PRIVATE);
        boolean deshabilitar = prefs.getBoolean("camposDeshabilitados", false);
        if (deshabilitar) actaManager.verEstadoPartidoYDeshabilitarCamposSiEsNecesario();

        // Cargar acta desde Firestore si fue jugado
        FirestoreLoader loader = new FirestoreLoader(idPartido, uiManager);
        loader.cargarActaSiFueJugado();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar_acta, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_guardar) {
            actaManager.guardarActa();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}