plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

// Agregar esto para habilitar KAPT

    kotlin("kapt")

}

android {
    namespace = "com.example.proyectologin006d_final"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.proyectologin006d_final"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3:material3:1.3.0")


    // Retrofit y Gson Converter
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// Corrutinas para trabajo asincrónico
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")

    // Jetpack Compose
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")



    // Dependencia para la navegación con Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.7.7")

// Íconos (core opcional) y EXTENDIDOS (¡este es el clave!)
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")


    // Dependencias Room
    implementation("androidx.room:room-runtime:2.6.1")  // Versión actualizada
    kapt("androidx.room:room-compiler:2.6.1")          // Misma versión
    implementation("androidx.room:room-ktx:2.6.1")     // Misma versión




    // OpenStreetMap (alternativa gratuita a Google Maps)
    implementation("org.osmdroid:osmdroid-android:6.1.10")
    implementation("com.github.MKergall:osmbonuspack:6.9.0")
    // Para permisos de ubicación
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.1")
    // Opcional: para diseño mejorado
    implementation("com.google.android.material:material:1.9.0")
    
    // Coil para cargar imágenes
    implementation("io.coil-kt:coil-compose:2.5.0")

    // CameraX para funcionalidad de cámara avanzada
    val camerax_version = "1.3.3"
    implementation("androidx.camera:camera-core:$camerax_version")
    implementation("androidx.camera:camera-camera2:$camerax_version")
    implementation("androidx.camera:camera-lifecycle:$camerax_version")
    implementation("androidx.camera:camera-view:$camerax_version")

    // ML Kit Barcode Scanning para códigos QR
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // LiveData para Compose
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)



    //  TEST DEPENDENCIES (CONFIGURACIÓN CORRECTA Y LIMPIA)

// Kotest (solo estas 2 son necesarias)
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")

// MockK
    testImplementation("io.mockk:mockk:1.13.10")

// Coroutines Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")

// AndroidX Test
    testImplementation("androidx.arch.core:core-testing:2.2.0")

// JUnit 5 (solo engine, Kotest usa JUnit 5)
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")




}


tasks.withType<Test> {
    useJUnitPlatform()  // <<< NECESARIO

    testLogging {
        events("passed", "failed", "skipped")
    }
}