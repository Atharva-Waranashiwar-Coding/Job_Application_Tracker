FROM nginx:1.25-alpine

# Use a custom nginx configuration so the SPA can proxy API requests to the backend
COPY nginx.conf /etc/nginx/nginx.conf

COPY src /usr/share/nginx/html

EXPOSE 80
