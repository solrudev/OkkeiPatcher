import android.annotation.SuppressLint
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
		versionCode = 42
		versionName = "2.0.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		vectorDrawables.useSupportLibrary = true
	}

	signingConfigs {
		register("release") {
			val keystorePropertiesFile = rootProject.file("keystore.properties")
			if (keystorePropertiesFile.exists()) {
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
			signingConfig = signingConfigs.getByName("release")
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
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}

	kotlinOptions {
		jvmTarget = "11"
		freeCompilerArgs += "-Xjvm-default=all"
	}

	buildFeatures {
		viewBinding = true
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
	implementation(files("libs/Base64.jar")) // java.util.Base64 for apksig on API 24-25

	debugImplementation(androidx.multidex)

	testImplementation(test.junit)
	androidTestImplementation(androidx.test.ext.junit)
	androidTestImplementation(androidx.espresso.core)
}

kapt {
	correctErrorTypes = true
}