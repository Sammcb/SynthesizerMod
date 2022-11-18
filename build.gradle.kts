plugins {
	kotlin("jvm").version(libs.versions.kotlin)
	kotlin("plugin.serialization").version(libs.versions.kotlin)
	alias(libs.plugins.quilt.loom)
}

dependencies {
	minecraft(libs.minecraft)
	mappings(variantOf(libs.quilt.mappings) {
		classifier("intermediary-v2")
	})
	modImplementation(libs.quilt.loader)
	modImplementation(libs.quilt.lang.kotlin)
	modImplementation(libs.quilted.fabric.api)
}

tasks {
	val modId: String by project
	val javaVersion = JavaVersion.VERSION_17

	withType<JavaCompile> {
		options.encoding = "UTF-8"
		sourceCompatibility = javaVersion.toString()
		targetCompatibility = javaVersion.toString()
		options.release.set(javaVersion.toString().toInt())
	}

	withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		kotlinOptions { jvmTarget = javaVersion.toString() }
	}

	processResources {
		val modVersion: String by project
		val mavenGroup: String by project

		inputs.property("version", modVersion)

		filesMatching("quilt.mod.json") {
			expand(
				"version" to modVersion,
				"id" to modId,
				"group" to mavenGroup,
				"minecraftVersion" to libs.versions.minecraft.get(),
				"loaderVersion" to libs.versions.quilt.loader.get(),
				"apiVersion" to libs.versions.quilted.fabric.api.get(),
				"quiltKotlinVersion" to libs.versions.quilt.lang.kotlin.get(),
				"javaVersion" to javaVersion.toString()
			)
		}

		filesMatching("**/*.json") {
			expand("id" to modId)
		}
	}

	withType<AbstractArchiveTask> {
		from("LICENSE") {
			rename { "${it}_${modId}" }
		}
	}

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
		}
		sourceCompatibility = javaVersion
		targetCompatibility = javaVersion
		withSourcesJar()
	}
}
