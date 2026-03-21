FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/fraud-engine-*.jar app.jar
#COPY src/main/resources/models/fraud_model.onnx /app/models/fraud_model.onnx
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
