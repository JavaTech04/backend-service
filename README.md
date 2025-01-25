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

## Setup ELK
### 1. Application
- Add dependency
```bash
<!-- Logging -->
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>6.6</version>
</dependency> 
```

- Create file `logback-spring.xml` at folder `resources`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/base.xml"/>
    <appender name="logstash" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <!-- <destination>localhost:5600</destination>--> <!--local env-->
        <destination>${LOGSTASH_HOST:-localhost:5600}</destination> <!--docker env-->
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
            <providers>
                <mdc/>
                <context/>
                <logLevel/>
                <loggerName/>
                <pattern>
                    <pattern>
                        {
                        "appName": "backend-service"
                        }
                    </pattern>
                </pattern>
                <threadName/>
                <message/>
                <logstashMarkers/>
                <stackTrace/>
            </providers>
        </encoder>
    </appender>
    <root level="info">
        <appender-ref ref="logstash"/>
    </root>
</configuration>
```

### 2. Setup ELK
- Create file `logstash.conf` before run elk
```bash
input {
  tcp {
    # Socket port
    port => 5600
    codec => json
  }
}

output {
  elasticsearch {
    hosts => ["${XPACK_MONITORING_ELASTICSEARCH_HOSTS}"]
    index => "elk-index-%{appName}"
  }
}
```
- Add to `docker-compose.yml`
```bash
  elastic-search:
    image: elasticsearch:7.14.1
    container_name: elasticsearch
    restart: always
    ports:
      - "9200:9200"
    environment:
      - discovery.type=single-node
    networks:
      - default
  kibana:
      image: kibana:7.14.1
      container_name: kibana
      restart: always
      ports:
        - "5601:5601"
      environment:
        - ELASTICSEARCH_HOSTS=http://elastic-search:9200
      networks:
        - default

  logstash:
      image: logstash:7.14.1
      container_name: logstash
      restart: always
      ports:
        - "5600:5600" # Socket port
        - "5044:5044"
      #      - "9600:9600"
      volumes:
        - ./logstash.conf:/usr/share/logstash/pipeline/logstash.conf
      environment:
        - XPACK_MONITORING_ELASTICSEARCH_HOSTS=http://elastic-search:9200
        - XPACK_MONITORING_ENABLED=true
      networks:
        - default
```

- Test
```bash
$ mvn package
$ docker compose up -d --build 
$ docker compose ps

Remove container running
$ docker compose down -v
```

### 3. Check & Visualize Log
- Check [Elasticsearch](http://localhost:9200/_cat/indices)
```
green  open .geoip_databases                T76nPl3XSGS5xqDu5mw_tg 1 0  37   0  35.5mb  35.5mb
yellow open elk-index-backend-service       aNf0inMdS5-ntrkS8p5Aqg 1 1 734   0   753kb   753kb -> It's here
green  open .apm-custom-link                _zOSYkkQTMKDJBIUqnkX8A 1 0   0   0    208b    208b
green  open .kibana_task_manager_7.14.1_001 EHQeLzZaQ7GfEeRco86BdA 1 0  14 141 134.8kb 134.8kb
green  open .apm-agent-configuration        4pKgs1HpT7ml0pWM3EN7GQ 1 0   0   0    208b    208b
green  open .kibana_7.14.1_001              ABc53CbETH6MH7nQuBMTOg 1 0  14   1   2.1mb   2.1mb
green  open .kibana-event-log-7.14.1-000001 nI2_XjuaRhGKd3z478JymQ 1 0   2   0    11kb    11kb
green  open .tasks                          oKNbLGasRXCdJChUc2MZiQ 1 0   2   0  13.8kb  13.8kb 
```

- Create [index pattern](http://localhost:5601/app/management/kibana/indexPatterns)
![Step 1](https://raw.githubusercontent.com/JavaTech04/backend-service/refs/heads/setup-logging-with-elk/images/Create%20index%20pattern%201.png)
![Step 2](https://github.com/JavaTech04/backend-service/blob/setup-logging-with-elk/images/Create%20index%20pattern%202.png?raw=true)
![Step 3](https://github.com/JavaTech04/backend-service/blob/setup-logging-with-elk/images/Create%20index%20pattern%203.png?raw=true)
![Step 4](https://github.com/JavaTech04/backend-service/blob/setup-logging-with-elk/images/Create%20index%20pattern%204.png?raw=true)
![Step 5](https://github.com/JavaTech04/backend-service/blob/setup-logging-with-elk/images/Create%20index%20pattern%205.png?raw=true)
![Step 6](https://github.com/JavaTech04/backend-service/blob/setup-logging-with-elk/images/Create%20index%20pattern%206.png?raw=true)