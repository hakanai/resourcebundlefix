
VERSION_NUMBER = '0.1'

repositories.remote << 'https://oss.sonatype.org/content/repositories/releases'

desc 'Alternative ResourceBundleControlProvider to work around in JRE Locale class'
define 'resourcebundlefix' do
  project.version = VERSION_NUMBER
  project.group = 'org.trypticon.resourcebundlefix'
  compile.options.source = compile.options.target = '1.8'
  package :jar
end
