rootProject.name = "TelegramLoginWidget"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven {
            name = "Central Portal Snapshots"
            setUrl("https://central.sonatype.com/repository/maven-snapshots/")
        }
    }
}

include(":shared")
include(":compose")

include(":samples:compose-multiplatform:composeApp")
include(":samples:compose-multiplatform:androidApp")
include(":samples:android-native")
