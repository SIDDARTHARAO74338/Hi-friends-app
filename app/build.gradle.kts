plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {

    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        )
    }

    namespace = "com.siddartharao.hifriends"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.siddartharao.hifriends"
        minSdk = 24
        targetSdk = 34
        versionCode = 49
        versionName = "V8.4.N.7.I.4.T.3.H.8.Y.A"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.google.firebase:firebase-messaging:24.0.1")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    implementation("androidx.activity:activity:1.9.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation ("com.android.volley:volley:1.2.1")

    //implementation(platform("com.google.firebase:firebase-bom:32.0.0"))
    //implementation("com.google.android.gms:play-services-base:18.0.1") // Update to latest


    implementation ("pl.droidsonroids.gif:android-gif-drawable:1.2.25")

    implementation ("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    implementation ("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")

    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation("com.google.auth:google-auth-library-oauth2-http:1.16.0")
    implementation("com.google.http-client:google-http-client-gson:1.42.3")

}