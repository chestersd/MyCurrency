// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

//plugins {
//    id("com.android.application") version "8.1.4" apply false
//    id("com.android.library") version "8.1.4" apply false
//    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
//    id("com.google.dagger.hilt.android") version "2.48.1" apply false
//
//    id("com.google.devtools.ksp") version "1.9.20-1.0.14" apply false
//}
//plugins {
//    alias(libs.plugins.android.application) apply false
//    alias(libs.plugins.kotlin.android) apply false
//    alias(libs.plugins.kotlin.compose) apply false
//    id("com.google.dagger.hilt.android") version "2.57.2" apply false
//    id("com.google.devtools.ksp") version "2.2.10-2.0.2"
//}