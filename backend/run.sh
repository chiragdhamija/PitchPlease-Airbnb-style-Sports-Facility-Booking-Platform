#!/bin/bash

set -e

# Detect OS
OS=$(uname)

# Use sudo only if not running on macOS
DOCKER_CMD="docker"
if [ "$OS" != "Darwin" ]; then
    DOCKER_CMD="sudo docker"
fi

# Maven build
mvn clean package -DskipTests

# Docker commands
$DOCKER_CMD compose down -v --remove-orphans
# $DOCKER_CMD volume rm postgres-data
$DOCKER_CMD compose build
$DOCKER_CMD compose up
