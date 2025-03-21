plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.3.15" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.5.0" apply false
    kotlin("android") version "2.1.0" apply false
    kotlin("kapt") version "2.1.0" apply false
}
