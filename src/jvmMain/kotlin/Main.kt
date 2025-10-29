package matrixMultiply

import kotlin.random.Random

fun main() {
    val mat1 = Matrix(4, 4, Array<Int>(16) { Random.nextInt() })
    val mat2 = Matrix(4, 4, Array<Int>(16) { Random.nextInt() })
    val r = mat1 * mat2
    println(r)
}


