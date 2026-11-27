# Dockerfile usado por docker-maven-plugin (mvn package -Pdocker).
# A diferencia del Dockerfile de la raíz, aquí no se compila nada: el plugin
# copia el JAR ejecutable (shade) generado por Maven al directorio "maven/"
# del contexto de construcción.
FROM eclipse-temurin:17-jre

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libxext6 libxrender1 libxtst6 libxi6 libxrandr2 \
        libfreetype6 fontconfig fonts-dejavu-core \
        xvfb x11vnc \
    && rm -rf /var/lib/apt/lists/* \
    && useradd --create-home --shell /usr/sbin/nologin appuser

WORKDIR /app
COPY maven/*.jar /app/threeinarow.jar
COPY docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod 755 /usr/local/bin/docker-entrypoint.sh

USER appuser
ENV DISPLAY=:99 \
    SCREEN_GEOMETRY=500x350x24 \
    VNC_PORT=5900
# Sin EXPOSE: VNC (5900) es un servicio de administración (Sonar docker:S6473).
# El puerto se publica de forma explícita y solo en local con -p 127.0.0.1:5900:5900.
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
CMD []