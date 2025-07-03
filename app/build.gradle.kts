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
    val ccp_version = "2.5.0"
    implementation("com.hbb20:ccp:$ccp_version")

    // fragment
    val fragment_version = "1.8.6"
    implementation("androidx.fragment:fragment:$fragment_version")

    // Import the BoM for the Firebase platform
    val bom_version = "33.13.0"
    implementation(platform("com.google.firebase:firebase-bom:$bom_version"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // to load images from Urls using library like glide or picasso
    val glide_version = "4.16.0"
    implementation("com.github.bumptech.glide:glide:$glide_version")
    annotationProcessor("com.github.bumptech.glide:compiler:$glide_version")

    // FirebaseUI for Cloud Firestore
    val firebaseui_version = "9.0.0"
    implementation("com.firebaseui:firebase-ui-firestore:$firebaseui_version")

    // Image Picker
    val imagepicker_version = "2.1"
    implementation("com.github.dhaval2404:imagepicker:$imagepicker_version")

    // Cloudinary -> for Storage
    val cloudinary_version = "2.4.0"
    implementation("com.cloudinary:cloudinary-android:$cloudinary_version")

    // lifecycle
    val lifecycle_version = "2.6.1"
    implementation("androidx.lifecycle:lifecycle-runtime:$lifecycle_version")

    // required by Cloudinary -> for video
    val work_version = "2.9.0"
    implementation("androidx.work:work-runtime:$work_version")

    // PhotoView
    val photoview_version = "2.3.0"
    implementation("com.github.chrisbanes:PhotoView:$photoview_version")

    // ExoPlayer -> for VideoView
    val exo_version = "1.3.1"
    implementation("androidx.media3:media3-exoplayer:$exo_version")
    implementation("androidx.media3:media3-ui:$exo_version")

}
