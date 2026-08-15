# Estágio de Build
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Estágio do Nginx
FROM nginx:alpine
# Copia o conteúdo da pasta 'dist' gerada pelo build para a pasta padrão do Nginx
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

# A imagem usa o proxy reverso interno para REST e WebSocket.

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]