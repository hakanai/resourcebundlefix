
# resourcebundlefix

resourcebundlefix is a JRE extension to work around issues with looking up resource bundles.

You might find it useful if:

* Java's `Locale` class rewrites some locales like Hebrew (he) to the
  legacy equivalent (iw), but you want to name your resource bundle files
  with the standard names instead of the legacy names.

* Java's resource bundle does not implement proper fallback for locales like
  Mexican Spanish (es_MX) but you want it to use the common Latin American
  Spanish (es_419) bundle files without producing a dozen copies of each
  file.

It currently supports Java 8 and Java 11. Other versions between should work
but are not tested. Other versions later than Java 11 might work but have
not been tested.

