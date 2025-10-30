package matrixMultiply

object MatrixMultiplierCPP {
    init {
        System.loadLibrary("matMul")
    }

    @JvmStatic
    external fun multiplyIntMatrices(
        n: Int,
        m: Int,
        l: Int,
        matrixA: IntArray,
        matrixB: IntArray,
        result: IntArray
    )

    @JvmStatic
    external fun multiplyFloatMatrices(
        n: Int,
        m: Int,
        l: Int,
        matrixA: FloatArray,
        matrixB: FloatArray,
        result: FloatArray
    )

    @JvmStatic
    external fun multiplyDoubleMatrices(
        n: Int,
        m: Int,
        l: Int,
        matrixA: DoubleArray,
        matrixB: DoubleArray,
        result: DoubleArray
    )

    @Suppress("UNCHECKED_CAST")
    fun <T> multiplyMatrices(n: Int, m: Int, l: Int, matrixA: Array<T>, matrixB: Array<T>) : Array<T> {
        when (matrixA.first()!!::class) {
            Int::class -> {
                val a = matrixA as Array<Int>
                val b = matrixB as Array<Int>
                val res = IntArray(n * l) { 0 }
                multiplyIntMatrices(n, m, l, a.toIntArray(), b.toIntArray(), res)
                return res.toTypedArray() as Array<T>
            }
            Float::class -> {
                val a = matrixA as Array<Float>
                val b = matrixB as Array<Float>
                val res = FloatArray(n * l) { 0f }
                multiplyFloatMatrices(n, m, l, a.toFloatArray(), b.toFloatArray(), res)
                return res.toTypedArray() as Array<T>
            }
            Double::class -> {
                val a = matrixA as Array<Double>
                val b = matrixB as Array<Double>
                val res = DoubleArray(n * l) { 0.0 }
                multiplyDoubleMatrices(n, m, l, a.toDoubleArray(), b.toDoubleArray(), res)
                return res.toTypedArray() as Array<T>
            }
            else -> throw IllegalArgumentException("Unsupported type")
        }
    }
}