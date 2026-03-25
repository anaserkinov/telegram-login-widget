import com.android.build.api.dsl.LibraryExtension
import com.android.builder.model.AndroidLibrary
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
    android {
        namespace = "me.anasmusa.telegramlogin.widget.data"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        optimization {
            consumerKeepRules.apply {
                publish = true
                files.add(File(projectDir, "consumer-proguard-rules.pro"))
            }
        }
    }

    val xcf = XCFramework("TelegramLoginWidgetData")
    val iosTargets = listOf(iosArm64(), iosX64(), iosSimulatorArm64())

    iosTargets.forEach {
        it.binaries.framework {
            baseName = "TelegramLoginWidgetData"
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

    coordinates(artifactId = "telegram-login-widget-data")

    pom {
        name = "Telegram Login Widget Data"
        description = "Telegram Login Widget Data"
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
