import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
}

val localConfig = Properties().apply {
    val configFile = rootProject.file("local.properties")
    if (configFile.exists()) configFile.inputStream().use { load(it) }
}

fun configValue(name: String): String =
    localConfig.getProperty(name, "").replace("\\", "\\\\").replace("\"", "\\\"")

android {
    namespace = "org.saudigitus.climasaude"
    compileSdk = 36
    defaultConfig {
        applicationId = "org.saudigitus.climasaude"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"
        buildConfigField("String", "API_BASE_URL", "\"" + configValue("BASE_URL") + "\"")
        buildConfigField("String", "ALERTS_BASE_URL", "\"" + configValue("ALERTS_BASE_URL") + "\"")
    }
    buildFeatures { compose = true; buildConfig = true }
    androidResources { noCompress += "litertlm" }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.activity.compose)
    implementation(libs.core.splashscreen)
    implementation(libs.work.runtime)
    implementation(libs.koin.core)
    implementation(libs.room.runtime)
    implementation(libs.litertlm.android)
}
