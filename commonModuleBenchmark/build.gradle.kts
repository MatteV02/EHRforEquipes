import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id ("com.gradleup.shadow") version ("8.3.0")
}

group = "com.MatteV02"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.MatteV02.EHRforEquipes.commonModuleBenchmark.MainKt"
    }
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)

    implementation(project(":commonModule"))

    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")

    implementation("io.github.koalaplot:koalaplot-core:0.6.3")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

compose.desktop {
    application {
        mainClass = "com.MatteV02.EHRforEquipes.commonModuleBenchmark.MainKt"

        nativeDistributions {
            modules("java.instrument", "java.management", "java.naming", "java.rmi", "java.sql", "jdk.unsupported")
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "commonModuleBenchmark"
            packageVersion = "1.0.0"
        }
    }
}
