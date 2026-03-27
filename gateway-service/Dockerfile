FROM eclipse-temurin:17-jre-jammy

WORKDIR /app


RUN groupadd -r appuser && useradd -r -g appuser appuser \
    && mkdir -p /home/appuser/nacos/naming/public/failover \
    && chown -R appuser:appuser /home/appuser

# 切换到非 root 用户
USER appuser


COPY target/gateway-service-0.0.2-SNAPSHOT.jar app.jar

EXPOSE 9999

ENV JAVA_OPTS="\
    -XX:+UseContainerSupport \
    -XX:InitialRAMPercentage=35.0 \
    -XX:MaxRAMPercentage=70.0 \
    -XX:+UseSerialGC \
    -Xss384k \
    -XX:MaxMetaspaceSize=128m \
    -XX:+UseStringDeduplication \
    -XX:+ExitOnOutOfMemoryError \
    -Dspring.main.lazy-initialization=true \
    -Dserver.tomcat.threads.max=20"


ENTRYPOINT exec java $JAVA_OPTS -jar app.jar