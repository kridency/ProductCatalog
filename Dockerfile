# Базовый образ, содержащий Java 21
FROM tomcat:10-jdk21

ARG RUN_PROFILE=docker

ARG SPRING_VERSION=7.0.3

ARG POSTGRES_DATASOURCE_URL='jdbc:postgresql://postgres-container:5432/product_db?currentSchema=custom&createDatabaseIfNotExist=true'

RUN apt-get update && apt-get install -y netcat-traditional

RUN rm -rf $CATALINA_HOME/webapps/*

# Копирование WAR-файла приложения в контейнер
COPY ./ProductApplication/target/ProductApplication-1.0.0-SNAPSHOT.war $CATALINA_HOME/webapps/'api#v1.war'

COPY ./ProductApplication/target/ProductApplication-1.0.0-SNAPSHOT/WEB-INF/lib/spring-instrument-$SPRING_VERSION.jar $CATALINA_HOME/javaagent/

ENV JAVA_TOOL_OPTIONS="-javaagent:${CATALINA_HOME}/javaagent/spring-instrument-${SPRING_VERSION}.jar"

ENV SPRING_VERSION="${SPRING_VERSION}"

ENV POSTGRES_DATASOURCE_URL="${POSTGRES_DATASOURCE_URL}"

# Выставление порта Tomcat
EXPOSE 8080
