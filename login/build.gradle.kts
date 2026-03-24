plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    android {
        namespace = "me.anasmusa.telegramlogin"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    listOf(
        iosArm64(),
        iosX64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "TelegramLogin"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.lifecycle.runtimeCompose)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.activity.compose)
            }
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(artifactId = "telegram-login")

    pom {
        name = "Telegram Login"
        description = "Telegram Login"
        inceptionYear = "2026"
        url = "https://github.com/anaserkinov/telegram-login-widget/"
        licenses {
            license {
                name = "MIT license"
                url = "https://github.com/anaserkinov/telegram-login-widget?tab=MIT-1-ov-file"
                distribution = "https://github.com/anaserkinov/telegram-login-widget?tab=MIT-1-ov-file"
            }
        }
        developers {
            developer {
                name = "Anas"
                email = "anaserkinjonov@gmail.com"
                url = "https://github.com/anaserkinov/"
            }
        }
        scm {
            url = "https://github.com/anaserkinov/telegram-login-widget/"
            connection = "scm:git:git://github.com/anaserkinov/telegram-login-widget.git"
            developerConnection = "scm:git:ssh://git@github.com/anaserkinov/telegram-login-widget.git"
        }
    }
}

