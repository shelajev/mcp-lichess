# Multi-stage build Dockerfile for Spring Boot application

# Stage 1: Build the application
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app

# Copy the POM file
COPY pom.xml .
# Copy the source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# Stage 2: build the engines
FROM --platform=$BUILDPLATFORM registry.access.redhat.com/ubi9/ubi-minimal:latest AS chessbuilder

ARG TARGETPLATFORM
# Install build tools for UBI (using microdnf)
RUN microdnf update -y && \
    microdnf install -y git make gcc-c++ wget ca-certificates && \
    microdnf install -y ninja-build meson clang && \
    microdnf clean all

WORKDIR /opt
RUN git clone --depth 1 --branch sf_17.1 \
      https://github.com/official-stockfish/Stockfish.git

WORKDIR /opt/Stockfish/src
# Compile Stockfish using the UBI's g++
# Detect architecture and build appropriate version
RUN if [ "$(uname -m)" = "arm64" ] || [ "$(uname -m)" = "aarch64" ]; then \
        make build ARCH=armv8; \
    else \
        make build ARCH=x86-64-modern; \
    fi

# lc0
RUN git clone -b release/0.31 --recurse-submodules \
        https://github.com/LeelaChessZero/lc0.git /lc0
WORKDIR /lc0

RUN ./build.sh && \
    strip build/release/lc0


# Stage 3: Create the final image
FROM registry.access.redhat.com/ubi9/openjdk-21:1.21

USER root
RUN microdnf update -y && \
    microdnf install -y expect zlib libstdc++ libatomic && \
    microdnf clean all

# Ensure the unversioned libz.so symlink exists
RUN ln -s /usr/lib64/libz.so.1 /usr/lib64/libz.so

# Run ldconfig to update the dynamic linker cache (good practice after adding symlinks)
RUN ldconfig

ENV LD_LIBRARY_PATH=/usr/lib64:${LD_LIBRARY_PATH}

# Copy Stockfish from the builder stage
COPY --from=chessbuilder /opt/Stockfish/src/stockfish /usr/local/bin/stockfish
# Copy CA bundle for NNUE if needed
COPY --from=chessbuilder /etc/pki/tls/certs/ca-bundle.crt /etc/pki/tls/certs/ca-bundle.crt
# copy lc0
COPY --from=chessbuilder /lc0/build/release/lc0 /usr/local/bin/lc0
# download the maia weights
RUN mkdir /maia && \
    for i in {1100..1900..100}; do curl -L -o /maia/maia-${i}.pb.gz "https://github.com/CSSLab/maia-chess/releases/download/v1.0/maia-${i}.pb.gz"; done


# Set working directory
WORKDIR /app
# Copy the JAR file from the build stage
COPY --from=build /app/target/mcp-lichess-0.0.1.jar app.jar

# Expose the default Spring Boot port
EXPOSE 8080

# Set the entrypoint
ENTRYPOINT ["java", "-jar", "app.jar"]
