package com.jah.pry_rfatm.Vista.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

public class ActaActivity extends AppCompatActivity {

    MaterialToolbar mtbBarActa;
    TextView lblEquipoLocal, lblEquipoVis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acta);
        UtilesUI.configurarStatusBar(this);
        initComponents();
        setSupportActionBar(mtbBarActa);
        mtbBarActa.setBackgroundColor(getResources().getColor(R.color.color_fondos));
    }

    /**
     * Crea el menú de opciones en la barra de acción.
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
     * Maneja la selección de elementos del menú de opciones.
     * @param item The menu item that was selected.
     *
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_guardar){
            //Implementar logica para guardar el acta
        }
        return false;
    }

    /**
     * Inicializa los componentes de la interfaz de usuario.
     */
    private void initComponents() {
        mtbBarActa = findViewById(R.id.mtbBarActa);
        lblEquipoLocal = findViewById(R.id.lblEquipoLocal);
        lblEquipoVis = findViewById(R.id.lblEquipoVis);
        String nombreLocal = getIntent().getStringExtra("equipoLocal");
        String nombreVisitante = getIntent().getStringExtra("equipoVisitante");
        lblEquipoLocal.setText(nombreLocal);
        lblEquipoVis.setText(nombreVisitante);
    }
}