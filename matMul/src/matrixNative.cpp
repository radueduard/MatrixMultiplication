#include "matMul.h"

#include <cblas.h>
#include <type_traits>
#include <vector>
#include <thread>

template <typename T, typename = std::enable_if_t<std::is_arithmetic<T>::value>>
void multiplyMatrices(const int n, const int m, const int l, T* mat1, T* mat2, T* result) {
    auto* mat2T = new T[m * l];
    for (int i = 0; i < m; ++i) {
        for (int j = 0; j < l; ++j) {
            mat2T[i * l + j] = mat2[j * m + i];
        }
    }

    std::vector<std::thread> threads;
    for (int i = 0; i < n; ++i) {
        threads.emplace_back([mat1, mat2T, result, n, m, l, i]() {
            register T* mat1_cur = mat1 + i * m;
            register T* mat2_cur = mat2T;
            register T* result_cur = result + i * l;

            for (register int j = 0; j < l; ++j) {
                for (register int k = 0; k < m; ++k) {
                    *result_cur += *mat1_cur * *mat2_cur;
                    mat1_cur++;
                    mat2_cur++;
                }
                result_cur++;
                mat1_cur -= m;
            }
        });
    }

    for (auto& thread : threads) {
        thread.join();
    }

    delete[] mat2T;
}

extern "C" {
    int multiplyIntMatrices(const int n, const int m, const int l, int* mat1, int* mat2, int* result) {
        multiplyMatrices<int>(n, m, l, mat1, mat2, result);
        return 0;
    }

    int multiplyFloatMatrices(const int n, const int m, const int l, float* mat1, float* mat2, float* result) {
//         cblas_sgemm(CblasRowMajor, CblasNoTrans, CblasNoTrans,
//                     n, l, m,
//                     1.0f,
//                     mat1, m,
//                     mat2, l,
//                     0.0f,
//                     result, l);

        multiplyMatrices<float>(n, m, l, mat1, mat2, result);
        return 0;
    }

    int multiplyDoubleMatrices(const int n, const int m, const int l, double* mat1, double* mat2, double* result) {
//        cblas_dgemm(CblasRowMajor, CblasNoTrans, CblasNoTrans,
//                    n, l, m,
//                    1.0f,
//                    mat1, m,
//                    mat2, l,
//                    0.0f,
//                    result, l);
        multiplyMatrices<double>(n, m, l, mat1, mat2, result);
        return 0;
    }
}