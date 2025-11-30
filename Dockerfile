# Базовый образ, содержащий Java 21
FROM tomcat:10-jdk21

#Директория приложения внутри контейнера
WORKDIR /app

ENV POSTGRES_DATASOURCE_URL=jdbc:postgresql://postgres-container.docker_default:5432/product_db
#ENV JAVA_TOOL_OPTIONS=-javaagent:/app/lib/spring-instrument-6.1.9.jar

RUN apt-get update && apt-get install -y netcat-traditional 

EXPOSE 8088

#Копирование JAP-файла приложения в контейнер
COPY ./target/ProductCatalog-1.0.0-SNAPSHOT-jar-with-dependencies.jar /app/product_catalog.jar
RUN mkdir -p /app/lib
#COPY ./target/lib/ /app/lib/

#Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "product_catalog.jar"]
