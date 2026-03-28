FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 创建用户 + nacos缓存目录
RUN groupadd -r appuser && useradd -r -g appuser appuser \
    && mkdir -p /home/appuser/nacos/naming/public/failover \
    && chown -R appuser:appuser /home/appuser

USER appuser

COPY target/user-service-0.0.2-SNAPSHOT.jar app.jar

EXPOSE 8081

ENV JAVA_OPTS="\
    -XX:+UseContainerSupport \
    -XX:InitialRAMPercentage=30.0 \
    -XX:MaxRAMPercentage=60.0 \
    -XX:+UseSerialGC \
    -Xss384k \
    -XX:MaxMetaspaceSize=128m \
    -XX:+UseStringDeduplication \
    -XX:+ExitOnOutOfMemoryError \
    -Dspring.main.lazy-initialization=true \
    -Dspring.datasource.hikari.maximum-pool-size=5 \
    -Dserver.tomcat.threads.max=20"

ENTRYPOINT exec java $JAVA_OPTS -jar app.jar