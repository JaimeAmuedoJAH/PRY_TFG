package com.jah.pry_rfatm.Rendimiento;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.jah.pry_rfatm.Vista.Fragments.RankingFragment;
import com.jah.pry_rfatm.Controlador.FirebaseController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RankingFragmentPerformanceTest {

    @Before
    public void setup() {
        FirebaseController.iniciarFirebase(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void testCargaLayoutPerformance() {
        long startTime = System.currentTimeMillis();

        FragmentScenario<RankingFragment> scenario = FragmentScenario.launchInContainer(RankingFragment.class);

        scenario.onFragment(fragment -> {
            // Aquí ya el fragment está creado, podemos medir el tiempo transcurrido
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.println("Tiempo en cargar RankingFragment: " + duration + " ms");

            // Por ejemplo, establecemos que debe tardar menos de 2 segundos (2000 ms)
            assertTrue("Carga demasiado lenta: " + duration + " ms", duration < 2000);
        });
    }
}
