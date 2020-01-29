package org.trypticon.resourcebundlefix;

import java.util.ListResourceBundle;

public class LastResortFallbackClass extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                { "key", "value" },
        };
    }
}
