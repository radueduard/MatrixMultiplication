# Matrix Multiplication Library

A high-performance matrix multiplication library for Kotlin with native C++ backend support. This library provides optimized matrix operations for both JVM and Kotlin/Native platforms, supporting `Int`, `Float`, and `Double` data types.

## Features

- **Multi-platform support**: Works on both JVM and Kotlin/Native
- **Type-safe**: Generic `Matrix<T>` class supporting `Int`, `Float`, and `Double`
- **High performance**: Native C++ implementation with multi-threading
- **Simple API**: Easy-to-use operator overloading for matrix multiplication
- **Well-tested**: Comprehensive test suite included

## API Usage

### Creating Matrices

Create a matrix by specifying the number of rows, columns, and data:

```kotlin
// Create a 2x3 integer matrix
val data = arrayOf(1, 2, 3, 4, 5, 6)
val matrix = Matrix(rows = 2, cols = 3, data = data)

// Create matrices with different types
val intMatrix = Matrix(2, 2, arrayOf(1, 2, 3, 4))
val floatMatrix = Matrix(2, 2, arrayOf(1.5f, 2.5f, 3.5f, 4.5f))
val doubleMatrix = Matrix(2, 2, arrayOf(1.5, 2.5, 3.5, 4.5))
```

### Matrix Multiplication

Use the `*` operator to multiply matrices:

```kotlin
val matrixA = Matrix(2, 3, arrayOf(2, 3, 4, 1, 2, 3))
val matrixB = Matrix(3, 2, arrayOf(1, 4, 2, 5, 3, 6))

// Multiply matrices (2x3 * 3x2 = 2x2)
val result = matrixA * matrixB
```

**Note**: The number of columns in the first matrix must equal the number of rows in the second matrix, or an `IllegalArgumentException` will be thrown.

### Accessing Matrix Elements

Access and modify matrix elements using array-style indexing:

```kotlin
val matrix = Matrix(2, 3, arrayOf(1, 2, 3, 4, 5, 6))

// Get element at row 0, column 1
val value = matrix[0, 1]  // Returns 2

// Set element at row 1, column 2
matrix[1, 2] = 10
```

### Matrix Properties

```kotlin
val matrix = Matrix(2, 3, arrayOf(1, 2, 3, 4, 5, 6))

println(matrix.rows)  // 2
println(matrix.cols)  // 3
println(matrix.data)  // [1, 2, 3, 4, 5, 6]
```

### String Representation

```kotlin
val matrix = Matrix(2, 3, arrayOf(1, 2, 3, 4, 5, 6))
println(matrix)
```

## Examples

### Example 1: Identity Matrix Multiplication

```kotlin
// 3x3 identity matrix
val identity = Matrix(3, 3, arrayOf(
    1, 0, 0,
    0, 1, 0,
    0, 0, 1
))

// Any matrix multiplied by identity equals itself
val testMatrix = Matrix(3, 3, arrayOf(
    5, 6, 7,
    8, 9, 10,
    11, 12, 13
))

val result = testMatrix * identity  // Result equals testMatrix
```

### Example 2: Rectangular Matrix Multiplication

```kotlin
// 1x3 matrix
val matrixA = Matrix(1, 3, arrayOf(2, 3, 4))

// 3x1 matrix
val matrixB = Matrix(3, 1, arrayOf(5, 6, 7))

// Result is 1x1 matrix: [56]
// Calculation: 2*5 + 3*6 + 4*7 = 10 + 18 + 28 = 56
val result = matrixA * matrixB
```

### Example 3: Floating Point Matrices

```kotlin
val matrixA = Matrix(2, 2, arrayOf(1.5f, 2.5f, 3.5f, 4.5f))
val matrixB = Matrix(2, 2, arrayOf(0.5f, 1.0f, 1.5f, 2.0f))

val result = matrixA * matrixB
```

## Running Tests

### Prerequisites

- JDK 20
- CMake (for native builds)
- C++ compiler with C++14 support

### Run JVM Tests

```bash
./gradlew :cleanJvmTest :jvmTest --tests "matrixMultiply.MatrixTestsJVM" -Dorg.gradle.java.home=$(/usr/libexec/java_home -v 20)
```

### Run Native Tests

```bash
./gradlew :cleanNativeTest :nativeTest --tests "MatrixTestsNative" -Dorg.gradle.java.home=$(/usr/libexec/java_home -v 20)
```

> There are also configurations done in IntelliJ for running tests.

## Building the Project

### Build for JVM

```bash
./gradlew jvmJar
```

### Build for Native

```bash
./gradlew nativeBinaries
```

### Build Everything

```bash
./gradlew build
```

## Performance

The library uses optimized C++ implementation with:

- **Matrix transposition**: The second matrix is transposed for better cache locality
- **Multi-threading**: Each row is computed in a separate thread for parallel execution
- **Register optimization**: Uses register variables for frequently accessed data

## Project Structure

```
.
├── build.gradle.kts           # Gradle build configuration
├── settings.gradle.kts        # Gradle settings
├── matMul/                    # Native C++ library
│   ├── CMakeLists.txt         # CMake configuration
│   ├── include/
│   │   └── matMul.h           # C++ header
│   └── src/
│       ├── matrixNative.cpp   # Native implementation
│       └── matrixJNI.cpp      # JNI bindings
├── src/
│   ├── jvmMain/kotlin/        # JVM implementation
│   ├── jvmTest/kotlin/        # JVM tests
│   ├── nativeMain/kotlin/     # Kotlin/Native implementation
│   └── nativeTest/kotlin/     # Kotlin/Native tests
└── README.md
```

## Error Handling

The library performs validation and throws appropriate exceptions:

```kotlin
// Throws IllegalArgumentException: Invalid matrix dimensions
val invalid1 = Matrix(0, 3, arrayOf(1, 2, 3))
val invalid2 = Matrix(-1, 3, arrayOf(1, 2, 3))

// Throws IllegalArgumentException: Incompatible dimensions for multiplication
val matrixA = Matrix(2, 3, arrayOf(1, 2, 3, 4, 5, 6))
val matrixB = Matrix(2, 2, arrayOf(1, 2, 3, 4))  // 2x3 cannot multiply 2x2
val result = matrixA * matrixB
```

