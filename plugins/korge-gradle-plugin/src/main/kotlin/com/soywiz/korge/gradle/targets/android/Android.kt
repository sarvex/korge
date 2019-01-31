package com.soywiz.korge.gradle.targets.android

import com.soywiz.korge.gradle.coroutinesVersion
import com.soywiz.korge.gradle.targets.getIconBytes
import com.soywiz.korge.gradle.korge
import com.soywiz.korge.gradle.kotlinVersion
import com.soywiz.korge.gradle.quoted
import com.soywiz.korge.gradle.util.Indenter
import com.soywiz.korge.gradle.util.conditionally
import com.soywiz.korge.gradle.util.ensureParents
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.GradleBuild
import java.io.File
import kotlin.collections.LinkedHashMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.listOf
import kotlin.collections.set

//Linux: ~/Android/Sdk
//Mac: ~/Library/Android/sdk
//Windows: %LOCALAPPDATA%\Android\sdk
val Project.androidSdkPath: String by lazy {
	val userHome = System.getProperty("user.home")
	listOfNotNull(
		System.getenv("ANDROID_HOME"),
		"$userHome/AppData/Local/Android/sdk",
		"$userHome/Library/Android/sdk",
		"$userHome/Android/Sdk"
	).firstOrNull { File(it).exists() } ?: error("Can't find android sdk (ANDROID_HOME environment not set and Android SDK not found in standard locations)")
}

