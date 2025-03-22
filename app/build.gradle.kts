import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin") version "2.7.7"
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

val apiKey: String = if (project.rootProject.file("apikey.properties").exists()) {
    val properties = Properties()
    properties.load(project.rootProject.file("apikey.properties").inputStream())
    properties.getProperty("OPENAI_API_KEY", "")
} else {
    ""
}

android {
    namespace = "com.example.flowerly"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.flowerly"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"



        buildConfigField("String", "OPENAI_API_KEY", "\"$apiKey\"")

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    // Firebase BoM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation ("androidx.room:room-runtime:2.5.2")
    implementation ("androidx.room:room-ktx:2.5.2")
    implementation(libs.transport.api)

    kapt ("androidx.room:room-compiler:2.5.2")

    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation(libs.firebase.firestore.ktx)
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.0")

    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("com.google.android.material:material:1.11.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
