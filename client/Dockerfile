# Stage 1: Build the React application
FROM node:24-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy package files first
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy the remaining frontend source code
COPY . .

# Build the production application
RUN npm run build

# Stage 2: Serve the React application using Nginx
FROM nginx:alpine

# Copy the production build from the previous stage
COPY --from=build /app/dist /usr/share/nginx/html

# Copy our custom Nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf


# Instruction	                           Purpose
# FROM node:24-alpine	    Uses Node.js 24 on a lightweight Alpine Linux image.
# AS build	                Names this stage so we can reference it later.
# WORKDIR /app	            Sets the working directory inside the container.
# COPY package*.json ./	    Copies package files before the source code.
# RUN npm ci	            Installs the frontend dependencies.
# COPY . .	                Copies the rest of your React project.
# RUN npm run build	        Creates the production-ready dist folder.