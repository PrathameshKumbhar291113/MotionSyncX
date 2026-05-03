plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
}

// jitpack.group / jitpack.artifact / jitpack.version — see gradle.properties
group = (findProperty("jitpack.group") as String?)?.takeIf { it.isNotBlank() } ?: "com.motionsyncx.library"
version = (findProperty("jitpack.version") as String?)?.takeIf { it.isNotBlank() } ?: "1.0.0-SNAPSHOT"

android {
    namespace = "com.motionsyncx.motionsyncx"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
    }
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                artifactId = (findProperty("jitpack.artifact") as String?)?.takeIf { it.isNotBlank() }
                    ?: "motionsyncx"
                pom {
                    name.set("MotionSyncX")
                    description.set("MotionSyncX Android library module.")
                    inceptionYear.set("2026")
                }
            }
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.core.ktx)
}
