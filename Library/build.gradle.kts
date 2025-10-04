import org.gradle.kotlin.dsl.invoke

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
    kotlinOptions {
        jvmTarget = "17"
    }
}
jacoco {
    toolVersion = "0.8.5" // La versión que sugeriste en la documentación
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
            "**/R.class", "**/R\$*.class", "**/BuildConfig.*", "**/Manifest*.*", // Android
            "android/**/*.*",
            "**/*_Factory.class", "**/*_MembersInjector.class", "**/*_Provide*.class" // Dagger/Hilt generated classes (if applicable)
        )
    }

    sourceDirectories.setFrom(files(
        "${project.projectDir}/src/main/java",
        "${project.projectDir}/src/main/kotlin" // Añadido para Kotlin
    ))

    // Rutas de clases compiladas
    classDirectories.setFrom(
        fileTree("${project.buildDir}/intermediates/javac/debug") { fileTreeConfig(this) } +
                fileTree("${project.buildDir}/tmp/kotlin-classes/debug") { fileTreeConfig(this) }
        // Si tienes otros paths de clases compiladas (ej. para variantes específicas), añádelas aquí
    )

    // Rutas de datos de ejecución de JaCoCo
    executionData.setFrom(fileTree(project.buildDir) {
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

        reports {
            html.required = true
            html.outputLocation = file("$buildDir/reports/jacoco/html")

            xml.required = true
            xml.outputLocation = file("$buildDir/reports/jacoco/jacocoFullReport.xml")
        }

        doFirst {
            executionData.setFrom(files(executionData.filter { it.exists() }))
        }

        coverallsJacoco {
            dependsOn(jacocoReportTask)

            reportPath = "$buildDir/reports/jacoco/jacocoFullReport.xml"
            reportSourceSets = subSourceDirs.flatMap { files(it) }
        }
    }
}


// **Configuración para el plugin de Coveralls:**
// El plugin de Coveralls (asumiendo que es `com.github.klieber.coveralls`)
// automáticamente buscará una tarea JacocoReport y la usará.
// Por defecto, crea una tarea llamada `coverallsJacoco`.
// Solo necesitamos asegurarnos de que `coverallsJacoco` dependa de nuestro `jacocoReport` personalizado.
tasks.named("coverallsJacoco") {
    dependsOn(tasks.named("jacocoReport")) // Asegura que 'jacocoReport' se ejecute antes de coverallsJacoco
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
