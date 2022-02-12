import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	val androidGradleVersion: String by extra("7.1.1")
	val kotlinVersion: String by extra("1.6.10")
	val hiltVersion: String by extra("2.40.5")

	repositories {
		google()
		mavenCentral()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:$androidGradleVersion")
		classpath(kotlin("gradle-plugin", kotlinVersion))
		classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
	}
}

allprojects {
	repositories {
		google()
		mavenCentral()
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
		}
	}
}

tasks.register<Delete>("clean").configure {
	delete(rootProject.buildDir)
}
