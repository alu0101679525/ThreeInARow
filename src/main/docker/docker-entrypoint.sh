#!/bin/sh
# Arranca un display virtual, un servidor VNC y la aplicación Swing.
set -e

Xvfb "${DISPLAY}" -screen 0 "${SCREEN_GEOMETRY}" -nolisten tcp &

# Espera a que el display esté listo
i=0
while [ ! -e "/tmp/.X11-unix/X${DISPLAY#:}" ] && [ "$i" -lt 50 ]; do
    i=$((i + 1))
    sleep 0.1
done

x11vnc -display "${DISPLAY}" -rfbport "${VNC_PORT}" -forever -shared -nopw -quiet &

exec java -jar /app/threeinarow.jar "$@"
