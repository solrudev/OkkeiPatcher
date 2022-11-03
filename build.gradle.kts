buildscript {
	val androidGradleVersion: String by extra("7.3.1")
	val kotlinVersion: String by extra("1.7.20")
	val hiltVersion: String by extra("2.44")
	val navigationVersion: String by extra("2.5.2")

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
