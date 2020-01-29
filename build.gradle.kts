
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
    testImplementation("org.hamcrest:hamcrest:2.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Test>() {
    // Java 8 forces me to set java.ext.dirs to make the lib get picked up.
    // On the other hand, Java 11 gives an error if you set `java.ext.dirs`.
    if (JavaVersion.current() == JavaVersion.VERSION_1_8) {
        systemProperty("java.ext.dirs", "build/libs");
    }
}
