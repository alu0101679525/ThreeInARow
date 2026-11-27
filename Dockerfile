# ---------------------------------------------------------------------------
# Etapa 1: compilación (Maven + JDK 17). Genera el JAR ejecutable (shade).
# Los tests de GUI necesitan una pantalla, por lo que se omiten aquí; se
# ejecutan aparte (ver README: "Docker").
# ---------------------------------------------------------------------------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Capa de dependencias: se reutiliza mientras no cambie el pom.xml
COPY pom.xml checkstyle.xml ./
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------------------------------------------------------------------------
# Etapa 2: base de ejecución (JRE 17 + librerías gráficas mínimas para Swing)
# ---------------------------------------------------------------------------
FROM eclipse-temurin:17-jre AS base
RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        libxext6 libxrender1 libxtst6 libxi6 libxrandr2 \
        libfreetype6 fontconfig fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

RUN useradd --create-home --shell /usr/sbin/nologin appuser
WORKDIR /app
COPY --from=build /build/target/threeinarow-1.0.0-SNAPSHOT.jar /app/threeinarow.jar

# ---------------------------------------------------------------------------
# Target "x11": muestra la ventana en el servidor X11 del anfitrión (Linux).
#   docker build --target x11 -t threeinarow:x11 .
# ---------------------------------------------------------------------------
FROM base AS x11
USER appuser
ENTRYPOINT ["java", "-jar", "/app/threeinarow.jar"]
CMD []

# ---------------------------------------------------------------------------
# Target por defecto "vnc": Xvfb + x11vnc, accesible por el puerto 5900 con
# cualquier cliente VNC (funciona en Linux, macOS y Windows).
# ---------------------------------------------------------------------------
FROM base AS vnc
RUN apt-get update \
    && apt-get install -y --no-install-recommends xvfb x11vnc \
    && rm -rf /var/lib/apt/lists/*
COPY src/main/docker/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod 755 /usr/local/bin/docker-entrypoint.sh

USER appuser
ENV DISPLAY=:99 \
    SCREEN_GEOMETRY=500x350x24 \
    VNC_PORT=5900
# Sin EXPOSE: VNC (5900) es un servicio de administración (Sonar docker:S6473).
# El puerto se publica de forma explícita y solo en local con -p 127.0.0.1:5900:5900.
ENTRYPOINT ["/usr/local/bin/docker-entrypoint.sh"]
# Argumento opcional: ThreeInARow (por defecto) o TicTacToe
CMD []