buildscript {
	val androidGradleVersion: String by extra("7.2.1")
	val kotlinVersion: String by extra("1.7.10")
	val hiltVersion: String by extra("2.42")
	val navigationVersion: String by extra("2.5.0")

	repositories {
		google()
		mavenCentral()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:$androidGradleVersion")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
		classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
		classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion")
	}
}

@Suppress("JcenterRepositoryObsolete")
allprojects {
	repositories {
		google()
		mavenCentral()
		jcenter()
	}
}

tasks.register<Delete>("clean").configure {
	delete(rootProject.buildDir)
}
