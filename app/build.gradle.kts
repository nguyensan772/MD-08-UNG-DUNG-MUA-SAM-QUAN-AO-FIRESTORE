plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.md_08_ungdungfivestore"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.md_08_ungdungfivestore"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Thư viện Retrofit chính
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson Converter (Bắt buộc để xử lý JSON)
    implementation(libs.gson)
    implementation(libs.converter.gson)
}