import java.io.FileInputStream
import java.util.*

val packageName = "solru.okkeipatcher"
val androidGradleVersion: String by rootProject.extra
val hiltVersion: String by rootProject.extra
val ktorVersion: String by rootProject.extra
val okioVersion: String by rootProject.extra

plugins {
	id("com.android.application")
	kotlin("android")
	kotlin("kapt")
	id("dagger.hilt.android.plugin")
}

base {
	archivesName.set(packageName)
}

android {
	compileSdk = 31
	buildToolsVersion = "31.0.0"

	defaultConfig {
		applicationId = packageName
		minSdk = 19
		targetSdk = 31
		versionCode = 42
		versionName = "2.0"
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	signingConfigs {
		register("release") {
			val keystorePropertiesFile = rootProject.file("keystore.properties")
			val keystoreProperties = Properties().apply {
				load(FileInputStream(keystorePropertiesFile))
			}
			keyAlias = keystoreProperties["keyAlias"] as String?
			keyPassword = keystoreProperties["keyPassword"] as String?
			storeFile = keystoreProperties["storeFile"]?.let { file(it) }
			storePassword = keystoreProperties["storePassword"] as String?
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
			signingConfig = signingConfigs.getByName("release")
			setProguardFiles(
				listOf(
					getDefaultProguardFile("proguard-android-optimize.txt"),
					"proguard-rules.pro"
				)
			)
		}
	}

	androidResources {
		noCompress("pem")
		noCompress("pk8")
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	}

	packagingOptions {
		resources.excludes.add("DebugProbesKt.bin")
		resources.excludes.add("androidsupportmultidexversion.txt")
	}

	kotlinOptions {
		jvmTarget = "1.8"
	}

	buildFeatures {
		dataBinding = true
		viewBinding = true
	}
}

dependencies {
	val retrofitVersion = "2.9.0"
	val moshiVersion = "1.13.0"

	kapt("androidx.databinding:databinding-compiler:$androidGradleVersion")
	kapt("com.google.dagger:hilt-compiler:$hiltVersion")
	kapt("androidx.hilt:hilt-compiler:1.0.0")
	kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

	// AndroidX
	implementation("com.google.dagger:hilt-android:$hiltVersion")
	implementation("com.google.android.material:material:1.5.0")
	implementation("androidx.preference:preference-ktx:1.2.0")
	implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
	implementation("androidx.fragment:fragment-ktx:1.4.1")
	implementation("androidx.hilt:hilt-work:1.0.0")
	implementation("androidx.work:work-runtime-ktx:2.7.1")

	// I/O
	val excludeOkHttp = Action<ExternalModuleDependency> {
		exclude(group = "com.squareup.okhttp3", module = "okhttp")
	}
	implementation("com.squareup.okio:okio:3.0.0")
	implementation("com.squareup.moshi:moshi:$moshiVersion")
	implementation("com.squareup.retrofit2:retrofit:$retrofitVersion", excludeOkHttp)
	implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion", excludeOkHttp)
	implementation("io.ktor:ktor-client-okhttp:1.6.3", excludeOkHttp)
	implementation("com.squareup.okhttp3:okhttp:3.12.13") {
		because("Android 4.4 support")
	}

	// Miscellaneous
	implementation("com.android.tools.build:apksig:$androidGradleVersion")
	implementation("com.anggrayudi:storage:1.1.0")
	implementation("net.lingala.zip4j:zip4j:2.9.1")
	implementation("io.github.solrudev:simpleinstaller:1.2.3")

	debugImplementation("androidx.multidex:multidex:2.0.1")

	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.1.3")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

kapt {
	correctErrorTypes = true
}
