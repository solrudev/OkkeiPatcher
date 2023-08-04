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
import java.util.*

val packageName = "ru.solrudev.okkeipatcher"

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.kapt)
	alias(libs.plugins.kotlin.ksp)
	alias(dagger.plugins.hilt.plugin)
	alias(androidx.plugins.navigation.safeargs)
	alias(libs.plugins.android.cachefix)
}

kotlin {
	jvmToolchain(17)
}

base {
	archivesName.set(packageName)
}

android {
	compileSdk = 33
	buildToolsVersion = "33.0.2"
	namespace = packageName

	defaultConfig {
		applicationId = packageName
		minSdk = 19
		@SuppressLint("OldTargetApi")
		targetSdk = 32
		versionCode = 51
		versionName = "2.0.9"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables.useSupportLibrary = true
	}

	val releaseSigningConfig by signingConfigs.registering {
		initWith(signingConfigs["debug"])
		val keystorePropertiesFile = rootProject.file("keystore.properties")
		if (keystorePropertiesFile.exists()) {
			val keystoreProperties = Properties().apply {
				keystorePropertiesFile.inputStream().use(::load)
			}
			keyAlias = keystoreProperties["keyAlias"] as? String
			keyPassword = keystoreProperties["keyPassword"] as? String
			storeFile = keystoreProperties["storeFile"]?.let(::file)
			storePassword = keystoreProperties["storePassword"] as? String
			enableV3Signing = true
			enableV4Signing = true
		}
	}

	buildTypes {
		named("debug") {
			multiDexEnabled = true
		}
		named("release") {
			isMinifyEnabled = true
			isShrinkResources = true
			signingConfig = releaseSigningConfig.get()
			proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
		}
	}

	val flavorDimension = "flavor"
	flavorDimensions += flavorDimension

	productFlavors {
		register("mock") {
			dimension = flavorDimension
			applicationIdSuffix = ".mock"
			versionNameSuffix = "-mock"
		}
		register("prod") {
			dimension = flavorDimension
		}
	}

	androidResources {
		noCompress += listOf("pem", "pk8", "past")
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	buildFeatures {
		viewBinding = true
	}
}

tasks.withType<KotlinJvmCompile>().configureEach {
	compilerOptions {
		freeCompilerArgs.add("-Xjvm-default=all")
	}
}

tasks.withType<Test>().configureEach {
	useJUnitPlatform()
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
	implementation(libs.kotlinx.coroutines)
	implementation(libs.pseudoapksigner)
	implementation(libs.apksig)
	implementation(libs.zip4j)
	implementation(libs.ackpine)
	implementation(libs.jetmvi)
	implementation(libs.viewbindingpropertydelegate)
	implementation(libs.progressbutton)
	implementation(libs.insetter)
	implementation(libs.lottie)
	implementation(files("libs/Base64.jar")) // java.util.Base64 for apksig on API 24-25

	debugImplementation(androidx.multidex)

	testImplementation(libs.kotlin.test)
	testImplementation(libs.okio.fakefilesystem)
	testImplementation(libs.kotlinx.coroutines.test)
	androidTestImplementation(androidx.test.ext.junit)
	androidTestImplementation(androidx.espresso.core)
}

kapt {
	correctErrorTypes = true
}