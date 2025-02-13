import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.shadow)
}

android {
    namespace = "com.blipblipcode.library"
    compileSdk = 35

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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    afterEvaluate {
        libraryVariants.forEach { variant ->
            variant.packageLibraryProvider?.get()?.apply {
                from(tasks.getByName("shadowJar")) // Incluye el shadowJar en el AAR
            }
        }
    }
}

val embed: Configuration by configurations.creating {
    isCanBeResolved = true // Habilita la resolución
    isCanBeConsumed = false // No se expone a otros proyectos
}
dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    embed(libs.threetenabp)
    implementation(libs.threetenabp)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register("shadowJar", ShadowJar::class) {
    archiveFileName.set("dependency-shadow.jar")
    from(android.sourceSets["main"].java.srcDirs)

    configurations = listOf(embed)
    exclude("com.blipblipcode.library/**")
    mergeServiceFiles()

}

// Ejecuta shadowJar antes de compilar
tasks.named("preBuild") {
    dependsOn("shadowJar")
}