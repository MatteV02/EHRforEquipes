plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("kapt") version "2.0.0"
}

group = "com.MatteV02"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)
    //implementation("androidx.compose.material:material-icons-extended")
    implementation("org.hibernate:hibernate-core:6.5.2.Final")
    implementation("org.hibernate:hibernate-validator:8.0.1.Final")
    implementation("org.glassfish:jakarta.el:4.0.2")
    implementation("org.hibernate:hibernate-agroal:6.5.2.Final")
    implementation("io.agroal:agroal-pool:2.5")
    implementation("org.hibernate:hibernate-jpamodelgen:6.5.2.Final")
    kapt("org.hibernate:hibernate-jpamodelgen:6.5.2.Final")
    implementation("com.h2database:h2:2.3.230")

    // https://mvnrepository.com/artifact/io.kotest/kotest-runner-junit5-jvm
    testImplementation("io.kotest:kotest-runner-junit5-jvm:5.9.1")
}

//compose.desktop {
//    application {
//        mainClass = "MainKt"
//
//        nativeDistributions {
//            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
//            packageName = "commonModule"
//            packageVersion = "1.0.0"
//        }
//    }
//}
