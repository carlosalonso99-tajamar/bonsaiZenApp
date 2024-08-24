// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {

    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
    alias(libs.plugins.kotlinParcelize) apply false
    alias(libs.plugins.navigationSafeArgs) apply false
    alias(libs.plugins.pluginDaggerHilt) apply false
    alias(libs.plugins.pluginDevKsp) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.firebaseCrashlytics) apply false

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.pluginNavigationSafeArgs) apply false
    alias(libs.plugins.pluginDaggerHilt) apply false
    alias(libs.plugins.pluginCrashlytics) apply false
    alias(libs.plugins.pluginGoogleServices) apply false
    alias(libs.plugins.pluginKsp) apply false
    alias(libs.plugins.sonarQube) apply false

}