FROM eclipse-temurin:19-jdk AS maven-build

WORKDIR /backend/

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

RUN ./mvnw dependency:go-offline

COPY ./src ./src

RUN ./mvnw clean install -DskipTests

FROM eclipse-temurin:19-jre

WORKDIR /backend/

EXPOSE 8080

COPY --from=maven-build /backend/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]