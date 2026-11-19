plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "br.unisc.gestaofinanceira"
    compileSdk = 36

    defaultConfig {
        applicationId = "br.unisc.gestaofinanceira"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            // Emulador: 10.0.2.2 é o computador. Celular de verdade: trocar pelo IP do PC.
            buildConfigField("String", "API_URL", "\"http://10.0.2.2:8080/api/\"")
        }
        release {
            // Sem hospedagem: mesma URL da debug para a apresentação.
            buildConfigField("String", "API_URL", "\"http://10.0.2.2:8080/api/\"")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        // Deixa os testes JUnit chamarem métodos do Android (ex.: Log) sem quebrar.
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.swiperefreshlayout)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.mpandroidchart)

    // Log das requisições só existe no build de debug.
    debugImplementation(libs.okhttp.logging)

    testImplementation(libs.junit)
    testImplementation(libs.core.testing)
    testImplementation(libs.okhttp.mockwebserver)
}
