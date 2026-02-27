plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
}

group = "com.inferno"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    // Uncomment when network access to Paper's repository is available:
    // maven("https://repo.papermc.io/repository/maven-public/")
    // maven("https://jitpack.io")
    // maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    // Paper API, VaultAPI, PlaceholderAPI and Citizens are provided by the server at runtime.
    // The local libs/paper-api-stub.jar provides compile-time stubs.
    // When network access is available, replace with:
    //   compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")
    //   compileOnly("me.clip:placeholderapi:2.11.6")
    //   compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    //   compileOnly("net.citizensnpcs:citizens-main:2.0.35-SNAPSHOT")
    compileOnly(fileTree("libs") { include("*.jar") })

    // Bundled runtime dependencies
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.xerial:sqlite-jdbc:3.47.1.0")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release = 21
    }
    processResources {
        filteringCharset = "UTF-8"
    }
}
