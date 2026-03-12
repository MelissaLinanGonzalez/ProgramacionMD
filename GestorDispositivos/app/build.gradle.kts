plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") // Plugin fundamental para que Room pueda compilar
}

android {
    namespace = "com.example.gestordispositivos"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gestordispositivos"
        minSdk = 24
        targetSdk = 36
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
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Dependencias base del sistema (las que ya tenías)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // --- DEPENDENCIAS AÑADIDAS PARA LA ACTIVIDAD 7 ---

    // 1. Room (Base de datos local persistente)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Lifecycle (para lifecycleScope con corrutinas)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")

    // 2. Retrofit (Para conexiones HTTP/HTTPS y sincronización web)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 3. MPAndroidChart (Para extraer información y generar gráficos visuales)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // 4. Testing (Room + Robolectric)
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    testImplementation("org.robolectric:robolectric:4.11.1")
}