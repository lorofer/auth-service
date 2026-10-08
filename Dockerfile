FROM eclipse-temurin:21-jdk AS builder

WORKDIR /build/auth
COPY auth/gradlew auth/settings.gradle.kts auth/build.gradle.kts ./
COPY auth/gradle ./gradle
RUN ./gradlew dependencies --no-daemon -q
COPY proto /build/proto
COPY auth/src ./src
RUN ./gradlew bootJar --no-daemon && mv build/libs/*.jar /app.jar

FROM eclipse-temurin:21-jre
COPY --from=builder /app.jar /app.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "/app.jar"]
