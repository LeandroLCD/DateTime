import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.gradle.testing.jacoco.tasks.JacocoReport

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.coveralls)
    id("jacoco")
}

android {
    namespace = "com.blipblipcode.library"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments.put("useTestStorageService", "true")
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            // Necesario para generar cobertura en pruebas instrumentadas
            enableAndroidTestCoverage = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}

jacoco {
    toolVersion = "0.8.10"
}
tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)


    // JUnit Jupiter dependencies for unit testing
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

/**
 * Genera reporte JaCoCo a partir de las pruebas instrumentadas (androidTest)
 */

tasks.register<JacocoReport>("jacocoAndroidTestReport") {
    dependsOn("connectedDebugAndroidTest")

    group = "Reporting"
    description = "Generates JaCoCo coverage report for Android Instrumented Tests."

    val buildDir = layout.buildDirectory

    reports {
        xml.required.set(true)
        html.required.set(true)
        html.outputLocation.set(file("$buildDir/reports/jacoco/html"))
        xml.outputLocation.set(file("$buildDir/reports/jacoco/jacocoAndroidTestReport.xml"))
    }

    // Directorios de código fuente
    sourceDirectories.setFrom(
        files(
            "src/main/java",
            "src/main/kotlin"
        )
    )

    // Clases compiladas del build de debug
    classDirectories.setFrom(
        files(
            fileTree("${buildDir.get()}/intermediates/javac/debug") {
                exclude("**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*")
            },
            fileTree("${buildDir.get()}/tmp/kotlin-classes/debug") {
                exclude("**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*")
            }
        )
    )

    // Archivo de ejecución JaCoCo generado por las pruebas instrumentadas
    executionData.setFrom(
        fileTree(buildDir) {
            include("**/outputs/code_coverage/*AndroidTest/connected_coverage*.ec*")
        }
    )

    doFirst {
        executionData.setFrom(files(executionData.filter { it.exists() }))
    }
}
/**
 * Enlaza el reporte JaCoCo con Coveralls
 */
tasks.named("coverallsJacoco") {
    dependsOn("jacocoAndroidTestReport")
}

