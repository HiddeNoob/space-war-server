FROM maven:latest

ENV MAVEN_CONFIG=/home/server/.m2

RUN useradd -m server

WORKDIR /app

COPY src src
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .
COPY LICENSE .

RUN chmod +x mvnw
RUN chown -R server:server /app

USER server

EXPOSE 8080

CMD ["./mvnw", "spring-boot:run"]

HEALTHCHECK CMD curl -f -s http://localhost:8080/ > /dev/null || exit 1
