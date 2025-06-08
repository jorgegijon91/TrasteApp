plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services)

    id("org.jetbrains.dokka")
}

android {
    namespace = "com.example.trasteapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.trasteapp"
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
    implementation(libs.room.common.jvm)
    implementation(libs.room.runtime.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Google Maps
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Firebase
    implementation("com.google.firebase:firebase-auth-ktx:21.0.1")  // Firebase Auth
    implementation("com.google.android.gms:play-services-auth:19.0.0")  // Google Sign-In
    implementation("com.google.firebase:firebase-firestore:24.9.1")    // Firestore
    implementation("com.google.firebase:firebase-storage:20.3.0")


    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Viewpager
    implementation ("androidx.viewpager2:viewpager2:1.0.0")

    // Firma digital
    implementation ("com.github.gcacace:signature-pad:1.3.1")


}

apply(plugin = "com.google.gms.google-services")

tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml") {
    outputDirectory.set(layout.buildDirectory.dir("dokka"))

    dokkaSourceSets.configureEach {
        displayName.set("Android")
        platform.set(org.jetbrains.dokka.Platform.jvm)
        sourceRoots.from(file("src/main/java/"))
        noAndroidSdkLink.set(false)
        jdkVersion.set(8)
    }
}