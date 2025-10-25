plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false // Hilt объявлен тут
    alias(libs.plugins.ksp) apply false
    // Добавьте KSP СЮДА, с версией
    alias(libs.plugins.ksp) apply false // <-- Добавлено
}