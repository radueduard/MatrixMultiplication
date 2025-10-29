FROM gradle:8.5-jdk17

USER root

# Install C++ build tools, dependencies and OpenBLAS
RUN apt-get update && apt-get install -y \
    g++ \
    make \
    wget \
    cmake \
    libopenblas-dev \
    software-properties-common \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

# Build C++ library
RUN cd hello && \
    mkdir -p cmake-build-release && \
    cd cmake-build-release && \
    cmake .. && \
    make

ENV LD_LIBRARY_PATH=/app/hello/cmake-build-release

# Build Kotlin Native application
RUN gradle build --no-daemon

# Run the compiled native binary
#CMD ["./build/bin/native/releaseExecutable/KotlinNativeTemplate.kexe"]

# Run the tests
CMD ["gradle", "test", "--no-daemon"]