package org.trypticon.resourcebundlefix;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.spi.ResourceBundleControlProvider;

import com.google.auto.service.AutoService;

/**
 * Implementation of Java 8 {@link ResourceBundleControlProvider} with fixes to work around JRE issues.
 */
@AutoService(ResourceBundleControlProvider.class)
public class ResourceBundleControlProviderImpl implements ResourceBundleControlProvider {
    private static final ResourceBundle.Control INSTANCE = new ResourceBundleControlImpl();

    @Override
    public ResourceBundle.Control getControl(String baseName) {
        return INSTANCE;
    }

    private static class ResourceBundleControlImpl extends ResourceBundle.Control {
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            if ("es".equals(locale.getLanguage())) {
                switch (locale.getCountry()) {
                    // CLDR 28 cross-referenced with Wikipedia :/
                    case "AR": // Argentina
                    case "BO": // Bolivia
                    case "CL": // Chile
                    case "CO": // Colombia
                    case "CR": // Costa Rica
                    case "CU": // Cuba
                    case "DO": // Dominican Republic
                    case "EC": // Ecuador
                    case "SV": // El Salvador
                    case "GT": // Guatemala
                    case "GN": // Honduras
                    case "MX": // Mexico
                    case "NI": // Nicaragua
                    case "PA": // Panama
                    case "PY": // Paraguay
                    case "PE": // Peru
                    case "PR": // Puerto Rico
                    case "UY": // Uruguay
                    case "US": // United States
                    case "VE": // Venezuela
                        return new Locale("es", "419", locale.getVariant());

                    default:
                        // Nothing. Calling superclass constructor would cause infinite loop...
                        return null;
                }
            }

            return super.getFallbackLocale(baseName, locale);
        }

        // Overriding locale-related methods is pointless, because Java's Locale class will rewrite newer
        // locale codes to the deprecated names.
        // Overriding toBundleName is also pointless, because some people might be working around the issue
        // by shipping their bundles with the deprecated language suffix, so they wouldn't be able to look theirs up.
        // Therefore, overriding newBundle is the only option.
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
            throws IllegalAccessException, InstantiationException, IOException
        {
            // Here we have to use AltLocale, because Locale rewrites language codes to the deprecated form,
            // making the workaround not work at all.
            AltLocale altLocale = AltLocale.forLocale(locale);
            String language = altLocale.getLanguage();
            String standardLanguage = null;
            switch (language) {
                case "iw":
                    standardLanguage = "he";
                    break;
                case "ji":
                    standardLanguage = "yi";
                    break;
                case "in":
                    standardLanguage = "id";
                    break;
            }

            if (standardLanguage != null) {
                // Try the standard name first (reward people who are doing it right.)
                AltLocale standardLocale = new AltLocale(standardLanguage, altLocale.getCountry(), altLocale.getVariant(), altLocale.getScript());
                try {
                    ResourceBundle result = superNewBundle(baseName, standardLocale, format, loader, reload);

                    if (result != null) {
                        return result;
                    }
                } catch (Exception e) {
                    // Fall through to try the next way. It isn't particularly obvious whether nonexistent bundles will return null
                    // or throw an error, so maybe both are possible.
                }
            }

            return superNewBundle(baseName, altLocale, format, loader, reload);
        }

