import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

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
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        debug { // Asegúrate de que los reports de test estén habilitados para debug
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
            freeCompilerArgs.add("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
        }
    }
}
jacoco {
    toolVersion = "0.8.10"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.threetenabp)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.junit.jupiter.params)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register("jacocoReport", JacocoReport::class) {
    dependsOn("test") // Aseguramos que los unit tests se ejecuten antes

    group = "Reporting"
    description = "Generates JaCoCo coverage report for the Library module."

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    val fileTreeConfig: (ConfigurableFileTree) -> Unit = {
        it.exclude(
            "**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", // Android
            "android/**/*.*",
            "**/*_Factory.class", "**/*_MembersInjector.class", "**/*_Provide*.class" // Dagger/Hilt generated classes (if applicable)
        )
    }

    sourceDirectories.setFrom(files(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin" // Añadido para Kotlin
    ))

    // Rutas de clases compiladas
    val buildDirectory = getLayout().buildDirectory
    classDirectories.setFrom(
        fileTree("${buildDirectory}/intermediates/javac/debug") { fileTreeConfig(this) } +
                fileTree("${buildDirectory}/tmp/kotlin-classes/debug") { fileTreeConfig(this) }
        // Si tienes otros paths de clases compiladas (ej. para variantes específicas), añádelas aquí
    )

    // Rutas de datos de ejecución de JaCoCo
    executionData.setFrom(fileTree(buildDirectory) {
        include("jacoco/testDebugUnitTest.exec") // Para Unit Tests
        // include("outputs/code_coverage/debugAndroidTest/connected_coverage.exec") // Si tienes Instrumented Tests y quieres incluirlos
    })

    // Asegúrate de que los archivos de ejecución existan antes de procesar
    doFirst {
        executionData.setFrom(files(executionData.filter { it.exists() }))
    }
}

tasks {
    register("jacocoFullReport", JacocoReport::class) {
        val jacocoReportTask = this

        group = "Coverage reports"

        val subTasks = getByName("jacocoReport", JacocoReport::class)
        dependsOn(subTasks)

        val subSourceDirs = subTasks.sourceDirectories
        additionalSourceDirs.setFrom(subSourceDirs)
        sourceDirectories.setFrom(subSourceDirs)

        classDirectories.setFrom(files(subTasks.classDirectories))
        executionData.setFrom(files(subTasks.executionData))

        val buildDirectory = getLayout().buildDirectory
        reports {
            html.required = true
            html.outputLocation = file("$buildDirectory/reports/jacoco/html")

            xml.required = true
            xml.outputLocation = file("$buildDirectory/reports/jacoco/jacocoFullReport.xml")
        }

        doFirst {
            executionData.setFrom(files(executionData.filter { it.exists() }))
        }

        coverallsJacoco {
            dependsOn(jacocoReportTask)

            reportPath = "$buildDirectory/reports/jacoco/jacocoFullReport.xml"
            reportSourceSets = subSourceDirs.flatMap { files(it) }
        }
    }
}




// Opcional: Si quieres un reporte JaCoCo para instrumented tests
// tasks.register("jacocoAndroidTestReport", JacocoReport::class) {
//     dependsOn("createDebugCoverageReport") // Esta tarea se genera por AGP para instrumented tests
//     group = "Reporting"
//     description = "Generates JaCoCo coverage report for Android Instrumented Tests."
//
//     reports {
//         xml.required.set(true)
//         html.required.set(true)
//     }
//
//     classDirectories.setFrom(fileTree("${project.buildDir}/intermediates/javac/debug") +
//             fileTree("${project.buildDir}/tmp/kotlin-classes/debug"))
//
//     executionData.setFrom(fileTree(project.buildDir) {
//         include("outputs/code_coverage/debugAndroidTest/connected_coverage.exec")
//     })
//
//     sourceSets.setFrom(files("${project.projectDir}/src/main/java", "${project.projectDir}/src/main/kotlin"))
// }
