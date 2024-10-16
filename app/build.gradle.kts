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

    implementation ("androidx.appcompat:appcompat:1.4.1")
    implementation ("androidx.cardview:cardview:1.0.0")


    implementation(libs.firebase.database)// RecyclerView dependency
    implementation ("androidx.recyclerview:recyclerview:1.2.1")



    // Add the Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))

    // Firebase dependencies without specifying versions
    implementation("com.google.firebase:firebase-auth:+")
    implementation("com.google.firebase:firebase-firestore:+")
    implementation("com.google.firebase:firebase-storage:+")
    implementation("com.google.firebase:firebase-messaging:+")

    // Glide dependency
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")

    // Firebase UI for loading StorageReference with Glide
    implementation ("com.firebaseui:firebase-ui-storage:8.0.0")


    //constraintlayout
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")


    // Firebase App Check for Play Integrity (Android)
    implementation ("com.google.firebase:firebase-appcheck-playintegrity:17.0.0")


}
