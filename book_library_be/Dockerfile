## build stage ##
FROM maven:3.9.8-amazoncorretto-17 AS build

WORKDIR /app
COPY . .
RUN mvn install -DskipTests=true

## run stage ##
FROM amazoncorretto:17.0.7-alpine

WORKDIR /app
COPY --from=build /app/target/book_library-1.0-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

