rootProject.name = "Okkei Patcher"
include(":app")

pluginManagement {
	repositories {
		gradlePluginPortal()
		google()
		mavenCentral()
	}
}

dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

	repositories {
		google()
		mavenCentral()
	}

	versionCatalogs {
		create("androidx") {
			from(files("gradle/androidx.versions.toml"))
		}
		create("dagger") {
			from(files("gradle/dagger.versions.toml"))
		}
		create("test") {
			from(files("gradle/test.versions.toml"))
		}
	}
}