import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import matMul.*

@OptIn(ExperimentalForeignApi::class)
@Suppress("UNCHECKED_CAST")
fun <T: Number> multiplyMatrices(rowsA: Int, colsA: Int, colsB: Int, a: Array<T>, b: Array<T>): Array<T> {
    return when (a[0]::class) {
        Int::class -> {

            val aI = (a as Array<Int>).toIntArray()
            val bI = (b as Array<Int>).toIntArray()
            val result = IntArray(rowsA * colsB)
            multiplyIntMatrices(rowsA, colsA, colsB, aI.refTo(0), bI.refTo(0), result.refTo(0))
            result.toTypedArray() as Array<T>
        }
        Float::class -> {
            val aF = (a as Array<Float>).toFloatArray()
            val bF = (b as Array<Float>).toFloatArray()
            val result = FloatArray(rowsA * colsB)
            multiplyFloatMatrices(rowsA, colsA, colsB, aF.refTo(0), bF.refTo(0), result.refTo(0))
            result.toTypedArray() as Array<T>
        }
        Double::class -> {
            val aD = (a as Array<Double>).toDoubleArray()
            val bD = (b as Array<Double>).toDoubleArray()
            val result = DoubleArray(rowsA * colsB)
            multiplyDoubleMatrices(rowsA, colsA, colsB, aD.refTo(0), bD.refTo(0), result.refTo(0))
            result.toTypedArray() as Array<T>
        }
        else -> throw IllegalArgumentException("Unsupported matrix element type: ${a[0]::class}")
    }
}

class Matrix<T : Number>(val rows: Int, val cols: Int, var data : Array<T>) {
    init {
        require(rows > 0 && cols > 0) { "Matrix dimensions must be positive. Given: ${rows}x${cols}" }
    }

    override fun toString(): String = data.toList().chunked(cols).joinToString("\n") { it.joinToString(", ") }

    operator fun get(row: Int, col: Int): T = data[row * cols + col]
    operator fun set(row: Int, col: Int, value: T) { data[row * cols + col] = value }

    operator fun times(other: Matrix<T>): Matrix<T> {
        if (cols != other.rows) {
            throw IllegalArgumentException("Invalid matrix dimensions: cannot multiply ${rows}x${cols} by ${other.rows}x${other.cols}")
        }
        return Matrix(rows, other.cols, multiplyMatrices(rows, cols, other.cols, data, other.data))
    }
}