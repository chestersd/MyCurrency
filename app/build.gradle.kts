plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    // УДАЛИТЕ ЭТУ СТРОКУ:
    // id("com.google.devtools.ksp") version "1.9.10-1.0.13"
    // ЗАМЕНИТЕ НА ЭТУ:
//    id("com.google.devtools.ksp") // <-- Только ID, версия из корня
    alias(libs.plugins.ksp)
}

android {
    namespace = "dev.kadyko.mycurrency"
    compileSdk = 36

    defaultConfig {
        applicationId = "dev.kadyko.mycurrency"
        minSdk = 26 // Обновлено до 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler) // <-- Используем KSP для Hilt
    implementation(libs.androidx.hilt.navigation.compose)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle ViewModel for Compose
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler) // <-- Используем KSP для Room
    implementation(libs.androidx.room.ktx)

    // Shared Preferences
    implementation(libs.androidx.preference.ktx)

    // Compose Material Icons Extended
    implementation(libs.androidx.compose.material.icons.extended)
}