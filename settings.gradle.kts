rootProject.name = "Okkei Patcher"
include(":app")

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
	repositories {
		gradlePluginPortal()
		google()
		mavenCentral()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

	repositories {
		google()
		mavenCentral()
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