# ===== Build stage =====
FROM amazoncorretto:25 AS build
# gradlew needs xargs, which the minimal Corretto image lacks
RUN yum install -y findutils && yum clean all
WORKDIR /app

# Warm the dependency cache in its own layer so code changes rebuild fast
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

COPY src ./src
RUN ./gradlew --no-daemon bootJar

# ===== Run stage =====
FROM amazoncorretto:25
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
