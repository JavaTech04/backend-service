```text
    _   __                     __  __                           _    __     
   / | / /___  ____  ____ _   / / / /___  ____ _____  ____ _   | |  / /_  __
  /  |/ / __ \/ __ \/ __ `/  / /_/ / __ \/ __ `/ __ \/ __ `/   | | / / / / /
 / /|  / /_/ / / / / /_/ /  / __  / /_/ / /_/ / / / / /_/ /    | |/ / /_/ / 
/_/ |_/\____/_/ /_/\__, /  /_/ /_/\____/\__,_/_/ /_/\__, /     |___/\__,_/  
                  /____/                           /____/                   
Backend service for learning
Start date: 23/01/2025

Website: https://bit.ly/nong-hoang-vu-java
Youtube: https://www.youtube.com/@javatech04
Facebook: https://www.facebook.com/NongHoangVu04
```
## Prerequisite
- Cài đặt JDK 17+
- Install Maven 3.5+
- Install IntelliJ

## Technical Stacks
- Java 17
- Spring Boot 3.3.4
- PostgresSQL
- Kafka
- Redis
- Maven 3.5+
- Lombok
- DevTools
- Docker, Docker compose

## Build application
```bash
mvn clean package -P dev|test|uat|prod
```

## Run application
- Maven statement
```bash
./mvnw spring-boot:run
```
- Jar statement
```bash
java -jar target/backend-service.jar
```

- Docker
```bash
docker build -t backend-service .
docker run -d backend-service:latest backend-service
```

## Package application
```bash
docker build -t backend-service .
```

## Test
- Check health with **cURL**
```bash
curl --location 'http://localhost:8080/actuator/health'
-- Response --
{
  "status": "UP"
} 
```
- Use swagger UI to test [Run swagger UI](http://localhost:8080/swagger-ui/index.html)

## Package Spring Boot application with Jib
```xml
...
<properties>
    <java.version>17</java.version>
    <image.path>registry.hub.docker.com/nonghoangvu</image.path>
</properties>
        ...
<build>
<plugins>
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <configuration>
            <excludes>
                <exclude>
                    <groupId>org.projectlombok</groupId>
                    <artifactId>lombok</artifactId>
                </exclude>
            </excludes>
        </configuration>
    </plugin>
    <plugin>
        <groupId>com.google.cloud.tools</groupId>
        <artifactId>jib-maven-plugin</artifactId>
        <version>2.8.0</version>
        <configuration>
            <from>
                <image>openjdk:17-alpine</image>
            </from>
            <to>
                <image>${image.path}/${project.artifactId}:20241213</image>
                <!-- Setting dockerhub account -->
                <auth>
                    <!--suppress UnresolvedMavenProperty -->
                    <username>${env.DOCKER_USERNAME}</username>
                    <!--suppress UnresolvedMavenProperty -->
                    <password>${env.DOCKER_PASSWORD}</password>
                </auth>
            </to>
            <container>
                <ports>
                    <port>8080</port>
                </ports>
                <environment>
                    <SPRING_PROFILES_ACTIVE>prod</SPRING_PROFILES_ACTIVE>
                </environment>
            </container>
        </configuration>
    </plugin>
</plugins>
<finalName>backend-service</finalName>
</build>
```
- Base image: `openjdk:17-alpine`
- Tên image: `${image.path}/${project.artifactId}:20241213`
  - image.path: `registry.hub.docker.com/nonghoangvu`
  - project.artifactId: backend-service
  - tag: 20241213 (or latest)
  - auth: Tài khoản để login `hub.docker.com`. `env.DOCKER_USERNAME` và `env.DOCKER_PASSWORD` được lấy từ biến môi trường.
  - Mở port `8080`
- Thêm Environment variable `SPRING_PROFILES_ACTIVE=prod`

### Build Image with Jib
- Build và đưa image vào Docker daemon (local Docker): Lệnh này sẽ tạo image và lưu vào Docker daemon trên máy của chúng ta.
```bash
mvn package jib:dockerBuild 
```
- Build và push image trực tiếp lên container registry: Nếu bạn muốn đẩy image lên một container registry (ví dụ: Docker Hub, Google Container Registry, AWS ECR), sử dụng lệnh sau:
```bash
mvn package jib:build 
```