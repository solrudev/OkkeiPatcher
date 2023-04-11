/*
 * Okkei Patcher
 * Copyright (C) 2023 Ilya Fomichev
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import android.annotation.SuppressLint
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import java.io.FileInputStream
import java.util.*

val packageName = "ru.solrudev.okkeipatcher"

@Suppress("DSL_SCOPE_VIOLATION") // https://github.com/gradle/gradle/issues/22797
plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.kapt)
	alias(libs.plugins.kotlin.ksp)
	alias(dagger.plugins.hilt.plugin)
	alias(androidx.plugins.navigation.safeargs)
}

kotlin {
	jvmToolchain(17)
}

base {
	archivesName.set(packageName)
}

android {
	compileSdk = 33
	buildToolsVersion = "33.0.0"
	namespace = packageName

	defaultConfig {
		applicationId = packageName
		minSdk = 19
		@SuppressLint("OldTargetApi")
		targetSdk = 32
		versionCode = 43
		versionName = "2.0.1"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables.useSupportLibrary = true
	}

	signingConfigs {
		val keystorePropertiesFile = rootProject.file("keystore.properties")
		if (keystorePropertiesFile.exists()) {
			register("release") {
				val keystoreProperties = Properties().apply {
					load(FileInputStream(keystorePropertiesFile))
				}
				keyAlias = keystoreProperties["keyAlias"] as? String
				keyPassword = keystoreProperties["keyPassword"] as? String
				storeFile = keystoreProperties["storeFile"]?.let(::file)
				storePassword = keystoreProperties["storePassword"] as? String
				enableV3Signing = true
				enableV4Signing = true
			}
		}
	}

	buildTypes {
		debug {
			multiDexEnabled = true
		}
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			signingConfig = signingConfigs.findByName("release") ?: signingConfigs["debug"]
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}

	val flavorDimension = "flavor"
	flavorDimensions.add(flavorDimension)

	productFlavors {
		create("mock") {
			dimension = flavorDimension
			applicationIdSuffix = ".mock"
			versionNameSuffix = "-mock"
		}
		create("prod") {
			dimension = flavorDimension
		}
		sourceSets {
			named("mock") {
				kotlin.srcDir("src/mock/kotlin")
			}
			named("prod") {
				kotlin.srcDir("src/prod/kotlin")
			}
		}
	}

	androidResources {
		noCompress("pem")
		noCompress("pk8")
		noCompress("past")
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	buildFeatures {
		viewBinding = true
	}
}

tasks.withType<KotlinJvmCompile> {
	compilerOptions {
		freeCompilerArgs.add("-Xjvm-default=all")
	}
}

dependencies {
	kapt(dagger.bundles.hilt.compilers)
	ksp(libs.moshi.kotlin.codegen)
	ksp(androidx.room.compiler)

	// Jetpack
	implementation(dagger.hilt.android)
	implementation(androidx.activity.ktx)
	implementation(androidx.preference.ktx)
	implementation(androidx.lifecycle.livedata.ktx)
	implementation(androidx.fragment.ktx)
	implementation(androidx.hilt.work)
	implementation(androidx.work.runtime.ktx)
	implementation(androidx.bundles.navigation)
	implementation(androidx.room.ktx)
	implementation(androidx.datastore.preferences)
	implementation(androidx.swiperefreshlayout)

	// Material Components
	implementation(libs.materialcomponents)

	// I/O
	implementation(libs.okio)
	implementation(libs.moshi)
	implementation(libs.okhttp)
	implementation(libs.bundles.retrofit)

	// Miscellaneous
	implementation(libs.pseudoapksigner)
	implementation(libs.apksig)
	implementation(libs.zip4j)
	implementation(libs.simpleinstaller)
	implementation(libs.jetmvi)
	implementation(libs.viewbindingpropertydelegate)
	implementation(libs.progressbutton)
	implementation(libs.insetter)
	implementation(libs.lottie)
	implementation(files("libs/Base64.jar")) // java.util.Base64 for apksig on API 24-25

	debugImplementation(androidx.multidex)

	testImplementation(test.junit)
	androidTestImplementation(androidx.test.ext.junit)
	androidTestImplementation(androidx.espresso.core)
}

kapt {
	correctErrorTypes = true
}