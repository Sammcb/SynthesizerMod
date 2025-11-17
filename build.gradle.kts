import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	// Add Kotlin plugin
	kotlin("jvm").version(libs.versions.kotlin)
	// Add Kotlin serialization for JSON
	kotlin("plugin.serialization").version(libs.versions.kotlin)
	// Add Fabric Loom for building mod
	alias(libs.plugins.fabric.loom)
}

val modId: String by project
val modVersion: String by project
val mavenGroup: String by project

// Values expected by Fabric
version = modVersion
group = mavenGroup

base {
	// Sets archive base name for archive tasks
	archivesName.set(modId)
}

// Configure build to separate client and common code
loom {
	splitEnvironmentSourceSets()

	mods {
		register(modId) {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}
}

dependencies {
	// Add Minecraft dependency
	minecraft(libs.minecraft)
	// Use official Mojan mappings
	mappings(loom.officialMojangMappings())
	// Add Fabric loader
	modImplementation(libs.fabric.loader)
	// Add Fabric API
	modImplementation(libs.fabric.api)
	// Add Fabric language Kotlin
	modImplementation(libs.fabric.lang.kotlin)
}

tasks {
	val javaVersion = JavaVersion.toVersion(libs.versions.java.get())

	// Set Java compilation settings
	withType<JavaCompile> {
		options.release.set(javaVersion.toString().toInt())
	}

	// Set Kotlin compliation settings
	kotlin {
		compilerOptions {
			jvmTarget.set(JvmTarget.fromTarget(javaVersion.toString()))
		}
	}

	processResources {
		inputs.property("version", modVersion)

		// Template values in mod info file
		filesMatching("fabric.mod.json") {
			expand(
				"version" to modVersion,
				"id" to modId,
				"group" to mavenGroup,
				"javaVersion" to javaVersion.toString(),
				"minecraftVersion" to libs.versions.minecraft.get(),
				"loaderVersion" to libs.versions.fabric.loader.get(),
				"apiVersion" to libs.versions.fabric.api.get(),
				"fabricKotlinVersion" to libs.versions.fabric.lang.kotlin.get()
			)
		}

		// Template values in data/resource pack files
		filesMatching("**/*.json") {
			expand("id" to modId)
		}
	}

	java {
		// Generate Minecraft source
		withSourcesJar()

		// Pin to Java version
		sourceCompatibility = javaVersion
		targetCompatibility = javaVersion
	}

	jar {
		// Rename jar file
		inputs.property("archivesName", modId)

		// Include LICENSE file in jar output
		from("LICENSE")
	}
}
