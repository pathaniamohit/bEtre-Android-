plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.betre"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.betre"
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}
dependencies {
    // Add the Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))

    // Firebase dependencies without specifying versions
    implementation("com.google.firebase:firebase-auth:+")
    implementation("com.google.firebase:firebase-firestore:+")
    implementation("com.google.firebase:firebase-storage:+")
    implementation("com.google.firebase:firebase-messaging:+")
}