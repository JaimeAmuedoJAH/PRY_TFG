package com.jah.pry_rfatm.Usabilidad;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.jah.pry_rfatm.R;
import com.jah.pry_rfatm.Vista.Activities.LogInActivity;
import com.jah.pry_rfatm.Vista.Activities.RegistroActivity;
import com.jah.pry_rfatm.Vista.Activities.ResetPasswordActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LogInUsabilidadTest {

    @Rule
    public IntentsTestRule<LogInActivity> intentsRule = new IntentsTestRule<>(LogInActivity.class);

    @Test
    public void elementosPrincipalesVisiblesYHabilitados() {
        onView(withId(R.id.txtCorreo)).check(matches(isDisplayed()));
        onView(withId(R.id.txtPass)).check(matches(isDisplayed()));
        onView(withId(R.id.btnIni)).check(matches(isDisplayed())).check(matches(isEnabled()));
        onView(withId(R.id.btnRegistrar)).check(matches(isDisplayed())).check(matches(isEnabled()));
        onView(withId(R.id.btnIniGoogle)).check(matches(isDisplayed())).check(matches(isEnabled()));
        onView(withId(R.id.lblRecuperar)).check(matches(isDisplayed())).check(matches(isEnabled()));
    }

    @Test
    public void botonRegistrarAbreRegistroActivity() {
        onView(withId(R.id.btnRegistrar)).perform(click());
        intended(hasComponent(RegistroActivity.class.getName()));
    }

    @Test
    public void labelRecuperarAbreResetPasswordActivity() {
        onView(withId(R.id.lblRecuperar)).perform(click());
        intended(hasComponent(ResetPasswordActivity.class.getName()));
    }

    @Test
    public void ingresarTextoEnCorreo() {
        String email = "usuario@example.com";

        onView(withId(R.id.txtCorreo))
                .perform(typeText(email), closeSoftKeyboard());

        onView(withId(R.id.txtCorreo))
                .check(matches(withText(email)));
    }
}
