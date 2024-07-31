FROM amazoncorretto:22-alpine-jdk AS builder
COPY . .
RUN dos2unix gradlew
RUN ./gradlew bootJar

FROM amazoncorretto:22-alpine
COPY --from=builder /build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]