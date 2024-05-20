
plugins {
    id("com.android.application")
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.final_blackjack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.final_blackjack"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    androidResources {
        ignoreAssetsPattern = "!.svn:!.git:.*:.DS_Store:!*.scc:*.*:!CVS:!thumbs.db:!picasa.ini"
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

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))



    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:21.0.3")  // o la versión más reciente
    implementation ("com.google.android.gms:play-services-auth:20.2.0")  // o la versión más reciente
    implementation ("com.google.firebase:firebase-firestore:24.3.1")


    // Gson
    implementation ("com.google.code.gson:gson:2.8.8")

    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")

    //Location
    implementation ("com.google.android.gms:play-services-location:19.0.1")

    // Room
    implementation("androidx.room:room-common:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-rxjava3:2.6.1")


    // RxJava
    implementation("io.reactivex.rxjava3:rxjava:3.1.1")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}


