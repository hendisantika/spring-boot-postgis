# Multi-stage build for Indonesia Map Application
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application (migrations are included in the JAR)
RUN ./mvnw clean package -DskipTests -Dmaven.test.skip=true

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Install curl for healthcheck
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -g 1000 appuser && \
    adduser -D -u 1000 -G appuser appuser

# Copy JAR from builder (migrations are inside the JAR)
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

USER appuser

# Environment variables (can be overridden at runtime)
ENV SPRING_PROFILES_ACTIVE=dev \
    SERVER_PORT=2000

# Expose port
EXPOSE 8000

# Health check using curl (longer start-period for migrations)
HEALTHCHECK --interval=30s --timeout=10s --start-period=180s --retries=3 \
    CMD curl -f http://localhost:${SERVER_PORT}/ || exit 1

# Run application with JVM options
ENTRYPOINT ["java", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
