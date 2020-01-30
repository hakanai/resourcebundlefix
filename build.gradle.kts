
version = "0.1.1"
group = "org.trypticon.resourcebundlefix"
description = "Alternative ResourceBundleControlProvider to work around in JRE Locale class"

plugins {
    `java-library`
    `maven-publish`
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

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

tasks.withType<Test>() {
    // Java 8 forces me to set java.ext.dirs to make the lib get picked up.
    // On the other hand, Java 11 gives an error if you set `java.ext.dirs`.
    if (JavaVersion.current() == JavaVersion.VERSION_1_8) {
        systemProperty("java.ext.dirs", "build/libs");
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${System.getenv("REPOSITORY")}")
            credentials {
                username = project.findProperty("gpr.user")?.toString() ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key")?.toString() ?: System.getenv("PASSWORD")
            }
        }
    }
    publications {
        register("gpr", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
        }
    }
}

