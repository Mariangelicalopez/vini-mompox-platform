    # --- ETAPA DE CONSTRUCCIÓN (BUILD STAGE) ---
    FROM node:20-alpine AS build
    WORKDIR /app
    COPY package*.json ./
    RUN npm install
    COPY . .
    RUN npm run build

    # --- PASOS DE DEPURACIÓN TEMPORALES (CAMBIO MÍNIMO PARA INVALIDAR CACHÉ) ---
    RUN echo "Contenido de /app después de npm run build:" && ls -l /app # Añade un espacio al final, o un comentario
    RUN echo "Contenido de /app/build: " && ls -l /app/build # <-- Añadí un espacio después del ':'
    # --- FIN PASOS DE DEPURACIÓN TEMPORALES ---


    # --- ETAPA DE SERVICIO (SERVE STAGE) ---
    FROM nginx:alpine

    WORKDIR /usr/share/nginx/html

    COPY --from=build /app/build .
    COPY nginx.conf /etc/nginx/conf.d/default.conf

    EXPOSE 80
    CMD ["nginx", "-g", "daemon off;"]