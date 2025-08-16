import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.dagger.hilt)
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.example.running_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.running_app"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //load the values from .properties file
        val keystoreFile = project.rootProject.file("apikeys.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        //return empty key in case something goes wrong
        val mapsApiKey = properties.getProperty("MAPS_API_KEY") ?: ""

        buildConfigField(
            type = "String",
            name = "MAPS_API_KEY",
            value = mapsApiKey
        )

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
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    coreLibraryDesugaring(libs.core.library.desugaring)

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

    //Glide
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // Google Maps
    implementation(libs.maps.utils)
    implementation(libs.maps.utils.ktx)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)

    //Dagger Hilt
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    //EasyPermissions
    implementation(libs.easyPermissions)

    //Timber
    implementation(libs.timber)

    //MPAndroidChart
    implementation(libs.mpAndroidChart)

    //DataStore
    implementation(libs.datastore)

    //LiveData
    implementation(libs.liveData)
}
