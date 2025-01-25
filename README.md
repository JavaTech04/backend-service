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

## Configure Grafana + Prometheus into Spring Boot
### 1. Configure Spring Boot
- pom.xml
```xml
<!-- Health check -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<!-- Monitoring -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>
```

- application.yml
```yaml
management:
  endpoints:
    web:
      exposure:
        include: '*'
        # include: health,prometheus,metrics
```

### 2. Install and Configure Prometheus
#### Install Prometheus with HomeBrew (MACOS)
- Update HomeBrew
```bash
$ brew update
```
- Install Prometheus
```bash
$ brew install prometheus 
```
- Create Job: Create Prometheus.yml file in/OPT/Homebrew/ETC with content as described below
```bash
$ cd /opt/homebrew/etc
$ vim prometheus.yml 
```
```bash
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: "prometheus"
    static_configs:
      - targets: ["localhost:9090"]
  - job_name: 'backend-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: [ 'backend-service:8080' ]
        labels:
          application: 'Backend Service'
```
- Start Prometheus
```bash
$ brew services start prometheus 
```
- Stop Prometheus
```bash
$ brew services stop prometheus
```
- Uninstall Prometheus
```bash
$ brew uninstall prometheus
```

#### Install Prometheus with Docker compose
- Create file `prometheus.yml` located in `backend-Service`
```yaml
# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: "prometheus"
    static_configs:
      - targets: ["localhost:9090"]
  - job_name: 'backend-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: [ 'backend-service:8080' ]
        labels:
          application: 'Backend Service'
```

```yaml
# docker-compose.yml
services:

  prometheus:
    image: prom/prometheus
    container_name: prometheus
    restart: unless-stopped
    command:
      - --config.file=/etc/prometheus/prometheus.yml
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - '9090:9090'

networks:
  default:
    name: api-network
```

```bash
$ docker-compose up -d prometheus
# build
$ mvn package
$  docker-compose up -d --build backend-service
# logs
$ docker-compose logs -tf backend-service
```
### View Backend Service information at [Prometheus](http://localhost:9090/targets)
- image
### View Endpoint [backend-service](http://127.0.0.1:8080/actuator/prometheus)
- image

## 3. Install and configure Grafana
### Cài đăt Grafana với HomeBrew (MACOS)
- Update HomeBrew
```bash
$ brew update
```

- Install Grafana
```bash
$ brew install grafana
```

- Start Grafana
```bash
$ brew services start grafana
```

- Stop Grafana
```bash
$ brew services stop grafana
```

- Uninstall Grafana
```bash
$ brew uninstall grafana
```

### Install Grafana with Docker Compose
```yaml
services:

  grafana:
    image: grafana/grafana
    container_name: grafana
    restart: unless-stopped
    environment: # account: grafana/password
      - GF_SECURITY_ADMIN_USER=grafana
      - GF_SECURITY_ADMIN_PASSWORD=password
    ports:
      - '3000:3000'
    links:
      - prometheus
    volumes:
      - grafana:/var/lib/grafana

networks:
  default:
    name: api-network

volumes:
  grafana:
```
```bash
docker-compose up -d grafana
```
#### Configuration grafana
- View information [Grafana](http://localhost:3000/login) log in with the account `grafana/password`
- [sample-dashboard.json](https://drive.google.com/file/d/1yPcbPJ7jnhtB2rMB3RWzT0cpNEWPvHa0/view)
