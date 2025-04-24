package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import utils.DatabaseConnection;

public class Server {

    private ServerSocket server;
    private final int listenPort;
    private boolean running = true;
    private List<ClientHandlerV2> clients = new ArrayList<>();
    private final List<ClientHandlerV2> matchmakingQueue = new ArrayList<>();
    private List<Room> rooms = new ArrayList<>();
    private Connection connection;

    public synchronized void addToMatchmakingQueue(ClientHandlerV2 client) {
        matchmakingQueue.add(client);
        if (matchmakingQueue.size() >= 2) {
            // Match two players
            ClientHandlerV2 player1 = matchmakingQueue.remove(0);
            ClientHandlerV2 player2 = matchmakingQueue.remove(0);

            int roomId = generateRoomId();
            Room room = new Room("AutoMatch Room " + roomId, true, roomId);
            room.addClient(player1);
            room.addClient(player2);
            rooms.add(room);
            player1.setRoom(room);
            player2.setRoom(room);
            player1.sendMessage("MATCHED:" + roomId);
            player2.sendMessage("MATCHED:" + roomId);

            System.out.println("Matched players into Room ID: " + roomId);
        }
    }

    public synchronized void removeFromMatchmakingQueue(ClientHandlerV2 client) {
        matchmakingQueue.remove(client);
    }

    public Server(final int listen_port) {
        listenPort = listen_port;
    }

    public Room getRoom(int roomId) {
        for (Room room : rooms) {
            if (room.getId()==roomId) {
                return room;
            }
        }
        return null;
    }

    public void joinRoom(ClientHandlerV2 client, int roomId) {
        Room room = getRoom(roomId);
        if (room != null) {
            room.addClient(client);
        }
    }

    public void createRoom(ClientHandlerV2 client, String roomName, boolean isPrivate, int roomId) {
        Room room = new Room(roomName,isPrivate,roomId);
        room.addClient(client);
        rooms.add(room);
    }

    public void deleteRoom(int roomId) {
        Room room = getRoom(roomId);
        if (room != null) {
            if (room.isEmpty()) {
                rooms.remove(room);
            }
        }
    }

    public String getRoomList() {
        StringBuilder roomList = new StringBuilder();
        for (Room room : rooms) {
            if(!room.isPrivate()) {
                roomList.append(room.getName()).append(":").append(room.getId()).append(":");
            }
        }
        return roomList.toString();
    }

    public void init() {
        try {
            server = new ServerSocket(listenPort);
            System.out.println("Server started on port " + listenPort);

            connection= DatabaseConnection.getConnection();
            if (connection == null) {
                System.out.println("Database connection failed");
                return;
            }
            System.out.println("Database connection successful");

            while (running) {
                try {
                    Socket clientSocket = waitForConnection();
                    System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                    ClientHandlerV2 handler = new ClientHandlerV2(this, clientSocket);
                    clients.add(handler);
                    handler.start();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private Socket waitForConnection() throws IOException {
        return server.accept();
    }

    public void removeClient(ClientHandlerV2 client) {
        clients.remove(client);
    }

    public void broadcast(String message, ClientHandlerV2 sender) {
        for (ClientHandlerV2 client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void closeConnection() {
        try {
            running = false;
            if (server != null && !server.isClosed()) {
                server.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.init();
    }

    public int generateRoomId() {
        int roomId = 0;
        for (Room room : rooms) {
            if (room.getId() > roomId) {
                roomId = room.getId();
            }
        }
        return roomId + 1;
    }
}