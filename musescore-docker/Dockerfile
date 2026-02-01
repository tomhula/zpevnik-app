FROM ubuntu:22.04

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8


WORKDIR /app


RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends \
        ca-certificates \
        tini \
        xvfb \
        libasound2 \
        libdbus-1-3 \
        libegl1 \
        libfontconfig1 \
        libgl1 \
        libglib2.0-0 \
        libglx0 \
        libice6 \
        libjack-jackd2-0 \
        libnss3 \
        libopengl0 \
        libpipewire-0.3-0 \
        libsm6 \
        libx11-6 \
        libxcb1 \
        libxcomposite1 \
        libxext6 \
        libxrender1 \
        wget \
    ; \
    wget -q -O /tmp/musescore.AppImage "https://cdn.jsdelivr.net/musescore/v4.6.5/MuseScore-Studio-4.6.5.253511702-x86_64.AppImage"; \
    chmod +x /tmp/musescore.AppImage; \
    /tmp/musescore.AppImage --appimage-extract; \
    rm -f /tmp/musescore.AppImage; \
    # Remove desktop integration files (not needed for headless) \
    rm -rf squashfs-root/usr/share/icons && \
    rm -rf squashfs-root/usr/share/applications && \
    rm -rf squashfs-root/usr/share/mime && \
    # Remove the updater and installer stubs \
    rm -f squashfs-root/usr/bin/mscore-installer && \
    mv /app/squashfs-root /musescore; \
    apt-get purge -y --auto-remove wget; \
    apt-get clean; \
    rm -rf /var/lib/apt/lists/*

RUN useradd -m musescore
USER musescore


ENTRYPOINT ["tini", "--", "xvfb-run", "-a", "--", "/musescore/AppRun"]
