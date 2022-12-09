tasks.register<Delete>("clean").configure {
	delete(rootProject.buildDir)
}