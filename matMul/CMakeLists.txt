cmake_minimum_required(VERSION 3.22)
project(matMul VERSION 1.0 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 14)

# Core C++ source files (without JNI wrapper)
file(GLOB CORE_SOURCE_FILES src/matrixNative.cpp)

# Add the library with core functionality
add_library(${PROJECT_NAME}
        STATIC
        SHARED
        ${CORE_SOURCE_FILES})

target_include_directories(${PROJECT_NAME} PUBLIC include)

# Optionally build with JNI support
option(BUILD_JNI "Build with JNI support" ON)

if(BUILD_JNI)
    find_package(JNI)

    if(JNI_FOUND)
        # Add JNI wrapper to the library
        target_sources(${PROJECT_NAME} PRIVATE src/matrixJNI.cpp)
        target_include_directories(${PROJECT_NAME} PRIVATE ${JNI_INCLUDE_DIRS})
        message(STATUS "JNI found - building with JNI support")
    else()
        message(WARNING "JNI not found - building without JNI wrapper")
    endif()
endif()