fun Project.configureNativeAndroid() {
	val resolvedArtifacts = LinkedHashMap<String, String>()

	configurations.all {
		it.resolutionStrategy.eachDependency {
			val cleanFullName = "${it.requested.group}:${it.requested.name}".removeSuffix("-js").removeSuffix("-jvm")
			//println("RESOLVE ARTIFACT: ${it.requested}")
			//if (cleanFullName.startsWith("org.jetbrains.intellij.deps:trove4j")) return@eachDependency
			//if (cleanFullName.startsWith("org.jetbrains:annotations")) return@eachDependency
			if (cleanFullName.startsWith("org.jetbrains")) return@eachDependency
			if (cleanFullName.startsWith("junit:junit")) return@eachDependency
			if (cleanFullName.startsWith("org.hamcrest:hamcrest-core")) return@eachDependency
			if (cleanFullName.startsWith("org.jogamp")) return@eachDependency
			resolvedArtifacts[cleanFullName] = it.requested.version.toString()
		}
	}

	//val androidPackageName = "com.example.myapplication"
	//val androidAppName = "My Awesome APP Name"
	val prepareAndroidBootstrap = tasks.create("prepareAndroidBootstrap") { task ->
		task.dependsOn("compileTestKotlinJvm") // So artifacts are resolved
		task.apply {
			group = "korge"
			val overwrite = true
			val outputFolder = File(buildDir, "platforms/android")
			doLast {
				val androidPackageName = korge.id
				val androidAppName = korge.name

				val DOLLAR = "\\$"
				val ifNotExists = !overwrite
				//File(outputFolder, "build.gradle").conditionally(ifNotExists) {
				//	ensureParents().writeText("""
				//		// Top-level build file where you can add configuration options common to all sub-projects/modules.
				//		buildscript {
				//			repositories { google(); jcenter() }
				//			dependencies { classpath 'com.android.tools.build:gradle:3.3.0'; classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion" }
				//		}
				//		allprojects {
				//			repositories {
				//				mavenLocal(); maven { url = "https://dl.bintray.com/soywiz/soywiz" }; google(); jcenter()
				//			}
				//		}
				//		task clean(type: Delete) { delete rootProject.buildDir }
				//""".trimIndent())
				//}
				File(outputFolder, "local.properties").conditionally(ifNotExists) {
					ensureParents().writeText("sdk.dir=$androidSdkPath")
				}
				File(outputFolder, "settings.gradle").conditionally(ifNotExists) { ensureParents().writeText("") }
				File(
					outputFolder,
					"proguard-rules.pro"
				).conditionally(ifNotExists) { ensureParents().writeText("#Rules here\n") }

				File(outputFolder, "build.gradle").conditionally(ifNotExists) {
					ensureParents().writeText(Indenter {
						line("buildscript") {
							line("repositories { google(); jcenter() }")
							line("dependencies { classpath 'com.android.tools.build:gradle:3.3.0'; classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion' }")
						}
						line("repositories") {
							line("mavenLocal()")
							line("maven { url = 'https://dl.bintray.com/soywiz/soywiz' }")
							line("google()")
							line("jcenter()")
						}

						line("apply plugin: 'com.android.application'")
						line("apply plugin: 'kotlin-android'")
						line("apply plugin: 'kotlin-android-extensions'")

						line("android") {
							line("compileSdkVersion 28")
							line("defaultConfig") {
								line("multiDexEnabled true")
								line("applicationId '$androidPackageName'")
								line("minSdkVersion 19")
								line("targetSdkVersion 28")
								line("versionCode 1")
								line("versionName '1.0'")
								line("testInstrumentationRunner 'android.support.test.runner.AndroidJUnitRunner'")
								line("manifestPlaceholders = [${korge.configs.map { it.key + ":" + it.value.quoted }.joinToString(", ")}]")
							}
							line("buildTypes") {
								line("debug") {
									line("minifyEnabled false")
								}
								line("release") {
									line("minifyEnabled false")
									line("proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'")
								}
							}
							line("sourceSets") {
								line("main") {
									// @TODO: Use proper source sets of the app
									line("java.srcDirs += ['${project.rootDir}/src/commonMain/kotlin', '${project.rootDir}/src/androidMain/kotlin']")
									line("assets.srcDirs += ['${project.rootDir}/src/commonMain/resources', '${project.rootDir}/src/androidMain/resources']")
								}
							}
						}

						line("dependencies") {
							line("implementation fileTree(dir: 'libs', include: ['*.jar'])")
							line("implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion'")
							line("implementation 'com.android.support:multidex:1.0.3'")

							line("api 'org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion'")
							for ((name, version) in resolvedArtifacts) {
								if (name.startsWith("org.jetbrains.kotlin")) continue
								line("api '$name-android:$version'")
							}

							for (dependency in korge.plugins.flatMap { it.androidDependencies }) {
								line("implementation ${dependency.quoted}")
							}

							line("implementation 'com.android.support:appcompat-v7:28.0.0'")
							line("implementation 'com.android.support.constraint:constraint-layout:1.1.3'")
							line("testImplementation 'junit:junit:4.12'")
							line("androidTestImplementation 'com.android.support.test:runner:1.0.2'")
							line("androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'")
						}
					}.toString())
				}

				File(outputFolder, "src/main/res/mipmap-mdpi/icon.png").conditionally(ifNotExists) {
					ensureParents().writeBytes(korge.getIconBytes())
				}

				File(outputFolder, "src/main/AndroidManifest.xml").conditionally(ifNotExists) {
					ensureParents().writeText(Indenter {
						line("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
						line("<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\" package=\"$androidPackageName\">")
						indent {
							line("<application")
							indent {
								line("")
								line("android:allowBackup=\"true\"")
								line("android:label=\"$androidAppName\"")
								line("android:icon=\"@mipmap/icon\"")
								//line("android:icon=\"@android:drawable/sym_def_app_icon\"")
								//line("android:roundIcon=\"@android:drawable/sym_def_app_icon\"")
								line("android:supportsRtl=\"true\"")
								line("android:theme=\"@android:style/Theme.Black.NoTitleBar.Fullscreen\"")
							}
							line(">")
							indent {
								for (text in korge.plugins.mapNotNull { it.androidManifestApplication }) {
									line(text)
								}

								line("<activity android:name=\".MainActivity\">")
								indent {
									line("<intent-filter>")
									indent {
										line("<action android:name=\"android.intent.action.MAIN\"/>")
										line("<category android:name=\"android.intent.category.LAUNCHER\"/>")
									}
									line("</intent-filter>")
								}
								line("</activity>")
							}
							line("</application>")
						}
						line("</manifest>")
					}.toString())
				}

				File(outputFolder, "src/main/java/MainActivity.kt").conditionally(ifNotExists) {
					ensureParents().writeText(Indenter {
						line("package $androidPackageName")

						line("import com.soywiz.klock.*")
						line("import com.soywiz.korge.*")
						line("import com.soywiz.korge.tween.*")
						line("import com.soywiz.korge.view.*")
						line("import com.soywiz.korgw.*")
						line("import com.soywiz.korim.color.*")
						line("import com.soywiz.korma.geom.*")
						line("import kotlinx.coroutines.*")
						line("import ${korge.entryPoint}")

						line("class MainActivity : KorgwActivity()") {
							line("override suspend fun activityMain()") {
								for (text in korge.plugins.mapNotNull { it.androidInit }) {
									line(text)
								}
								line("${korge.entryPoint}()")
							}
						}
					}.toString())
				}


				File(outputFolder, "gradle.properties").conditionally(ifNotExists) {
					ensureParents().writeText("org.gradle.jvmargs=-Xmx1536m")
				}
			}
		}
	}

	// adb shell am start -n com.package.name/com.package.name.ActivityName
	for (debug in listOf(false, true)) {
		val suffixDebug = if (debug) "Debug" else "Release"
		val installAndroidTask = tasks.create("installAndroid$suffixDebug", GradleBuild::class.java) { task ->
			task.apply {
				group = "korge"
				dependsOn(prepareAndroidBootstrap)
				buildFile = File(buildDir, "platforms/android/build.gradle")
				version = "4.10.1"
				tasks = listOf("install$suffixDebug")
			}
		}

		for (emulator in listOf(null, false, true)) {
			val suffixDevice = when (emulator) {
				null -> ""
				false -> "Device"
				true -> "Emulator"
			}

			val extra = when (emulator) {
				null -> arrayOf()
				false -> arrayOf("-d")
				true -> arrayOf("-e")
			}

			tasks.create("runAndroid$suffixDevice$suffixDebug", Exec::class.java) { task ->
				task.apply {
					group = "korge"
					dependsOn(installAndroidTask)
					afterEvaluate {
						commandLine(
							"$androidSdkPath/platform-tools/adb", *extra, "shell", "am", "start", "-n",
							"${korge.id}/${korge.id}.MainActivity"
						)
					}
				}
			}
		}
	}
}
