import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    id("org.jetbrains.dokka") version "1.9.10"
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false
}

android {
    namespace = "com.jah.pry_rfatm"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.jah.pry_rfatm"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.3.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        lint {
            baseline = file("lint-baseline.xml")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)

    implementation(platform(libs.firebase.bom.v33130))
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.perf)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.google.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.appcheck.debug)

    implementation(libs.credentials)
    implementation(libs.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.play.services.auth.v2070)

    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation (libs.fragment.testing)
    androidTestImplementation (libs.junit.v115)
    androidTestImplementation (libs.espresso.core.v351)
    androidTestImplementation (libs.rules)
    androidTestImplementation (libs.runner)
    androidTestImplementation (libs.mockito.android)
    androidTestImplementation (libs.truth)
}

/*tasks.named<DokkaTask>("dokkaHtml") {
    outputDirectory.set(buildDir.resolve("dokka/html"))

    dokkaSourceSets.configureEach {
        includeNonPublic.set(true)
        skipEmptyPackages.set(false)
        reportUndocumented.set(true)

        // Necesario para incluir código Java
        sourceRoots.from(file("src/main/java"))

        perPackageOption {
            matchingRegex.set(".*")
            suppress.set(false)
        }
    }
}*/
