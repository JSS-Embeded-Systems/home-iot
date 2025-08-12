FROM ubuntu:latest
LABEL authors="nils"

# Development
FROM node:20-alpine AS builder

# Arbeitsverzeichnis setzen
WORKDIR /app

# Nur package-Dateien zuerst kopieren, um Caching zu nutzen
COPY package*.json ./

# Dependencies installieren
RUN npm ci

# Restliche Dateien kopieren
COPY . .

# Production-Build erstellen
RUN npm run build

# Production
FROM nginx:1.27-alpine AS production

# Default Nginx-Konfiguration ersetzen (optional: Vue Router support)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Build-Output ins nginx html-Verzeichnis kopieren
COPY --from=builder /app/dist /usr/share/nginx/html

# Port exposen
EXPOSE 80

# Container starten
CMD ["nginx", "-g", "daemon off;"]