package com.jah.pry_rfatm.UI;

import static org.junit.Assert.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.Espresso.onView;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Activities.EditarPerfilActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Prueba de interfaz de usuario para verificar que un usuario de tipo "entrenador"
 * ve correctamente los campos relacionados con los jugadores al abrir EditarPerfilActivity.
 */
@RunWith(AndroidJUnit4.class)
public class EditarPerfilUITest {

    private Context appContext;

    /**
     * Prepara el contexto de aplicación antes de cada prueba.
     */
    @Before
    public void setup() {
        appContext = ApplicationProvider.getApplicationContext();
        assertNotNull("El contexto de la aplicación es nulo.", appContext);
    }

    /**
     * Verifica que se muestran los campos de jugadores cuando el usuario es entrenador.
     */
    @Test
    public void testCamposEntrenadorVisibles() {
        // Crea un intent con tipoUsuario = "entrenador"
        Intent intent = new Intent(appContext, EditarPerfilActivity.class);
        intent.putExtra("tipoUsuario", "entrenador");

        try (ActivityScenario<EditarPerfilActivity> scenario = ActivityScenario.launch(intent)) {
            // Verifica que los campos de jugadores están visibles
            onView(withId(R.id.txtJugador1)).check(matches(isDisplayed()));
            onView(withId(R.id.txtJugador2)).check(matches(isDisplayed()));
            onView(withId(R.id.txtJugador3)).check(matches(isDisplayed()));
            onView(withId(R.id.txtJugador4)).check(matches(isDisplayed()));
            onView(withId(R.id.txtJugador5)).check(matches(isDisplayed()));
            onView(withId(R.id.txtJugador6)).check(matches(isDisplayed()));
        }
    }
}
