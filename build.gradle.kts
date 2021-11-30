import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
	val kotlinVersion: String by extra("1.6.0")
	val hiltVersion: String by extra("2.38.1")
	val ktorVersion: String by extra("1.6.3")
	val okioVersion: String by extra("2.10.0")

	repositories {
		google()
		mavenCentral()
	}

	dependencies {
		classpath("com.android.tools.build:gradle:7.0.3")
		classpath(kotlin("gradle-plugin", kotlinVersion))
		classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
		classpath("io.ktor:ktor-client-core-jvm:$ktorVersion")
	}
}

@Suppress("JcenterRepositoryObsolete")
allprojects {
	repositories {
		google()
		mavenCentral()
		jcenter()
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
