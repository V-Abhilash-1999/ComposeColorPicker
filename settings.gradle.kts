enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()

        maven {
            setUrl("https://jitpack.io")
            setUrl("https://kotlin.bintray.com/kotlinx")
            setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        maven {
            setUrl("https://jitpack.io")
            setUrl("https://kotlin.bintray.com/kotlinx")
            setUrl("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        }
    }
}

rootProject.name = "ComposeColorPicker"
include(":android")
include(":desktop")
include(":common")
include(":colorpicker")