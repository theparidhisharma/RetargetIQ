# STAGE 1: Build the frontend assets
# We use a Node.js image to compile the React/Angular/Vue code
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package.json first to cache dependencies
COPY package*.json ./
RUN npm install

# Copy the rest of the source code
COPY . .

# Build the project
# This usually creates a 'build' or 'dist' folder
RUN npm run build

# STAGE 2: Serve the app with Nginx
# Nginx is a high-performance web server, much better for production than 'npm start'
FROM nginx:alpine

# Remove default Nginx website
RUN rm -rf /usr/share/nginx/html/*

# Copy the built assets from the 'builder' stage
# CRITICAL: Check your local project! 
# - If React (Create React App): use /app/build
# - If Vite/Vue: use /app/dist
# - If Angular: use /app/dist/your-project-name
COPY --from=builder /app/build /usr/share/nginx/html

# Expose port 80 (standard web port)
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]