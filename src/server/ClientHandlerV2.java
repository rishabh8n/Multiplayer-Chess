package server;

import controller.UserController;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

public class ClientHandlerV2 extends Thread {
    private final Server server;
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private HashMap<String, Integer> mapColumns;
    private HashMap<String, Integer> mapRows;
    private Room room;
    private User user;

    public ClientHandlerV2(Server server, Socket socket) {
        this.server = server;
        this.clientSocket = socket;
        initializeMaps();
    }

    private void initializeMaps() {
        mapColumns = new HashMap<>();
        mapColumns.put("a", 0);
        mapColumns.put("b", 1);
        mapColumns.put("c", 2);
        mapColumns.put("d", 3);
        mapColumns.put("e", 4);
        mapColumns.put("f", 5);
        mapColumns.put("g", 6);
        mapColumns.put("h", 7);

        mapRows = new HashMap<>();
        mapRows.put("1", 0);
        mapRows.put("2", 1);
        mapRows.put("3", 2);
        mapRows.put("4", 3);
        mapRows.put("5", 4);
        mapRows.put("6", 5);
        mapRows.put("7", 6);
        mapRows.put("8", 7);
    }

    private void setupStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void run(){
        try {
            setupStreams();
            sendWelcomeMessage();
            if(authenticateUser()) {
                processClientData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void sendWelcomeMessage() {
        out.println("RESPONSE:Welcome to the Chess Server!");
    }

    private boolean authenticateUser() throws IOException {
        out.println("RESPONSE:Authenticate user");
        String request;
        while ((request = in.readLine()) != null) {
            if(request.startsWith("REGISTER:")){
                String[] parts = request.split(":");
                if(parts.length !=3){
                    out.println("RESPONSE:REGISTER:Invalid command");
                    continue;
                }
                boolean success = UserController.registerUser(parts[1], parts[2]);
                if(success){
                    out.println("RESPONSE:REGISTER:SUCCESS:"+parts[1]);
                } else {
                    out.println("RESPONSE:REGISTER:FAILED");
                }
            }else if(request.startsWith("LOGIN:")){
                String[] parts = request.split(":");
                if(parts.length !=3){
                    out.println("RESPONSE:LOGIN:Invalid command");
                    continue;
                }
                user = UserController.authenticateUser(parts[1], parts[2]);
                if(user != null){
                    out.println("RESPONSE:LOGIN:SUCCESS:"+user.getUsername());
                    return true;
                } else {
                    out.println("RESPONSE:LOGIN:FAILED");
                }
            } else if(request.startsWith("EXIT:")){
                out.println("RESPONSE:EXIT:Goodbye!");
                closeConnection();
                return false;
            } else {
                out.println("RESPONSE:Invalid command");
            }
        }
        return false;
    }

    private void processClientData() throws IOException {
        String request;
        while ((request = in.readLine()) != null) {
            System.out.println("Received: " + request);
            if(request.startsWith("EXIT:")){
                out.println("RESPONSE:EXIT:Goodbye!");
                closeConnection();
                break;
            } else if(request.startsWith("CREATE:")){
                String[] parts = request.split(":");
                if(parts.length !=3){
                    out.println("RESPONSE:CREATE:Invalid command");
                    continue;
                }
                String roomName = parts[1];
                int isPrivate = Integer.parseInt(parts[2]);

                if(room!=null){
                    room.removeClient(this);
                }
                int roomId = server.generateRoomId();
                server.createRoom(this, roomName, isPrivate==0, roomId);
                room=server.getRoom(roomId);
                if(room==null){
                    out.println("RESPONSE:CREATE:FAILED");
                } else {
                    out.println("RESPONSE:CREATE:SUCCESS:" +roomName+":"+ roomId);
                }
            } else if (request.startsWith("JOIN:")) {
                String[] parts = request.split(":");
                if(parts.length !=2){
                    out.println("RESPONSE:JOIN:Invalid command");
                    continue;
                }
                int roomId= Integer.parseInt(parts[1]);
                Room targetRoom = server.getRoom(roomId);
                if(targetRoom==null){
                    out.println("RESPONSE:JOIN:FAILED:Room not found");
                }else {
                    if(room!=null){
                        room.removeClient(this);
                    }
                    server.joinRoom(this, roomId);
                    room=targetRoom;
                    out.println("RESPONSE:JOIN:SUCCESS:" + targetRoom.getName()+":"+ roomId);
                }
            }else if(request.startsWith("LEAVE:")){
                if(room!=null){
                    room.removeClient(this);
                    room=null;
                    out.println("RESPONSE:LEAVE:SUCCESS");
                } else {
                    out.println("RESPONSE:LEAVE:FAILED:You are not in any room");
                }
            } else if (request.startsWith("MOVE:")) {
                String[] parts = request.split(":");
                String move = parts[1];
                playMove(move);
            } else if(request.startsWith("USER:")){
                String[] parts = request.split(":");
                if(parts.length !=2){
                    out.println("RESPONSE:USER:Invalid command");
                    continue;
                }
                if(parts[1].equals("GET")){
                    if(user != null){
                        out.println("RESPONSE:USER:SUCCESS:"+user.getUsername());
                    } else {
                        out.println("RESPONSE:USER:FAILED:User not authenticated");
                    }
                } else {
                    out.println("RESPONSE:USER:Invalid command");
                }
            }
            else{
                out.println("RESPONSE:Invalid command");
            }
        }
    }

    private boolean playMove(String move) {
        if(room==null){
            out.println("RESPONSE:MOVE:FAILED:You are not in any room");
            return false;
        }
        String from = move.substring(0, 2);
        String to = move.substring(2, 4);
        if (!isValidPosition(from) || !isValidPosition(to)) {
            out.println("RESPONSE: Invalid position format");
            return false;
        }
        int fromIndex = mapRows.get(from.charAt(1) + "") * 8 + mapColumns.get(from.charAt(0) + "");
        int toIndex = mapRows.get(to.charAt(1) + "") * 8 + mapColumns.get(to.charAt(0) + "");

        return room.playMove(this, move);
    }

    private boolean isValidPosition(String position) {
        if (position.length() != 2) return false;

        String col = position.charAt(0) + "";
        String row = position.charAt(1) + "";

        return mapColumns.containsKey(col) && mapRows.containsKey(row);
    }

    private void closeConnection() {
        try {
            if (room != null) {
                room.removeClient(this);
            }

            server.removeClient(this);

            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }

            System.out.println("Connection with client closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
