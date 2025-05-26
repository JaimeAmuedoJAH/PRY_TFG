package com.jah.pry_rfatm.Funcionalidad;

import android.content.Context;
import android.widget.EditText;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jah.pry_rfatm.Vista.Activities.LogInActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LogInValidationTest {

    private LogInActivity activity;

    @Before
    public void setup() {
        Context context = ApplicationProvider.getApplicationContext();
        activity = new LogInActivity();

        // Inicializar componentes manualmente (sin onCreate)
        activity.txtCorreo = new EditText(context);
        activity.txtPass = new EditText(context);
    }

    @Test
    public void iniciarSesion_conCorreoVacio_muestraError() {
        activity.txtCorreo.setText("");
        activity.txtPass.setText("123456");

        activity.iniciarSesionConCorreo();

        assertEquals("El correo es obligatorio", activity.txtCorreo.getError());
    }

    @Test
    public void iniciarSesion_conPassVacio_muestraError() {
        activity.txtCorreo.setText("usuario@ejemplo.com");
        activity.txtPass.setText("");

        activity.iniciarSesionConCorreo();

        assertEquals("La contraseña es obligatoria", activity.txtPass.getError());
    }
}

