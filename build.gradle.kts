
version = "0.1.1"
group = "org.trypticon.resourcebundlefix"
description = "Alternative ResourceBundleControlProvider to work around in JRE Locale class"

plugins {
    `java-library`
}

repositories {
    jcenter()
}

dependencies {
    compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc6")
    annotationProcessor("com.google.auto.service:auto-service:1.0-rc6")
    testImplementation("junit:junit:4.12")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

