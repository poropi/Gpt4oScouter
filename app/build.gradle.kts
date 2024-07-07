import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "dev.poropi.gpt4oscouter"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "dev.poropi.gpt4oscouter"
        minSdk = 34
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val propertiesFile = rootProject.file("local.properties")
        val properties = Properties()
        properties.load(propertiesFile.inputStream())

        // local.propertiesからAPI_KEYを取得してBuildConfigに設定
        buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY")}\"")
        buildConfigField("String", "BASE_URL", "\"${"https://api.openai.com/"}\"")

        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    //Coroutines コルーチン
    implementation(libs.kotlinx.coroutines.android)
    // Retrofit
    implementation(libs.retrofit)
    // Retrofit with Scalar Converter
    implementation(libs.converter.scalars)
    // Retrofit with gson Converter
    implementation(libs.converter.gson)
    // Retrofit with moshi Converter
    implementation(libs.converter.moshi)
    // Moshi
    implementation(libs.moshi.kotlin)

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.timber)
    implementation(libs.logging.interceptor)

    implementation (libs.accompanist.permissions)

// CameraXライブラリ
    implementation(libs.androidx.camera.core)
    implementation (libs.androidx.camera.camera2)
    implementation (libs.androidx.camera.lifecycle)
    implementation (libs.androidx.camera.view)
    implementation (libs.androidx.camera.extensions)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.camerax.compose)

    implementation(libs.accompanist.permissions)
}

kapt {
    correctErrorTypes = true
}