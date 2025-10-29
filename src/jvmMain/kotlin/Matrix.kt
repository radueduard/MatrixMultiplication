package matrixMultiply

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

        val resultArray = MatrixMultiplierCPP.multiplyMatrices(rows, cols, other.cols, data, other.data)
        return Matrix(rows, other.cols, resultArray)
    }
}