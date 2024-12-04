plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.smartgrocerytracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartgrocerytracker"
        minSdk = 31
        targetSdk = 33
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
    sourceSets {
        getByName("main") {
            java {
                srcDirs("src/main/java", "src/main/java/ui/signup", "src/main/java/signup",
                    "src/main/java/com/example/smartgrocerytracker/ui/signup"
                )
            }
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    implementation(libs.activity)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

// http request
    implementation(libs.volley)

// keystore sharedpref
    implementation(libs.security.crypto)
    implementation(libs.jwtdecode)
    implementation(libs.material.v180)

    implementation(libs.mpandroidchart)
    implementation(libs.gif)

    //
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

}