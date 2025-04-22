package com.jah.pry_rfatm.Vista.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Fragments.ClasificacionFragment;
import com.jah.pry_rfatm.Vista.Fragments.InicioFragment;
import com.jah.pry_rfatm.Vista.Fragments.PerfilFragment;
import com.jah.pry_rfatm.Vista.Fragments.RankingFragment;
import com.jah.pry_rfatm.Vista.Recursos.UtilesUI;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UtilesUI.configurarStatusBar(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.item_inicio) cargarFragment(new InicioFragment());
            if(item.getItemId() == R.id.item_clasificacion) cargarFragment(new ClasificacionFragment());
            if(item.getItemId() == R.id.item_ranking) cargarFragment(new RankingFragment());
            if(item.getItemId() == R.id.item_perfil) cargarFragment(new PerfilFragment());
            return true;
        });
        cargarFragment(new InicioFragment());
    }

    private void cargarFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameContainer, fragment).commit();
    }
}