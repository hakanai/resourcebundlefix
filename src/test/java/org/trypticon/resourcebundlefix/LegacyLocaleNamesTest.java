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
public class LegacyLocaleNamesTest {

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][] {
                { "org.trypticon.resourcebundlefix.LegacyNameClass", "he", "iw" },
                { "org.trypticon.resourcebundlefix.LegacyNameClass", "iw", "iw" },
                { "org.trypticon.resourcebundlefix.StandardNameClass", "he", "iw" },
                { "org.trypticon.resourcebundlefix.StandardNameClass", "iw", "iw" },
                { "org.trypticon.resourcebundlefix.LegacyNameProperties", "he", "iw" },
                { "org.trypticon.resourcebundlefix.LegacyNameProperties", "iw", "iw" },
                { "org.trypticon.resourcebundlefix.StandardNameProperties", "he", "iw" },
                { "org.trypticon.resourcebundlefix.StandardNameProperties", "iw", "iw" },

                { "org.trypticon.resourcebundlefix.StandardNameClass", "ji", "ji" },
                { "org.trypticon.resourcebundlefix.StandardNameClass", "in", "in" },
        };
    }

    private final String baseName;
    private final Locale requestedLocale;
    private final Locale expectedLocale;

    public LegacyLocaleNamesTest(String baseName, String requestedLanguageTag, String expectedLanguageTag) {
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
