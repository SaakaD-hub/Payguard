#!/bin/bash
set -e

echo "🚀 Setting up PayGuard Maven project structure..."


# Services to create
declare -A services=(
    ["api-gateway"]="apigateway"
    ["user-service"]="user"
    ["payment-service"]="payment"
    ["fraud-engine"]="fraud"
    ["notification-service"]="notification"
    ["reconciliation-service"]="reconciliation"
)

# Create structure for each service
for service in "${!services[@]}"; do
    package_name="${services[$service]}"
    
    echo "📦 Creating $service..."
    
    # Java package structure
    mkdir -p "$service/src/main/java/com/payguard/$package_name"/{controller,service,repository,model,dto,config,exception}
    
    # Resources
    mkdir -p "$service/src/main/resources"/{db/migration,templates,static}
    
    # Test structure
    mkdir -p "$service/src/test/java/com/payguard/$package_name"
    mkdir -p "$service/src/test/resources"
    
    # Create minimal pom.xml if doesn't exist
    if [ ! -f "$service/pom.xml" ]; then
        cat > "$service/pom.xml" <<EOF
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.payguard</groupId>
        <artifactId>payguard-parent</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>$service</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>${service^}</name>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
    </dependencies>
</project>
EOF
    fi
    
    # Create Application.java
    app_class="${package_name^}ServiceApplication"
    if [ ! -f "$service/src/main/java/com/payguard/$package_name/${app_class}.java" ]; then
        cat > "$service/src/main/java/com/payguard/$package_name/${app_class}.java" <<EOF
package com.payguard.$package_name;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ${app_class} {
    public static void main(String[] args) {
        SpringApplication.run(${app_class}.class, args);
    }
}
EOF
    fi
    
    # Create application.yml
    if [ ! -f "$service/src/main/resources/application.yml" ]; then
        cat > "$service/src/main/resources/application.yml" <<EOF
server:
  port: 808${services[$service]:0:1}

spring:
  application:
    name: $service
EOF
    fi
    
    # Create Dockerfile
    if [ ! -f "$service/Dockerfile" ]; then
        cat > "$service/Dockerfile" <<EOF
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 808${services[$service]:0:1}
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
    fi
    
    echo "✅ $service structure created"
done

echo ""
echo "🎉 Setup complete!"
echo ""
echo "Next steps:"
echo "1. cd payguard"
echo "2. mvn clean install    # Build all services"
echo "3. cd user-service && mvn spring-boot:run    # Run a service"