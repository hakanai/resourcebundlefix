package org.trypticon.resourcebundlefix;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class FallbackTest {

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][] {
                // Fallback from es-MX to es-419
                { "org.trypticon.resourcebundlefix.LatinAmericanSpanishFallbackClass", "es-MX", "es-419" },
                { "org/trypticon/resourcebundlefix/LatinAmericanSpanishFallbackProperties", "es-MX", "es-419" },

                // Fallback from es-MX to es
                { "org.trypticon.resourcebundlefix.LanguageOnlyFallbackClass", "es-MX", "es" },
                { "org/trypticon/resourcebundlefix/LanguageOnlyFallbackProperties", "es-MX", "es" },

                // Fallback from es-MX to root locale
                { "org.trypticon.resourcebundlefix.LastResortFallbackClass", "es-MX", "" },
                { "org/trypticon/resourcebundlefix/LastResortFallbackProperties", "es-MX", "" },

                // Case where two options exist, to document current behaviour.
                // Not sure whether this is desirable.
                { "org.trypticon.resourcebundlefix.LatinAmericanSpanishAndLanguageOnlyFallbackClass", "es-MX", "es" },
                { "org/trypticon/resourcebundlefix/LatinAmericanSpanishAndLanguageOnlyFallbackProperties", "es-MX", "es" },
        };
    }

    private final String baseName;
    private final Locale requestedLocale;
    private final Locale expectedLocale;

    public FallbackTest(String baseName, String requestedLanguageTag, String expectedLanguageTag) {
        this.baseName = baseName;
        this.requestedLocale = Locale.forLanguageTag(requestedLanguageTag);
        this.expectedLocale = Locale.forLanguageTag(expectedLanguageTag);
    }

    @Test
    public void testLookup() {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, requestedLocale);
        assertThat(bundle.getLocale(), is(equalTo(expectedLocale)));
        assertThat(bundle.getString("key"), is(equalTo("value")));
    }
}
