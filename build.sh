#!/bin/bash

#build all modules
mvn clean install
# Define the module directories
modules=("catalogue-service" "notification-service")

# Loop through modules and build Docker images
for module in "${modules[@]}"; do
  echo "Building Docker image for $module..."

  # Navigate to module directory
  cd $module || exit

  # Build the Docker image (tagged with module name)
  docker build -t efuture/$module:latest .

  # Go back to the monorepo root
  cd - > /dev/null || exit
done

echo "Docker images built successfully!"
