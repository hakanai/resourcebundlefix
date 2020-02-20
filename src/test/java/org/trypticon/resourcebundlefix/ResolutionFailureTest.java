package org.trypticon.resourcebundlefix;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

import static org.junit.Assert.fail;

@RunWith(Parameterized.class)
public class ResolutionFailureTest {

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][] {
                { "he-IL" },
                { "es-MX" },
        };
    }

    private final Locale requestedLocale;

    public ResolutionFailureTest(String requestedLanguageTag) {
        this.requestedLocale = Locale.forLanguageTag(requestedLanguageTag);
    }

    @Test
    public void testLookupFailure() {
        try {
            ResourceBundle.getBundle("org.trypticon.resourcebundlefix.ResourceMissing", requestedLocale);
            fail("Expected MissingResourceException");
        } catch (MissingResourceException expected) {
            // Expected.
        }
    }
}
