package org.trypticon.resourcebundlefix;

import java.util.ListResourceBundle;

public class LegacyNameClass_in extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                { "key", "value" },
        };
    }
}
