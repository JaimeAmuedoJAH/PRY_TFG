package com.jah.pry_rfatm.Rendimiento;

import android.content.Context;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.Vista.Fragments.PerfilFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class PerfilFragmentPerformanceTest {

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseController.iniciarFirebase(context);
    }

    @Test
    public void testCargaLayoutEntrenadorPerformance() {
        long startTime = System.currentTimeMillis();


        FragmentScenario<PerfilFragment> scenario = FragmentScenario.launchInContainer(PerfilFragment.class);


        scenario.onFragment(fragment -> {

        });

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Tiempo en cargar PerfilFragment: " + duration + " ms");

        assert(duration < 2000);
    }
}

