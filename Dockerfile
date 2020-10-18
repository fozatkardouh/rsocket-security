FROM openjdk:14-jdk-slim
VOLUME /tmp
EXPOSE 7000
ADD target/stock-price-service.jar stock-price-service.jar
RUN sh -c 'touch /stock-price-service.jar'
ENTRYPOINT ["java","-jar","/stock-price-service.jar"]
