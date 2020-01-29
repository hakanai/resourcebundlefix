package org.trypticon.resourcebundlefix;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class LegacyLocaleNamesTest {

    @Parameterized.Parameters
    public static Object[][] data() {
        Object[] bundleNames = {
                "org.trypticon.resourcebundlefix.LastResortFallbackClass",
                "org.trypticon.resourcebundlefix.LegacyNameClass",
                "org.trypticon.resourcebundlefix.StandardNameClass",
                "org/trypticon/resourcebundlefix/LastResortFallbackProperties",
                "org/trypticon/resourcebundlefix/LegacyNameProperties",
                "org/trypticon/resourcebundlefix/StandardNameProperties",
        };

        Object[] locales = {
                "he-IL", // standard name
                "iw-IL", // legacy name
                "yi-IL", // standard name
                "ji-IL", // legacy name
                "id-ID", // standard name
                "in-ID", // legacy name
        };

        Stream.Builder<Object[]> builder = Stream.builder();
        for (int i = 0; i < bundleNames.length; i++)
        {
            for (int j = 0; j < locales.length; j++)
            {
                builder.add(new Object[] { bundleNames[i], locales[j] });
            }
        }
        return builder.build().toArray(Object[][]::new);
    }

    private final String baseName;
    private final Locale locale;

    public LegacyLocaleNamesTest(String baseName, String languageTag) {
        this.baseName = baseName;
        this.locale = Locale.forLanguageTag(languageTag);
    }

    @Test
    public void testLookup() {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        assertThat(bundle.getString("key"), is(equalTo("value")));
    }
}
