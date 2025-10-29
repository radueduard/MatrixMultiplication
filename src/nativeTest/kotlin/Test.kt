import kotlinx.cinterop.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import platform.posix._SC_NPROCESSORS_ONLN
import platform.posix.sysconf
import kotlin.test.*
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

import matMul.multiplyIntMatrices
import matMul.multiplyFloatMatrices
import matMul.multiplyDoubleMatrices

import kotlin.time.measureTime

class MatrixTests {

    suspend inline fun kotlinMultiply(n: Int, m: Int, l: Int, a: IntArray, b: IntArray): IntArray = coroutineScope {
        val result = IntArray(n * l)

        val bT = IntArray(l * m)
        for (i in 0 until m) {
            for (j in 0 until l) {
                bT[i * l + j] = b[j * m + i]
            }
        }
        val processors = sysconf(_SC_NPROCESSORS_ONLN).toInt()
        val chunkSize = (n + processors - 1) / processors

        val jobs = (0 until n step chunkSize).map { startRow ->
            async(Dispatchers.Default) {
                val endRow = minOf(startRow + chunkSize, n)
                for (i in startRow until endRow) {
                    for (j in 0 until l) {
                        var sum = 0
                        for (k in 0 until m) {
                            sum += a[i * m + k] * bT[j * m + k]
                        }
                        result[i * l + j] = sum
                    }
                }
            }
        }

        jobs.awaitAll()
        result
    }

    suspend inline fun kotlinMultiply(n: Int, m: Int, l: Int, a: DoubleArray, b: DoubleArray): DoubleArray = coroutineScope {
        val result = DoubleArray(n * l) { 0.0 }

        val bT = DoubleArray(l * m) { 0.0 }
        for (i in 0 until m) {
            for (j in 0 until l) {
                bT[i * l + j] = b[j * m + i]
            }
        }

        val processors = sysconf(_SC_NPROCESSORS_ONLN).toInt()
        val chunkSize = (n + processors - 1) / processors

        val jobs = (0 until n step chunkSize).map { startRow ->
            async(Dispatchers.Default) {
                val endRow = minOf(startRow + chunkSize, n)
                for (i in startRow until endRow) {
                    for (j in 0 until l) {
                        var sum = 0.0
                        for (k in 0 until m) {
                            sum += a[i * m + k] * bT[j * m + k]
                        }
                        result[i * l + j] = sum
                    }
                }
            }
        }

        jobs.awaitAll()
        result
    }

    suspend inline fun kotlinMultiply(n: Int, m: Int, l: Int, a: FloatArray, b: FloatArray): FloatArray = coroutineScope {
        val result = FloatArray(n * l) { 0.0f }

        val bT = FloatArray(l * m) { 0.0f }
        for (i in 0 until m) {
            for (j in 0 until l) {
                bT[i * l + j] = b[j * m + i]
            }
        }

        val processors = sysconf(_SC_NPROCESSORS_ONLN).toInt()
        val chunkSize = (n + processors - 1) / processors

        val jobs = (0 until n step chunkSize).map { startRow ->
            async(Dispatchers.Default) {
                val endRow = minOf(startRow + chunkSize, n)
                for (i in startRow until endRow) {
                    for (j in 0 until l) {
                        var sum = 0.0f
                        for (k in 0 until m) {
                            sum += a[i * m + k] * bT[j * m + k]
                        }
                        result[i * l + j] = sum
                    }
                }
            }
        }

        jobs.awaitAll()
        result
    }


    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun smallIntMultiply() = runBlocking {
        memScoped {
            val n = 2
            val m = 3
            val l = 2

            val a = intArrayOf(2, 3, 4, 1, 2, 3)
            val b = intArrayOf(1, 4, 2, 5, 3, 6)
            val result = allocArray<IntVar>(n * l)

            multiplyIntMatrices(n, m, l, a.refTo(0), b.refTo(0), result)
            val expected = kotlinMultiply(n, m, l, a, b)

            for (i in 0 until n * l) {
                assertEquals(expected[i], result[i], "At index $i / ${n * l}")
            }

        }
    }

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun testMultiplyIntMatrices() = runBlocking {
        memScoped {
            val n = 1000
            val m = 1000 * 2
            val l = 1000 * 4

            val mat1 = allocArray<IntVar>(n * m)
            val mat2 = allocArray<IntVar>(m * l)
            val result = allocArray<IntVar>(n * l)

            val mat1Values = IntArray(n * m) { Random.nextInt() }
            val mat2Values = IntArray(m * l) { Random.nextInt() }

            for (i in 0 until n * m) {
                mat1[i] = mat1Values[i]
            }
            for (i in 0 until m * l) {
                mat2[i] = mat2Values[i]
            }

            val timeCPP = measureTime {
                multiplyIntMatrices(n, m, l, mat1, mat2, result)
            }

            val expected : IntArray
            val timeKotlin = measureTime {
                expected = kotlinMultiply(n, m, l, mat1Values, mat2Values)
            }

            println("Time CPP: $timeCPP")
            println("Time Kotlin: $timeKotlin")
            for (i in 0 until n * l) {
                assertEquals(expected[i], result[i], "At index $i / ${n * l}")
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun testMultiplyFloatMatrices() = runBlocking {
        memScoped {
            val n = 1000
            val m = 1000 * 2
            val l = 1000 * 4

            val mat1 = allocArray<FloatVar>(n * m)
            val mat2 = allocArray<FloatVar>(m * l)
            val result = allocArray<FloatVar>(n * l)

            val mat1Values = FloatArray(n * m) { Random.nextFloat() }
            val mat2Values = FloatArray(m * l) { Random.nextFloat() }

            for (i in 0 until n * m) {
                mat1[i] = mat1Values[i]
            }
            for (i in 0 until m * l) {
                mat2[i] = mat2Values[i]
            }

            val timeCPP = measureTime {
                multiplyFloatMatrices(n, m, l, mat1, mat2, result)
            }

            val expected : FloatArray
            val timeKotlin = measureTime {
                expected = kotlinMultiply(n, m, l, mat1Values, mat2Values)
            }
            println("Time CPP: $timeCPP")
            println("Time Kotlin: $timeKotlin")

            for (i in 0 until n * l) {
                assertEquals(expected[i], result[i], 0.01f, "At index $i / ${n * l}")
            }
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    @Test
    fun testMultiplyDoubleMatrices() = runBlocking {
        memScoped {
            val n = 1000
            val m = 1000 * 2
            val l = 1000 * 4

            val mat1 = allocArray<DoubleVar>(n * m)
            val mat2 = allocArray<DoubleVar>(m * l)
            val result = allocArray<DoubleVar>(n * l)

            val mat1Values = DoubleArray(n * m) { Random.nextDouble() }
            val mat2Values = DoubleArray(m * l) { Random.nextDouble() }

            for (i in 0 until n * m) {
                mat1[i] = mat1Values[i]
            }
            for (i in 0 until m * l) {
                mat2[i] = mat2Values[i]
            }

            val timeCPP = measureTime {
                multiplyDoubleMatrices(n, m, l, mat1, mat2, result)
            }

            val expected : DoubleArray
            val timeKotlin = measureTime {
                expected = kotlinMultiply(n, m, l, mat1Values, mat2Values)
            }

            println("Time CPP: $timeCPP")
            println("Time Kotlin: $timeKotlin")

            for (i in 0 until n * l) {
                assertEquals(expected[i], result[i], 0.001, "At index $i / ${n * l}")
            }
        }
    }
}
