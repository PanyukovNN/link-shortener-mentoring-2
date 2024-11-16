FROM amazoncorretto:17.0.7-alpine

COPY ./link-shortener-2*.jar ./link-shortener.jar

ENV TZ=Europe/Moscow

EXPOSE 8080

CMD ["java", "-jar", "./link-shortener.jar"]