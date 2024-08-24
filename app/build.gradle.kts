plugins {

    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.navigationSafeArgs)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.pluginDaggerHilt)
    alias(libs.plugins.pluginDevKsp)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)

    kotlin("kapt")
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.pluginNavigationSafeArgs)
    alias(libs.plugins.pluginDaggerHilt)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.pluginCrashlytics)
    alias(libs.plugins.pluginGoogleServices)
    alias(libs.plugins.pluginKsp)

}

android {
    namespace = "com.bonsaizen.bonsaizenapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bonsaizen.bonsaizenapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    buildFeatures {
        viewBinding = true


        buildConfig = true

    }
}

dependencies {

    implementation(libs.androidx.activity)

    implementation(libs.androidx.fragment)
    implementation(libs.androidx.constraintlayout)
    implementation (libs.androidx.recyclerview)
    implementation (libs.androidx.lifecycle.runtime.ktx)

    // Navigation
    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)

    // ViewModel
    implementation (libs.androidx.lifecycle.viewmodel.ktx)

    // Coroutines
    implementation (libs.kotlinx.coroutines.core)
    implementation (libs.kotlinx.coroutines.android)

    /// Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    //Lottie
    implementation(libs.lottie)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    //Biometric
    implementation(libs.androidxBiometric)
    //Encrypted Shared Preferences
    implementation(libs.androidxCryptoSharedPreferences)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation (libs.firebase.auth.ktx)
    implementation (libs.firebase.firestore.ktx)
    implementation (libs.firebase.storage.ktx)

    //Room
    implementation (libs.androidx.room.ktx)
    ksp (libs.androidx.room.compiler)
}

    implementation(libs.androidx.security.crypto.ktx)
    //Android
    implementation(libs.bundles.android)
    //Navigation
    implementation(libs.bundles.navigation)
    //Lifecycle
    implementation(libs.bundles.lifecycle)
    //Retrofit
    implementation(libs.bundles.retrofit)
    //Okhttp
    implementation(libs.okhttp)
    //Interceptor
    implementation(libs.interceptor)
    implementation(libs.gson)
    //Hilt
    implementation(libs.daggerHilt)
    //Rooms
    implementation(libs.bundles.room)
    //coroutines
    implementation(libs.bundles.coroutines)
    //Circle image view
    implementation(libs.circleimageview)
    // Kotlin
    implementation(libs.androidx.biometric)
    // Firebase
    implementation(libs.bundles.firebase)
    implementation(libs.firebase.firestore)
    implementation(platform(libs.firebase.bom.v3280))
    implementation(libs.firebase.storage)
    implementation(libs.firebase.auth)
    //Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Kapt
    kapt(libs.daggerHiltCompiler)
    //Ksp
    ksp(libs.androidx.room.compiler)
}

kapt {
    correctErrorTypes = true
}