        // Copy of the superclass implementation of newBundle, but taking AltLocale instead.
        private ResourceBundle superNewBundle(String baseName, AltLocale locale, String format, ClassLoader loader, boolean reload) 
                throws IllegalAccessException, InstantiationException, IOException {

            String bundleName = toBundleName(baseName, locale);
            ResourceBundle bundle = null;
            if ("java.class".equals(format)) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends ResourceBundle> bundleClass = (Class<? extends ResourceBundle>) loader.loadClass(bundleName);

                    // If the class isn't a ResourceBundle subclass, throw a ClassCastException.
                    if (ResourceBundle.class.isAssignableFrom(bundleClass)) {
                        bundle = bundleClass.newInstance();
                    } else {
                        throw new ClassCastException(bundleClass.getName() + " cannot be cast to ResourceBundle");
                    }
                } catch (ClassNotFoundException e) {
                    // Ignored
                }
            } else if ("java.properties".equals(format)) {
                String resourceName = toResourceName0(bundleName, "properties");
                if (resourceName == null) {
                    return null;
                }
                InputStream stream;
                try {
                    stream = AccessController.doPrivileged((PrivilegedExceptionAction<InputStream>) () -> {
                        InputStream is = null;
                        if (reload) {
                            URL url = loader.getResource(resourceName);
                            if (url != null) {
                                URLConnection connection = url.openConnection();
                                if (connection != null) {
                                    // Disable caches to get fresh data for reloading.
                                    connection.setUseCaches(false);
                                    is = connection.getInputStream();
                                }
                            }
                        } else {
                            // It's the caller's job to close this.
                            //noinspection IOResourceOpenedButNotSafelyClosed
                            is = loader.getResourceAsStream(resourceName);
                        }
                        return is;
                    });
                } catch (PrivilegedActionException e) {
                    //noinspection ThrowInsideCatchBlockWhichIgnoresCaughtException
                    throw (IOException) e.getException();
                }
                if (stream != null) {
                    try {
                        bundle = new PropertyResourceBundle(stream);
                    } finally {
                        stream.close();
                    }
                }
            } else {
                throw new IllegalArgumentException("unknown format: " + format);
            }
            return bundle;
        }

        // Copy of the superclass implementation of toBundleName, but taking AltLocale instead.
        // String and object reference equality used within - this is faithful to the original, but dubious.
        @SuppressWarnings({ "ObjectEquality", "StringEquality" })
        public String toBundleName(String baseName, AltLocale locale) {
            if (locale == AltLocale.ROOT) {
                return baseName;
            }

            String language = locale.getLanguage();
            String script = locale.getScript();
            String country = locale.getCountry();
            String variant = locale.getVariant();

            if (language == "" && country == "" && variant == "") {
                return baseName;
            }

            StringBuilder sb = new StringBuilder(baseName);
            sb.append('_');
            if (script != "") {
                if (variant != "") {
                    sb.append(language).append('_').append(script).append('_').append(country).append('_').append(variant);
                } else if (country != "") {
                    sb.append(language).append('_').append(script).append('_').append(country);
                } else {
                    sb.append(language).append('_').append(script);
                }
            } else {
                if (variant != "") {
                    sb.append(language).append('_').append(country).append('_').append(variant);
                } else if (country != "") {
                    sb.append(language).append('_').append(country);
                } else {
                    sb.append(language);
                }
            }
            return sb.toString();
        }

        // Copy of identical method from superclass because we can't call it directly.
        private String toResourceName0(String bundleName, String suffix) {
            // application protocol check
            if (bundleName.contains("://")) {
                return null;
            } else {
                return toResourceName(bundleName, suffix);
            }
        }


    }

    /**
     * Alternative locale class with just enough for what we need.
     */
    private static class AltLocale {
        private static final AltLocale ROOT = new AltLocale("", "", "", "");

        private final String language;
        private final String country;
        private final String variant;
        private final String script;

        private AltLocale(String language, String country, String variant, String script) {
            this.language = language;
            this.country = country;
            this.variant = variant;
            this.script = script;
        }

        private static AltLocale forLocale(Locale locale) {
            return new AltLocale(locale.getLanguage(), locale.getCountry(), locale.getVariant(), locale.getScript());
        }

        private String getLanguage() {
            return language;
        }

        private String getCountry() {
            return country;
        }

        private String getVariant() {
            return variant;
        }

        private String getScript() {
            return script;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            if (script != "") {
                if (variant != "") {
                    sb.append(language).append('_').append(script).append('_').append(country).append('_').append(variant);
                } else if (country != "") {
                    sb.append(language).append('_').append(script).append('_').append(country);
                } else {
                    sb.append(language).append('_').append(script);
                }
            } else {
                if (variant != "") {
                    sb.append(language).append('_').append(country).append('_').append(variant);
                } else if (country != "") {
                    sb.append(language).append('_').append(country);
                } else {
                    sb.append(language);
                }
            }
            return sb.toString();
        }
    }
}
