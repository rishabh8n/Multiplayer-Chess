#!/bin/bash

# Create directories
mkdir -p bin/resources

# Compile Java files (note: using forward slashes and colons for Unix paths)
javac -cp ".:lib/mysql-connector-java-8.0.28.jar" -d bin src/chess/board/*.java src/chess/move/*.java src/client/*.java src/server/*.java src/utils/*.java src/controller/*.java src/model/*.java

# Copy resources to bin folder
cp -R src/resources/* bin/resources/
