#!/usr/bin/env bash
set -eu -o pipefail
cd "$(dirname "$(realpath "${0}")")"

DATADIR=${DATADIR:-/usr/share}
LIBDIR=${LIBDIR:-/usr/lib}
LIBEXECDIR=${LIBEXECDIR:-/usr/lib/}
SYSCONFDIR=${SYSCONFDIR:-/etc}

install -Dm 0755 gnome-spuren-search-provider.py "${LIBEXECDIR}"/gnome-spuren-search-provider/gnome-spuren-search-provider.py

# Search provider definition
install -Dm 0644 conf/org.gnome.Spuren.SearchProvider.ini "${DATADIR}"/gnome-shell/search-providers/org.gnome.Spuren.SearchProvider.ini

# Desktop file
install -Dm 0644 conf/Spuren.desktop "${DATADIR}"/applications/org.gnome.Spuren.SearchProvider.desktop

# DBus configuration
install -Dm 0644 conf/org.gnome.Spuren.SearchProvider.service.dbus "${DATADIR}"/dbus-1/services/org.gnome.Spuren.SearchProvider.service
install -Dm 0644 conf/org.gnome.Spuren.SearchProvider.service.systemd "${LIBDIR}"/systemd/user/org.gnome.Spuren.SearchProvider.service

echo "Installation complete! Restart GNOME shell now, with Alt+F2, r, return"
