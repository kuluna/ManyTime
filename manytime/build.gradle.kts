import com.novoda.gradle.release.PublishExtension

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        consumerProguardFiles("proguard-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta4")
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("org.apache.commons:commons-lang3:3.10")

    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}

/////// bintray settings ///////
apply(plugin = "com.novoda.bintray-release")

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.novoda:bintray-release:0.9.2")
    }
}

// ./gradlew clean build bintrayUpload
configure<PublishExtension> {
    bintrayUser = System.getenv("BINTRAY_USER")
    bintrayKey = System.getenv("BINTRAY_KEY")

    userOrg = "kuluna"
    groupId = "jp.kuluna"
    artifactId = "manytime"
    publishVersion = "0.0.8"
    desc = "Start time and end time show/input view."
    website = "https://github.com/kuluna/ManyTime"

    dryRun = false
}
