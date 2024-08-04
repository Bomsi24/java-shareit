# первый (вспомогательный) этап с именем builder
FROM amazoncorretto:21-alpine as builder
# Устанавливаем переменные окружения для подключения к базе данных PostgreSQL
ENV DB_URL=jdbc:postgresql:mem:shareit
ENV DB_USER=test
ENV DB_PASSWORD=test

COPY target/*.jar /app/app.jar

COPY src/main/resources/shema.sql /docker-entrypoint-initdb.d/

RUN apk update && apk add postgresql-client

CMD ["java", "-jar", "/app/app.jar"]