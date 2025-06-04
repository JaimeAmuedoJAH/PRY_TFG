package com.jah.pry_rfatm.Aceptacion;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import com.jah.pry_rfatm.Vista.Fragments.RankingFragment;
import com.jah.pry_rfatm.Controlador.FirebaseController;
import com.jah.pry_rfatm.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RankingFragmentAcceptanceTest {

    @Before
    public void setup() {
        FirebaseController.iniciarFirebase(InstrumentationRegistry.getInstrumentation().getTargetContext());
    }

    @Test
    public void recyclerViewIsDisplayed() {
        FragmentScenario<RankingFragment> scenario = FragmentScenario.launchInContainer(RankingFragment.class);

        Espresso.onView(ViewMatchers.withId(R.id.rvRanking))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}