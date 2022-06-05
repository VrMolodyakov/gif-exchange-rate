FROM openjdk:11
COPY build/libs/gifAndExchangeRate-0.0.1-SNAPSHOT.jar gif-and-exchange-rate.jar
CMD ["java","-jar","gif-and-exchange-rate.jar"]