package matrixMultiply


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.test.*
import kotlin.time.measureTime

class MatrixTestsJVM {
    suspend inline fun kotlinMultiply(n: Int, m: Int, l: Int, a: IntArray, b: IntArray): IntArray = coroutineScope {
        val result = IntArray(n * l)

        val bT = IntArray(l * m)
        for (i in 0 until m) {
            for (j in 0 until l) {
                bT[i * l + j] = b[j * m + i]
            }
        }
        val processors = Runtime.getRuntime().availableProcessors()
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

        val processors = Runtime.getRuntime().availableProcessors()
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

        val processors = Runtime.getRuntime().availableProcessors()
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

    @Test
    fun testSmallIntMultiply() {
        val n = 2
        val m = 3
        val l = 2

        // Matrix A: 2x3
        // [2, 3, 4]
        // [1, 2, 3]
        val a = intArrayOf(2, 3, 4, 1, 2, 3)

        // Matrix B: 3x2
        // [1, 4]
        // [2, 5]
        // [3, 6]
        val b = intArrayOf(1, 4, 2, 5, 3, 6)

        val matrixA = Matrix(n, m, a.toTypedArray())
        val matrixB = Matrix(m, l, b.toTypedArray())

        val result = matrixA * matrixB
        val expected = runBlocking {
            kotlinMultiply(n, m, l, a, b)
        }

        for (i in 0 until n * l) {
            assertEquals(expected[i], result.data[i], "Mismatch at index $i")
        }
    }

    @Test
    fun testIdentityMatrixInt() {
        val n = 3

        // Identity matrix 3x3
        val identity = intArrayOf(
            1, 0, 0,
            0, 1, 0,
            0, 0, 1
        )

        // Test matrix 3x3
        val testMatrix = intArrayOf(
            5, 6, 7,
            8, 9, 10,
            11, 12, 13
        )

        val matrixA = Matrix(n, n, testMatrix.toTypedArray())
        val matrixI = Matrix(n, n, identity.toTypedArray())

        val result = matrixA * matrixI

        // Result should be the same as testMatrix
        for (i in testMatrix.indices) {
            assertEquals(testMatrix[i], result.data[i], "Identity multiplication failed at index $i")
        }
    }

    @Test
    fun testZeroMatrixInt() {
        val n = 2
        val m = 3
        val l = 2

        val a = intArrayOf(1, 2, 3, 4, 5, 6)
        val zero = intArrayOf(0, 0, 0, 0, 0, 0)

        val matrixA = Matrix(n, m, a.toTypedArray())
        val matrixZero = Matrix(m, l, zero.toTypedArray())

        val result = matrixA * matrixZero

        // Result should be all zeros
        for (i in result.data.indices) {
            assertEquals(0, result.data[i], "Zero matrix multiplication failed at index $i")
        }
    }

    @Test
    fun testMultiplyIntMatrices() {
        val n = 100
        val m = 150
        val l = 200

        val mat1Values = IntArray(n * m) { Random.nextInt(-1000, 1000) }
        val mat2Values = IntArray(m * l) { Random.nextInt(-1000, 1000) }

        val matrixA = Matrix(n, m, mat1Values.toTypedArray())
        val matrixB = Matrix(m, l, mat2Values.toTypedArray())

        val result: Matrix<Int>
        val timeCPP = measureTime {
            result = matrixA * matrixB
        }

        val expected: IntArray
        val timeKotlin = measureTime {
            expected = runBlocking {
                kotlinMultiply(n, m, l, mat1Values, mat2Values)
            }
        }

        println("Int matrices ($n x $m) * ($m x $l):")
        println("  CPP time: $timeCPP")
        println("  Kotlin time: $timeKotlin")

        for (i in 0 until n * l) {
            assertEquals(expected[i], result.data[i], "Mismatch at index $i")
        }
    }

    @Test
    fun testSmallFloatMultiply() {
        val n = 2
        val m = 2
        val l = 2

        val a = floatArrayOf(1.5f, 2.5f, 3.5f, 4.5f)
        val b = floatArrayOf(0.5f, 1.0f, 1.5f, 2.0f)

        val matrixA = Matrix(n, m, a.toTypedArray())
        val matrixB = Matrix(m, l, b.toTypedArray())

        val result = matrixA * matrixB
        val expected = runBlocking { kotlinMultiply(n, m, l, a, b) }

        for (i in 0 until n * l) {
            assertEquals(expected[i], result.data[i], 0.001f, "Mismatch at index $i")
        }
    }

    @Test
    fun testMultiplyFloatMatrices() {
        val n = 100
        val m = 150
        val l = 200

        val mat1Values = FloatArray(n * m) { Random.nextFloat() * 100 }
        val mat2Values = FloatArray(m * l) { Random.nextFloat() * 100 }

        val matrixA = Matrix(n, m, mat1Values.toTypedArray())
        val matrixB = Matrix(m, l, mat2Values.toTypedArray())

        val result: Matrix<Float>
        val timeCPP = measureTime {
            result = matrixA * matrixB
        }

        val expected: FloatArray
        val timeKotlin = measureTime {
            expected = runBlocking { kotlinMultiply(n, m, l, mat1Values, mat2Values) }
        }

        println("Float matrices ($n x $m) * ($m x $l):")
        println("  CPP time: $timeCPP")
        println("  Kotlin time: $timeKotlin")

        for (i in 0 until n * l) {
            assertEquals(expected[i], result.data[i], 0.1f, "Mismatch at index $i")
        }
    }

    @Test
    fun testSmallDoubleMultiply() {
        val n = 2
        val m = 2
        val l = 2

        val a = doubleArrayOf(1.5, 2.5, 3.5, 4.5)
        val b = doubleArrayOf(0.5, 1.0, 1.5, 2.0)

        val matrixA = Matrix(n, m, a.toTypedArray())
        val matrixB = Matrix(m, l, b.toTypedArray())

        val result = matrixA * matrixB
        val expected = runBlocking { kotlinMultiply(n, m, l, a, b) }

        for (i in 0 until n * l) {
            assertEquals(expected[i], result.data[i], 0.0001, "Mismatch at index $i")
        }
    }

    @Test
    fun testMultiplyDoubleMatrices() {
        val n = 100
        val m = 150
        val l = 200

        val mat1Values = DoubleArray(n * m) { Random.nextDouble() * 100 }
        val mat2Values = DoubleArray(m * l) { Random.nextDouble() * 100 }

        val matrixA = Matrix(n, m, mat1Values.toTypedArray())
        val matrixB = Matrix(m, l, mat2Values.toTypedArray())

        val result: Matrix<Double>
        val timeCPP = measureTime {
            result = matrixA * matrixB
        }

        val expected: DoubleArray
        val timeKotlin = measureTime {
            expected = runBlocking { kotlinMultiply(n, m, l, mat1Values, mat2Values) }
        }

        println("Double matrices ($n x $m) * ($m x $l):")
        println("  CPP time: $timeCPP")
        println("  Kotlin time: $timeKotlin")

        for (i in 0 until n * l) {
            assertEquals(expected[i], result.data[i], 0.001, "Mismatch at index $i")
        }
    }

    @Test
    fun testInvalidDimensions() {
        val matrixA = Matrix(2, 3, arrayOf(1, 2, 3, 4, 5, 6))
        val matrixB = Matrix(2, 2, arrayOf(1, 2, 3, 4))

        // 2x3 cannot be multiplied by 2x2
        assertFailsWith<IllegalArgumentException> {
            matrixA * matrixB
        }
    }

    @Test
    fun testSingleElementMatrices() {
        val matrixA = Matrix(1, 1, arrayOf(5))
        val matrixB = Matrix(1, 1, arrayOf(3))

        val result = matrixA * matrixB

        assertEquals(15, result.data[0])
    }

    @Test
    fun testRectangularMatrices() {
        // 1x3 * 3x1 = 1x1
        val matrixA = Matrix(1, 3, arrayOf(2, 3, 4))
        val matrixB = Matrix(3, 1, arrayOf(5, 6, 7))

        val result = matrixA * matrixB

        // 2*5 + 3*6 + 4*7 = 10 + 18 + 28 = 56
        assertEquals(56, result.data[0])
    }

    @Test
    fun testMatrixAccessors() {
        val data = arrayOf(1, 2, 3, 4, 5, 6)
        val matrix = Matrix(2, 3, data)

        assertEquals(1, matrix[0, 0])
        assertEquals(2, matrix[0, 1])
        assertEquals(3, matrix[0, 2])
        assertEquals(4, matrix[1, 0])
        assertEquals(5, matrix[1, 1])
        assertEquals(6, matrix[1, 2])

        matrix[0, 0] = 10
        assertEquals(10, matrix[0, 0])
    }

    @Test
    fun testMatrixToString() {
        val matrix = Matrix(2, 3, arrayOf(1, 2, 3, 4, 5, 6))
        val str = matrix.toString()

        assertTrue(str.contains("1"))
        assertTrue(str.contains("2"))
        assertTrue(str.contains("3"))
        assertTrue(str.contains("4"))
        assertTrue(str.contains("5"))
        assertTrue(str.contains("6"))
    }

    @Test
    fun testInvalidMatrixDimensions() {
        assertFailsWith<IllegalArgumentException> {
            Matrix(0, 3, arrayOf(1, 2, 3))
        }

        assertFailsWith<IllegalArgumentException> {
            Matrix(3, 0, arrayOf(1, 2, 3))
        }

        assertFailsWith<IllegalArgumentException> {
            Matrix(-1, 3, arrayOf(1, 2, 3))
        }
    }
}

