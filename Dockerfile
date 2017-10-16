FROM cogniteev/oracle-java:java9

MAINTAINER Alex Bondar <abondar1992@gmail.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/app/TodoList-1.0.jar"]

# Add Maven dependencies (not shaded into the artifact; Docker-cached)
ADD /target /app
# Add the service itself
ARG JAR_FILE