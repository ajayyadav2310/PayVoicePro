plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ajay.payvoicepro"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ajay.payvoicepro"
        minSdk = 23
        targetSdk = 36

        versionCode = 1
        versionName = "1.0.0"
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("com.google.android.play:app-update-ktx:2.1.0")
}
