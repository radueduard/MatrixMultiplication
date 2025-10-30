FROM --platform=linux/amd64 gradle:8.5-jdk17

USER root

# Install C++ build tools, dependencies and OpenBLAS
RUN apt-get update && apt-get install -y \
    g++ \
    make \
    wget \
    cmake \
    software-properties-common \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy Gradle build files first for better caching
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./

# Download Gradle dependencies
RUN gradle dependencies --no-daemon || true

# Now copy the rest of the source code
COPY . .

ENV LD_LIBRARY_PATH=/app/matMul/cmake-build-release

# Build Kotlin Native application
RUN gradle build --no-daemon

# Run both JVM (JNI) and Native tests
CMD ["gradle", "jvmTest", "nativeTest", "--no-daemon"]
