plugins {
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

