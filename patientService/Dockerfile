FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/patientService-1.0.0-SNAPSHOT.jar /app/patient-service.jar

EXPOSE 8081

CMD ["java", "-jar", "patient-service.jar"]