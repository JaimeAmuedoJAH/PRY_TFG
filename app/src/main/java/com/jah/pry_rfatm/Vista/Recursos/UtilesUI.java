package com.jah.pry_rfatm.Vista.Recursos;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Clase de utilidades relacionada con la configuración de la interfaz de usuario (UI).
 * Proporciona métodos estáticos para modificar elementos de la UI como la barra de estado.
 */
public class UtilesUI {

    /**
     * Configura la barra de estado (status bar) para una actividad.
     * <p>
     * - Oculta la ActionBar si está presente. <br>
     * - Cambia el color de fondo de la barra de estado a blanco. <br>
     * - Establece iconos oscuros en la barra de estado si el dispositivo tiene Android Marshmallow (API 23) o superior.
     * </p>
     *
     * @param activity La actividad sobre la cual se aplicarán los cambios en la UI.
     */
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
