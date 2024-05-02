#!/bin/bash

# Start Docker containers
docker compose up -d

# Create secret directory if it doesn't exist
mkdir -p secret

# Navigate to secret directory
cd secret

# Check if access_token_secret.txt file exists
if [ ! -f access_token_secret.txt ]; then
    # Generate new access token secret and write to access_token_secret.txt
    openssl rand -base64 64 > access_token_secret.txt
    echo "New access token secret generated."
else
    echo "Access token secret file already exists. Skipping generation."
fi

# Navigate back to project root
cd ..

# Start the Nest.js application in development mode
npm run start:dev
