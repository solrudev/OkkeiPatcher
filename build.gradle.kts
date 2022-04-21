buildscript {
	val androidGradleVersion: String by extra("7.1.3")
	val kotlinVersion: String by extra("1.6.10")
	val hiltVersion: String by extra("2.41")
	val navigationVersion: String by extra("2.4.1")

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

allprojects {
	repositories {
		google()
		mavenCentral()
	}
}

tasks.register<Delete>("clean").configure {
	delete(rootProject.buildDir)
}
