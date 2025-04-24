// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        // Plugin de Firebase para servicios de Google
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Plugin de Firebase (referenciado aquí también por seguridad)
    id("com.google.gms.google-services") version "4.4.2" apply false
}


