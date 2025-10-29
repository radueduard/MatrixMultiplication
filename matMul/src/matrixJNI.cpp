/**
 * JNI Wrapper for Matrix Multiplication Functions
 *
 * This file provides JNI (Java Native Interface) bindings for the matrix multiplication
 * functions implemented in hello.cpp. It allows Kotlin/JVM code to call the native C++
 * matrix multiplication functions.
 *
 * The wrapper implements three functions:
 * - multiplyIntMatrices: For integer matrix multiplication
 * - multiplyFloatMatrices: For single-precision floating-point matrix multiplication
 * - multiplyDoubleMatrices: For double-precision floating-point matrix multiplication
 *
 * Each function:
 * 1. Receives Java arrays from the JVM
 * 2. Converts them to native C++ pointers
 * 3. Calls the corresponding native C++ function
 * 4. Releases the arrays back to the JVM (copying back results)
 */

#include <jni.h>
#include "matMul.h"

extern "C" {

JNIEXPORT void JNICALL Java_matrixMultiply_MatrixMultiplierCPP_multiplyIntMatrices(
    JNIEnv* env,
    jclass clazz,
    jint n,
    jint m,
    jint l,
    jintArray matrixA,
    jintArray matrixB,
    jintArray result
) {
    // Get pointers to the array elements
    jint* arrayA = env->GetIntArrayElements(matrixA, nullptr);
    jint* arrayB = env->GetIntArrayElements(matrixB, nullptr);
    jint* arrayResult = env->GetIntArrayElements(result, nullptr);

    // Call the native C++ function
    multiplyIntMatrices(n, m, l, arrayA, arrayB, arrayResult);

    // Release the arrays (0 means copy back changes and free the native array)
    env->ReleaseIntArrayElements(matrixA, arrayA, JNI_ABORT);
    env->ReleaseIntArrayElements(matrixB, arrayB, JNI_ABORT);
    env->ReleaseIntArrayElements(result, arrayResult, 0);
}

JNIEXPORT void JNICALL Java_matrixMultiply_MatrixMultiplierCPP_multiplyFloatMatrices(
    JNIEnv* env,
    jclass clazz,
    jint n,
    jint m,
    jint l,
    jfloatArray matrixA,
    jfloatArray matrixB,
    jfloatArray result
) {
    // Get pointers to the array elements
    jfloat* arrayA = env->GetFloatArrayElements(matrixA, nullptr);
    jfloat* arrayB = env->GetFloatArrayElements(matrixB, nullptr);
    jfloat* arrayResult = env->GetFloatArrayElements(result, nullptr);

    // Call the native C++ function
    multiplyFloatMatrices(n, m, l, arrayA, arrayB, arrayResult);

    // Release the arrays (0 means copy back changes and free the native array)
    env->ReleaseFloatArrayElements(matrixA, arrayA, JNI_ABORT);
    env->ReleaseFloatArrayElements(matrixB, arrayB, JNI_ABORT);
    env->ReleaseFloatArrayElements(result, arrayResult, 0);
}

JNIEXPORT void JNICALL Java_matrixMultiply_MatrixMultiplierCPP_multiplyDoubleMatrices(
    JNIEnv* env,
    jclass clazz,
    jint n,
    jint m,
    jint l,
    jdoubleArray matrixA,
    jdoubleArray matrixB,
    jdoubleArray result
) {
    // Get pointers to the array elements
    jdouble* arrayA = env->GetDoubleArrayElements(matrixA, nullptr);
    jdouble* arrayB = env->GetDoubleArrayElements(matrixB, nullptr);
    jdouble* arrayResult = env->GetDoubleArrayElements(result, nullptr);

    // Call the native C++ function
    multiplyDoubleMatrices(n, m, l, arrayA, arrayB, arrayResult);

    // Release the arrays (0 means copy back changes and free the native array)
    env->ReleaseDoubleArrayElements(matrixA, arrayA, JNI_ABORT);
    env->ReleaseDoubleArrayElements(matrixB, arrayB, JNI_ABORT);
    env->ReleaseDoubleArrayElements(result, arrayResult, 0);
}

} // extern "C"

