# Kotlin/Native Template with JNI Support

A Kotlin Multiplatform project template supporting both Kotlin/Native and JVM (via JNI) targets, demonstrating how to share a C++ library between both platforms.

## Features

- **Kotlin/Native**: Direct C interop with native libraries
- **JVM/JNI**: Access the same C++ library from the JVM using JNI
- **Shared C++ Library**: Single `hello` library used by both targets
- **Matrix Multiplication**: Example implementation with Int, Float, and Double support

## Project Structure

```
kmp-native-wizard/
├── hello/                      # Shared C++ library
│   ├── include/hello.h         # C API header
│   ├── src/
│   │   ├── hello.cpp           # Core implementation
│   │   └── matrixjni.cpp       # JNI wrapper
│   └── CMakeLists.txt          # Builds libhello.so and libmatrixjni.so
├── src/
│   ├── nativeMain/             # Kotlin/Native code
│   │   └── kotlin/Main.kt
│   └── jvmMain/                # JVM code
│       └── kotlin/
│           ├── Main.kt         # JVM entry point
│           └── matrixMultiply.kt  # JNI bindings
└── build.gradle.kts            # Multiplatform build configuration
```

## Getting Started

### Prerequisites

- CMake 3.22+
- C++ compiler (g++)
- JDK 11+ (for JVM/JNI support)
- Kotlin 2.0+

### Setup

1. **Install JDK and build the native libraries:**
   ```bash
   ./setup-jni.sh
   ```
   
   This will install OpenJDK if needed and build both `libhello.so` and `libmatrixjni.so`.

2. **Or build manually:**
   ```bash
   cd hello/cmake-build-release
   cmake .. -DCMAKE_BUILD_TYPE=Release
   make
   ```

### Running the Applications

#### Kotlin/Native
```bash
./gradlew runDebugExecutableNative
```

#### JVM with JNI
```bash
./gradlew runJvm
```

Or use the Gradle task:
```bash
./gradlew buildHelloLibrary  # Build C++ libraries
./gradlew jvmJar             # Build JVM jar
./gradlew runJvm             # Run JVM application
```

## How It Works

### Native Module
The native module uses Kotlin's cinterop to call C functions directly:
- Defined in `src/nativeInterop/cinterop/hello.def`
- Links against `libhello.so`

### JVM Module
The JVM module uses JNI to call the same C++ library:
- `MatrixMultiplier` class loads `libmatrixjni.so`
- JNI wrapper (`matrixjni.cpp`) bridges Kotlin/JVM to C++
- Both share the same core `libhello.so` implementation

## Documentation

See [JNI_SETUP.md](JNI_SETUP.md) for detailed setup instructions and troubleshooting.

## License

The [kmp-native-wizard template](https://github.com/Kotlin/kmp-native-wizard/) is licensed under [CC0](https://creativecommons.org/publicdomain/zero/1.0/deed.en).


