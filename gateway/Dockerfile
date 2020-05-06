## builder
FROM openjdk:8-jdk-alpine as builder
WORKDIR /app
COPY build/libs build/libs/
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

## production
FROM openjdk:8-jdk-alpine as production
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG DEPENDENCY=/app/build/dependency

COPY --from=builder ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=builder ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=builder ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","-Dspring.profiles.active=docker","ke.co.infiware.gateway.GatewayApplicationKt"]