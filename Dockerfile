# Базовый образ, содержащий Java 21
FROM tomcat:10-jdk21

ENV POSTGRES_DATASOURCE_URL='jdbc:postgresql://postgres-container.docker_default:5432/product_db?createDatabaseIfNotExist=true'
ENV URL='openapi.json'
#ENV JAVA_TOOL_OPTIONS=-javaagent:/app/lib/spring-instrument-6.1.9.jar

RUN apt-get update && apt-get install -y netcat-traditional

RUN rm -rf /usr/local/tomcat/webapps/*

# Копирование WAP-файла приложения в контейнер
COPY ./target/ProductApplication-1.0.0-SNAPSHOT.war '/usr/local/tomcat/webapps/api#v1.war'

# Выставление порта Tomcat
EXPOSE 8080
