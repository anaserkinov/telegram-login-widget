import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.androidLint)
    alias(libs.plugins.skie)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    androidLibrary {
        namespace = "me.anasmusa.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    val xcf = XCFramework("TelegramLoginData")
    val iosTargets = listOf(iosArm64(), iosSimulatorArm64())

    iosTargets.forEach {
        it.binaries.framework {
            baseName = "TelegramLoginData"
            isStatic = true
            xcf.add(this)
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ksoup.html)
            }
        }
    }
}

skie {
    isEnabled = false
    analytics {
        disableUpload.set(true)
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(artifactId = "telegram-login-data")

    pom {
        name = "Telegram Login Data"
        description = "Telegram Login Data"
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
