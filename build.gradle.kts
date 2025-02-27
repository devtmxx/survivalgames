plugins {
    java
}

group = "de.tmxx"
version = "1.0.0"

repositories {
    mavenCentral()

    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    // lombok
    compileOnly("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")

    // papermc
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    // google guice
    compileOnly("com.google.inject:guice:7.0.0")
    compileOnly("com.google.inject.extensions:guice-assistedinject:7.0.0")

    // database
    compileOnly("org.apache.commons:commons-dbcp2:2.13.0")
    compileOnly("com.mysql:mysql-connector-j:9.2.0")

    // io utils
    compileOnly("commons-io:commons-io:2.18.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}