FROM openjdk:14-jdk-slim

ARG JAR_FILE=stock-price-service.jar

VOLUME /tmp
EXPOSE 7000
ADD target/${JAR_FILE} ${JAR_FILE}
RUN sh -c 'touch /${JAR_FILE}'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/stock-price-service.jar"]
