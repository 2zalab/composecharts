plugins {
    //alias(libs.plugins.android.application)
    id ("com.android.library")
    id ("maven-publish")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.touzalab.composecharts"
    compileSdk = 35

    defaultConfig {
        //applicationId = "com.touzalab.composecharts"
        minSdk = 24
        targetSdk = 35
        //versionCode = 1
        //versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    // Dépendances Jetpack Compose de base
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.foundation.foundation)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.material3.android)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // Dépendance pour l'animation des graphiques
    implementation(libs.androidx.animation)
    // Bibliothèque pour manipuler les couleurs et les dégradés
    implementation(libs.colorpicker.compose)
}


// Configuration de publication pour une bibliothèque
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.2zalab"  // Votre groupId
                artifactId = "composecharts"     // Votre artifactId
                version = "1.0.0"              // Votre version
                from(components["release"])
            }
        }
    }
}
