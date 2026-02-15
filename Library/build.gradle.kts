import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.android.junit5)
    `maven-publish`
}

android {
    namespace = "com.blipblipcode.library"
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
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

    testOptions.unitTests.apply {
        isReturnDefaultValues = true
        all {
            it.apply {
                testLogging {
                    exceptionFormat = TestExceptionFormat.FULL
                    events =
                        setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.STANDARD_ERROR)
                    showCauses = true
                    showExceptions = true
                    showStackTraces = true
                }

            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}
publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.github.LeandroLCD"
            artifactId = "query"
            version = project.version.toString()
        }
    }
}

afterEvaluate {
    val releaseComponent = components.findByName("release")
    if (releaseComponent != null) {
        publishing {
            publications {
                val pub = getByName("release") as MavenPublication
                pub.from(releaseComponent)
            }
        }
    } else {
        logger.warn("Android 'release' component not found; maven publication won't include component artifacts.")
    }
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
    testRuntimeOnly(libs.junit.platform.launcher)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(kotlin("test"))
}


