import android.annotation.SuppressLint
import java.io.FileInputStream
import java.util.*

val packageName = "ru.solrudev.okkeipatcher"
val androidGradleVersion: String by rootProject.extra
val hiltVersion: String by rootProject.extra
val ktorVersion: String by rootProject.extra
val okioVersion: String by rootProject.extra
val navigationVersion: String by rootProject.extra

plugins {
	id("com.android.application")
	kotlin("android")
	kotlin("kapt")
	id("dagger.hilt.android.plugin")
	id("androidx.navigation.safeargs.kotlin")
	id("com.google.devtools.ksp") version "1.7.20-1.0.7"
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
	val retrofitVersion = "2.9.0"
	val moshiVersion = "1.14.0"
	val roomVersion = "2.4.2"

	kapt("com.google.dagger:hilt-compiler:$hiltVersion")
	kapt("androidx.hilt:hilt-compiler:1.0.0")
	ksp("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")
	ksp("androidx.room:room-compiler:$roomVersion")

	// Jetpack
	implementation("com.google.dagger:hilt-android:$hiltVersion")
	implementation("androidx.activity:activity-ktx:1.6.1")
	implementation("androidx.preference:preference-ktx:1.2.0")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
	implementation("androidx.fragment:fragment-ktx:1.5.4")
	implementation("androidx.hilt:hilt-work:1.0.0")
	implementation("androidx.work:work-runtime-ktx:2.7.1")
	implementation("androidx.navigation:navigation-fragment-ktx:$navigationVersion")
	implementation("androidx.navigation:navigation-ui-ktx:$navigationVersion")
	implementation("androidx.room:room-ktx:$roomVersion")
	implementation("androidx.datastore:datastore-preferences:1.0.0")
	implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

	// Material Components
	implementation("com.google.android.material:material:1.7.0")

	// I/O
	val excludeOkHttp = Action<ExternalModuleDependency> {
		exclude(group = "com.squareup.okhttp3", module = "okhttp")
	}
	implementation("com.squareup.okio:okio:3.2.0")
	implementation("com.squareup.moshi:moshi:$moshiVersion")
	implementation("com.squareup.retrofit2:retrofit:$retrofitVersion", excludeOkHttp)
	implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion", excludeOkHttp)
	implementation("com.squareup.okhttp3:okhttp:3.12.13") {
		because("Android 4.4 support")
	}

	// Miscellaneous
	implementation("com.github.aefyr:pseudoapksigner:1.6")
	implementation("com.android.tools.build:apksig:$androidGradleVersion")
	implementation("net.lingala.zip4j:zip4j:2.11.1")
	implementation("io.github.solrudev:simpleinstaller:4.2.1")
	implementation("io.github.solrudev:jetmvi:0.0.11")
	implementation("com.github.kirich1409:viewbindingpropertydelegate-noreflection:1.5.6")
	implementation("com.github.razir.progressbutton:progressbutton:2.1.0")
	implementation(files("libs/Base64.jar")) // java.util.Base64 for apksig on API 24-25

	debugImplementation("androidx.multidex:multidex:2.0.1")

	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.3")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

kapt {
	correctErrorTypes = true
}
