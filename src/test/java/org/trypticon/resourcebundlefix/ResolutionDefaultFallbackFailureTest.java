package org.trypticon.resourcebundlefix;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ResolutionDefaultFallbackFailureTest {

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][] {
                { "es-MX" },
        };
    }

    private final Locale requestedLocale;

    public ResolutionDefaultFallbackFailureTest(String requestedLanguageTag) {
        this.requestedLocale = Locale.forLanguageTag(requestedLanguageTag);
    }

    @Test
    public void testLookupFailure() {
        Locale originalLocale = Locale.getDefault();
        try {
            Locale.setDefault(requestedLocale);
            ResourceBundle.getBundle("org.trypticon.resourcebundlefix.ResourceMissing", requestedLocale);
            fail("Expected MissingResourceException");
        } catch (MissingResourceException expected) {
            // Expected.
        } finally {
            Locale.setDefault(originalLocale);
        }
    }
}
