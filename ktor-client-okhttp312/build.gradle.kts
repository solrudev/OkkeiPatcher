val ktorVersion: String by rootProject.extra
val okioVersion: String by rootProject.extra

plugins {
	id("java-library")
	id("kotlin")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
	implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
	implementation("com.squareup.okio:okio:$okioVersion")
	api("com.squareup.okhttp3:okhttp") {
		version { strictly("3.12.13") }
	}
}
