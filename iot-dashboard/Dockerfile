FROM ubuntu:latest
LABEL authors="nils"

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