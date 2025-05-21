package com.jah.pry_rfatm.UI;

import static androidx.test.espresso.matcher.ViewMatchers.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.Espresso.onView;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.jah.pry_rfatm.Modelo.Partido;
import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Activities.VerPartidoActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Prueba de UI para verificar que VerPartidoActivity muestra correctamente los elementos clave.
 */
@RunWith(AndroidJUnit4.class)
public class VerPartidoActivityUITest {

    @Test
    public void testCargarVistaVerPartido() {
        // Crea datos ficticios para el intent
        Intent intent = new Intent();
        intent.putExtra("nombreLocal", "Equipo A");
        intent.putExtra("nombreVisitante", "Equipo B");
        intent.putExtra("fechaHora", "2025-05-16 12:00");
        intent.putExtra("idLocal", "id_local_test");
        intent.putExtra("idVisitante", "id_visitante_test");

        Partido partido = new Partido();
        partido.setEquipoLocalId("equipos/id_local_test");
        partido.setEquipoVisitanteId("equipos/id_visitante_test");
        partido.setEstado("pendiente");
        partido.setResultado("0-0");
        intent.putExtra("partido", partido);

        // Lanza la actividad con los datos
        try (ActivityScenario<VerPartidoActivity> scenario = ActivityScenario.launch(intent.setClassName(
                "com.jah.pry_rfatm",
                "com.jah.pry_rfatm.Vista.Activities.VerPartidoActivity"
        ))) {
            // Comprueba que los TextView principales están visibles
            onView(withId(R.id.lblNombreEquipoLocal)).check(matches(isDisplayed()));
            onView(withId(R.id.lblNombreEquipoVis)).check(matches(isDisplayed()));
            onView(withId(R.id.lblFechaHora)).check(matches(withText("2025-05-16 12:00")));
            onView(withId(R.id.lblEstado)).check(matches(withText("pendiente")));
            onView(withId(R.id.btnActa)).check(matches(isDisplayed()));
        }
    }
}

