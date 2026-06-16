import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("com.vanniktech.maven.publish") version "0.30.0"
}

android {
    namespace = "market.femi"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

mavenPublishing {
    coordinates(
        groupId = "io.github.femimarket",
        artifactId = "android-splash",
        version = project.findProperty("libraryVersion") as String? ?: "1.0.0"
    )

    pom {
        name.set("AndroidSplash")
        description.set("A premium video splash screen library for Jetpack Compose")
        url.set("https://github.com/femimarket/AndroidSplash")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("femi")
                name.set("Femi")
                email.set("business@femi.market")
            }
        }
        scm {
            connection.set("scm:git:github.com/femimarket/AndroidSplash.git")
            developerConnection.set("scm:git:ssh://github.com/femimarket/AndroidSplash.git")
            url.set("https://github.com/femimarket/AndroidSplash/tree/main")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

// Additional repository: GitHub Packages. Vanniktech wires Maven Central via
// `publishToMavenCentral` above; this block adds a parallel GitHub Packages
// target so `publishAllPublicationsToGitHubPackagesRepository` ships the same
// artifacts there too. Credentials read from ~/.gradle/gradle.properties
// (`gpr.user` / `gpr.key`) or env (`GITHUB_ACTOR` / `GITHUB_TOKEN`).
publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/femimarket/AndroidSplash")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}