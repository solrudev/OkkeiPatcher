import java.net.URI

rootProject.name = "Okkei Patcher"
include(":app")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
	repositories {
		google()
		mavenCentral()
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

	repositories {
		google()
		mavenCentral()
		maven {
			url = URI("https://jitpack.io")
			content {
				includeGroup("com.github.topjohnwu.libsu")
			}
		}
	}

	versionCatalogs {
		register("androidx") {
			from(files("gradle/androidx.versions.toml"))
		}
		register("dagger") {
			from(files("gradle/dagger.versions.toml"))
		}
	}
}