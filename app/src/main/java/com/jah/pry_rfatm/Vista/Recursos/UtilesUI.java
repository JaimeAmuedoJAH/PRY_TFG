package com.jah.pry_rfatm.Vista.Recursos;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;

public class UtilesUI {

    public static void configurarStatusBar(AppCompatActivity activity) {
        // Oculta la ActionBar si está presente
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().hide();
        }

        // Configura la status bar (barra superior)
        Window window = activity.getWindow();
        window.setStatusBarColor(Color.WHITE); // Fondo blanco

        // Íconos oscuros si el sistema lo permite
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = window.getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
