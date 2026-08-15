FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# 💡 Se o CSV precisar estar acessível fisicamente no container fora do JAR, 
# certifique-se de copiá-lo também para o estágio final, por exemplo:
COPY --from=build /app/src/main/resources /app/src/main/resources

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]