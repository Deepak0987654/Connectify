plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.connectify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.connectify"
        minSdk = 26
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
    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // pinView for otp
    val pinview_version = "1.4.4"
    implementation("io.github.chaosleung:pinview:$pinview_version")

    // Country Code Picker
    implementation("com.hbb20:ccp:2.5.0")

    // fragment
    val fragment_version = "1.8.6"
    implementation("androidx.fragment:fragment:$fragment_version")

    // Import the BoM for the Firebase platform
    val bom_version = "33.13.0"
    implementation(platform("com.google.firebase:firebase-bom:$bom_version"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // to load images from Urls using librar like glide or picasso
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // FirebaseUI for Cloud Firestore
    implementation("com.firebaseui:firebase-ui-firestore:9.0.0")

    // Image Picker
    implementation("com.github.dhaval2404:imagepicker:2.1")

}