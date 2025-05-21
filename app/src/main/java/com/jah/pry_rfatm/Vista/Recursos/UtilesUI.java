package com.jah.pry_rfatm.Vista.Recursos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;

/**
 * Clase de utilidades relacionada con la configuración de la interfaz de usuario (UI).
 * Proporciona métodos estáticos para modificar elementos de la UI como la barra de estado.
 */
public class UtilesUI {

    /**
     * Configura la barra de estado (status bar) para una actividad.
     * @param activity
     */
    public static void configurarStatusBar(AppCompatActivity activity) {
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
