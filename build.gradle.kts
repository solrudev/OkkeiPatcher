tasks.register<Delete>("clean").configure {
	delete(rootProject.layout.buildDirectory)
}