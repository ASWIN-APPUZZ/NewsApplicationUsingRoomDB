plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
//    id("androidx.navigation.safeargs.kotlin")

}

android {
    namespace = "com.android.news1application"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.android.news1application"
        minSdk = 25
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //MARK:- Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    //MARK:- Glide
    implementation(libs.glide)
    kapt(libs.compiler)

    //MARK:- Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    //MARK:- Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
//    implementation(libs.androidx.lifecycle.livedata.ktx.v260)
    implementation(libs.androidx.lifecycle.common.jvm)

    //MARK:- Retrofit
//    implementation (libs.retrofit)
//    implementation(libs.converter.gson)
//    implementation(libs.logging.interceptor)
    // retrofit


    implementation (libs.retrofit)

    implementation ("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // GSON


    implementation (libs.retrofit2.converter.gson)


    // coroutine


    implementation (libs.kotlinx.coroutines.android)

    implementation (libs.kotlinx.coroutines.core)

}