import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.example.running_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.running_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        //load the values from .properties file
//        val keystoreFile = project.rootProject.file("apikeys.properties")
//        val properties = Properties()
//        properties.load(keystoreFile.inputStream())
//
//        //return empty key in case something goes wrong
//        val mapsApiKey = properties.getProperty("MAPS_API_KEY") ?: ""
//
//        buildConfigField(
//            type = "String",
//            name = "MAPS_API_KEY",
//            value = mapsApiKey
//        )

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
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //room
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    // To use Kotlin Symbol Processing (KSP)
    ksp(libs.room.compiler)
    //Kotlin Extensions and Coroutines support for Room
    implementation(libs.room.ktx)

    //coroutines
    implementation(libs.kotlinx.coroutines)

    // Navigation
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)
    implementation(libs.androidx.navigation.safeArgs)

    //Glide
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // Google Maps
    implementation(libs.maps.utils)
    implementation(libs.maps.utils.ktx)

    //Dagger
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    //EasyPermissions
    implementation(libs.easyPermissions)

    //Timber
    implementation(libs.timber)

    //MPAndroidChart
    implementation(libs.mpAndroidChart)
}
