FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8


WORKDIR /tmp/work

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
    # Download and extract to /opt/musescore \
    wget -q -O /tmp/musescore.AppImage "https://cdn.jsdelivr.net/musescore/v4.6.5/MuseScore-Studio-4.6.5.253511702-x86_64.AppImage"; \
    chmod +x /tmp/musescore.AppImage; \
    /tmp/musescore.AppImage --appimage-extract; \
    mv squashfs-root /opt/musescore; \
    rm -f /tmp/musescore.AppImage; \
    # Create the wrapper script \
    echo '#!/bin/bash\nxvfb-run -a -- /opt/musescore/AppRun "$@"' > /usr/local/bin/musescore; \
    chmod +x /usr/local/bin/musescore; \
    # Cleanup \
    apt-get purge -y --auto-remove wget; \
    apt-get clean; \
    rm -rf /var/lib/apt/lists/*

RUN useradd -m musescore
USER musescore

ENTRYPOINT ["tini", "--", "musescore"]
