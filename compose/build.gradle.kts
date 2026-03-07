plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    androidLibrary {
        namespace = "me.anasmusa.telegramloginwidget"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        version = (project.findProperty("VERSION_NAME") as String?) ?: "1.0.0"

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            api(project(":shared"))
        }
    }
}

dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}

mavenPublishing {
    publishToMavenCentral(true)

    signAllPublications()

    coordinates(group.toString(), "telegram-login-widget", version.toString())

    pom {
        name = "Telegram Login Widget"
        description = "Telegram Login Widget"
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

