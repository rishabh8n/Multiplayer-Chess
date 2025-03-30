# Multiplayer Chess

This is a multiplayer chess application that allows users to play chess against each other online. The game features a client-server architecture with a GUI chess board, user authentication, and real-time gameplay.

### Features

- Multiplayer chess over network
- User authentication and account management
- Real time chess board updates

### Project Setup

Prerequisites

- JAVA SDK 17 or higher
- MySQL Database
- IDE (IntelliJ IDEA recommended)

#### Step 1: Clone the repository

Clone the repository to your local machine:

```bash
git clone https://github.com/rishabh8n/Multiplayer-Chess.git
cd chess-game
```

#### Step 2: Set up MySQL Database

```
CREATE DATABASE IF NOT EXISTS chess_db;
USE chess_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(64) NOT NULL,
    games_played INT DEFAULT 0,
    games_won INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### Step 3: Add MySQL Connector to Project

1. Download MySQL Connector/J from MySQL Downloads
2. Extract the downloaded archive
3. Add the JAR file to your project:
   - In IntelliJ IDEA: File > Project Structure > Libraries > + > Java > Select the JAR file

#### Step 4: Configuring Database Connection

1. Go to src/utils/DatabaseConnection.java
2. Update the values of URL, USER and PASSWORD variables

```Java
private static String URL = "jdbc:mysql://localhost:3306/chess_db";
private static String USER = "your username";
private static String PASSWORD = "your password";
```

#### Step 5: Compile and run the project

1. Compile the project

```bash
javac -cp ".:mysql-connector-java-8.0.33.jar" -d bin src/**/*.java
```

2. Run the server

```bash
java -cp "bin:mysql-connector-java-8.0.33.jar" server.ChessServer
```

3. Run the client

```bash
java -cp "bin:mysql-connector-java-8.0.33.jar" client.ChessClientV2
```
