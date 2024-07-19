plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlinAndroidKsp)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.bp.dinodata"
    compileSdk = 34

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/gradle/incremental.annotation.processors"
        }
    }

    defaultConfig {
        applicationId = "com.bp.dinodata"
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.ui.tooling.preview.android)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.androidx.media3.common)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Android Material Theming and Animations
    implementation(libs.androidx.animation)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material3)
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation(libs.androidx.material3.adaptive.navigation.suite)

    // Image Loading
    implementation(libs.glide)
    implementation(libs.glideCompose)
    implementation(libs.coil.compose)


    // RoomDB
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.androidx.room.compiler)


    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))

    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.firestore)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.extensions)

    // Dagger-Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Material Design 3
    implementation(libs.androidx.material3)

    // Android Studio Preview support
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

}