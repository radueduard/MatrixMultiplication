plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
}

group = "me.user"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    val hostOs = System.getProperty("os.name")
    val isArm64 = System.getProperty("os.arch") == "aarch64"
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
        hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
        hostOs == "Linux" && isArm64 -> linuxArm64("native")
        hostOs == "Linux" && !isArm64 -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    nativeTarget.apply {
        compilations.getByName("main") {
            cinterops {
                val matMul by creating {
                    packageName("matMul")
                    definitionFile.set(project.file("src/nativeInterop/cinterop/matMul.def"))
                    includeDirs.allHeaders(projectDir.resolve("matMul/include"))
                }
            }
        }
        binaries {
            executable {
                entryPoint = "main"
                runTask?.environment("LD_LIBRARY_PATH", "${projectDir}/matMul/cmake-build-release")
            }
            all {
                linkerOpts.add("-L${projectDir}/matMul/cmake-build-release")
                linkerOpts.add("-lmatMul")
                linkerOpts.add("-L/usr/lib/x86_64-linux-gnu")
                linkerOpts.add("-lopenblas")
            }
        }
    }
    sourceSets {
        nativeMain.dependencies {
            implementation(libs.kotlinxSerializationJson)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
        }
    }
}

// Task to configure CMake for the matMul library
val cmakeConfigure = tasks.register<Exec>("cmakeConfigure") {
    workingDir = file("matMul/cmake-build-release")
    commandLine("cmake", "..", "-DCMAKE_BUILD_TYPE=Release")

    notCompatibleWithConfigurationCache("Uses exec task with workingDir")

    doFirst {
        file("matMul/cmake-build-release").mkdirs()
    }
}

// Task to build the matMul library (both native and JNI)
val buildMatMulLibrary = tasks.register<Exec>("buildMatMulLibrary") {
    dependsOn(cmakeConfigure)
    workingDir = file("matMul/cmake-build-release")
    commandLine("make")

    notCompatibleWithConfigurationCache("Uses exec task with workingDir")
}

// Make sure the JNI library is built before JVM compilation
tasks.named("compileKotlinJvm") {
    dependsOn(buildMatMulLibrary)
}

// Configure JVM to find the native library
tasks.withType<JavaExec> {
    systemProperty("java.library.path", "${projectDir}/matMul/cmake-build-release")
}

// Create a custom run task for JVM
tasks.register<JavaExec>("runJvm") {
    dependsOn("jvmJar", "buildMatMulLibrary")
    group = "application"
    description = "Run the JVM application with matrix multiplication"
    classpath = files(
        tasks.named("jvmJar").get().outputs.files,
        configurations.named("jvmRuntimeClasspath").get()
    )
    mainClass.set("matrixMultiply.MainKt")
    systemProperty("java.library.path", "${projectDir}/matMul/cmake-build-release")
}

