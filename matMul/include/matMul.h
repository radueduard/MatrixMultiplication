#pragma once

#ifdef __cplusplus
extern "C"
{
#endif

int multiplyIntMatrices(int n, int m, int l, int* mat1, int* mat2, int* result);

/**
 *
 * @param n number of rows in matrix a
 * @param m number of columns in matrix a and rows in matrix b
 * @param l number of columns in matrix b
 * @param mat1 pointer to the first matrix (size n x m) as a flat array
 * @param mat2 pointer to the second matrix (size m x l) as a flat array
 * @param result pointer to the result matrix (size n x l) as a flat array
 * @return 0 on success, non-zero on failure
 */
int multiplyFloatMatrices(int n, int m, int l, float* mat1, float* mat2, float* result);

/**
 *
 * @param n number of rows in matrix a
 * @param m number of columns in matrix a and rows in matrix b
 * @param l number of columns in matrix b
 * @param mat1 pointer to the first matrix (size n x m) as a flat array
 * @param mat2 pointer to the second matrix (size m x l) as a flat array
 * @param result pointer to the result matrix (size n x l) as a flat array
 * @return 0 on success, non-zero on failure
 */
int multiplyDoubleMatrices(int n, int m, int l, double* mat1, double* mat2, double* result);

#ifdef __cplusplus
}
#endif