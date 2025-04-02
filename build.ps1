# build.ps1
# Create directories
if (!(Test-Path bin)) { mkdir bin }
if (!(Test-Path bin\resources)) { mkdir bin\resources -Force }

# Compile Java files
javac -cp ".;lib\mysql-connector-java-8.0.28.jar" -d bin src/chess/board/*.java src/chess/move/*.java src/client/*.java src/server/*.java src/utils/*.java src/controller/*.java src/model/*.java

# Copy resources to bin folder
Copy-Item -Path "src\resources\*" -Destination "bin\resources\" -Recurse -Force