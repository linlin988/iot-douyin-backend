FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

RUN groupadd -r appuser && useradd -r -g appuser appuser \
    && mkdir -p /home/appuser/nacos/naming/public/failover \
    && chown -R appuser:appuser /home/appuser

USER appuser

COPY target/content-service-0.0.2-SNAPSHOT.jar app.jar

EXPOSE 8082

ENV JAVA_OPTS="\
    -XX:+UseContainerSupport \
    -XX:InitialRAMPercentage=30.0 \
    -XX:MaxRAMPercentage=65.0 \
    -XX:+UseSerialGC \
    -Xss384k \
    -XX:MaxMetaspaceSize=128m \
    -XX:+UseStringDeduplication \
    -XX:+ExitOnOutOfMemoryError \
    -Dspring.main.lazy-initialization=true \
    -Dserver.tomcat.threads.max=30 \
    -Dspring.codec.max-in-memory-size=2MB"

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar