package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLOutput;

public class ChessClientV2 extends JFrame {

    private String host;
    private int port;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private boolean running = true;
    private int roomId = -1;
    private boolean isAuthenticated = true;
    private String username;

    //GUI components
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel lobbyPanel;
    private JPanel gamePanel;
    private ChessBoardPanel boardPanel;
    private JTextArea messagesArea;
    private JTextField commandField;
    private JButton sendButton;


    public ChessClientV2(String host, int port) {
        this.host = host;
        this.port = port;

        connect();
        setTitle("Chess Client");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initializeUIComponents();
        switchToPanel("login");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    running = false;
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                    if (in != null) in.close();
                    if (out != null) out.close();

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });
    }

    private void initializeUIComponents() {
        mainPanel = new JPanel(new CardLayout());
        loginPanel = createLoginPanel();
//        lobbyPanel = createLobbyPanel();
        gamePanel = createGamePanel();

        mainPanel.add(loginPanel, "login");
//        mainPanel.add(lobbyPanel, "lobby");
        mainPanel.add(gamePanel, "game");

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createLoginPanel(){
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (!username.isEmpty() && !password.isEmpty()) {
                sendCommand("LOGIN:" + username + ":" + password);
            } else {
                showMessage("Please enter both username and password");
            }
        });

        registerButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            if (!username.isEmpty() && !password.isEmpty()) {
                sendCommand("REGISTER:" + username + ":" + password);
            } else {
                showMessage("Please enter both username and password");
            }
        });

        return panel;
    }

    private JPanel createLobbyPanel() {
        JPanel panel = new JPanel(new BorderLayout(50,50));
        JLabel label = new JLabel("Lobby");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel roomControlsPanel = new JPanel(new FlowLayout());

        JPanel createPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField createRoomField = new JTextField(10);
        JCheckBox privateRoomCheckBox = new JCheckBox("Private Room");
        JButton createButton = new JButton("Create Room");
        createPanel.add(new JLabel("Room Name:"));
        createPanel.add(createRoomField);
        createPanel.add(privateRoomCheckBox);
        createPanel.add(createButton);

        JPanel joinPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextField joinRoomField = new JTextField(10);
        JButton joinButton = new JButton("Join Room");
        joinPanel.add(new JLabel("Room ID:"));
        joinPanel.add(joinRoomField);
        joinPanel.add(joinButton);

        roomControlsPanel.add(createPanel);
        roomControlsPanel.add(joinPanel);

        if(isAuthenticated) {
            label.setText("Welcome " + username + " to the Lobby");
            panel.add(label, BorderLayout.NORTH);
            panel.add(roomControlsPanel, BorderLayout.CENTER);
            createButton.addActionListener(e -> {
                String roomName = createRoomField.getText();
                boolean privateRoom = privateRoomCheckBox.isSelected();
                if (!roomName.isEmpty()) {
                    sendCommand("CREATE:" + roomName + ":" + (privateRoom ? "1" : "0"));
                } else {
                    showMessage("Please enter a room name");
                }
            });
            joinButton.addActionListener(e -> {
                String roomIdStr = joinRoomField.getText();
                if (!roomIdStr.isEmpty()) {
                    try {
                        int roomId = Integer.parseInt(roomIdStr);
                        sendCommand("JOIN:" + roomId);
                    } catch (NumberFormatException ex) {
                        showMessage("Invalid Room ID");
                    }
                } else {
                    showMessage("Please enter a room ID");
                }
            });
            return panel;
        } else {
            label.setText("Please login to access the lobby");
            JButton backButton = new JButton("Back to Login");
            backButton.addActionListener(e -> switchToPanel("login"));
            panel.add(backButton, BorderLayout.SOUTH);
        }
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        boardPanel = new ChessBoardPanel();
        boardPanel.addSquareClickListener((fromRow, fromCol, toRow, toCol) -> {
            String from = "" + (char)('a' + fromCol) + (fromRow + 1);
            String to = "" + (char)('a' + toCol) + (toRow + 1);
            sendCommand("MOVE:" + from + to);
        });

        messagesArea = new JTextArea();
        messagesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(messagesArea);
        scrollPane.setPreferredSize(new Dimension(200, 0));

        JPanel controlPanel = new JPanel(new BorderLayout());
        commandField = new JTextField();
        sendButton = new JButton("Send");

        controlPanel.add(commandField, BorderLayout.CENTER);
        controlPanel.add(sendButton, BorderLayout.EAST);

        sendButton.addActionListener(e -> {
            String command = commandField.getText();
            sendCommand(command);
        });
        commandField.addActionListener(e -> {
            String command = commandField.getText();
            if(command.equals("EXIT")){
                sendCommand("EXIT:");
                running = false;
                System.exit(0);
            }
            sendCommand(command);
        });

        JButton leaveButton = new JButton("Leave Game");
        leaveButton.addActionListener(e -> {
            sendCommand("LEAVE:");
        });

        controlPanel.add(leaveButton, BorderLayout.SOUTH);

        panel.add(boardPanel, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.EAST);
        panel.add(controlPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void switchToPanel(String panelName) {
        CardLayout cl = (CardLayout) (mainPanel.getLayout());
        cl.show(mainPanel, panelName);
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void sendCommand(String command) {
        if (out != null) {
            out.println(command);
        }
    }

    private void processIncomingData() {
        try {
            String response;
            while(running && (response = in.readLine()) != null) {
                System.out.println("Server: " + response);
                if(response.startsWith("BOARD:")){
                    // Collect the entire board representation first
                    StringBuilder boardStr = new StringBuilder(response.substring(7));
                    for (int i = 0; i < 8; i++) {  // Read exactly 8 rows for the chess board
                        String line = in.readLine();
                        if (line == null) break;
                        System.out.println("Board line: " + line);
                        boardStr.append("\n").append(line);
                    }

                    final String boardRepresentation = boardStr.toString();
                    SwingUtilities.invokeLater(() -> {
                        try {
                            boardPanel.updateBoard(boardRepresentation);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }else {
                    String finalResponse = response;
                    SwingUtilities.invokeLater(()->{
//                        final String response = response;
                        if(finalResponse.startsWith("RESPONSE:LOGIN")){
                            String[] parts = finalResponse.split(":");
                            if(parts[2].equals("FAILED")){
                                showMessage("Login Failed");
                            } else if(parts[2].equals("SUCCESS")) {
                                username = parts[3];
                                isAuthenticated = true;
                                lobbyPanel = createLobbyPanel();
                                mainPanel.add(lobbyPanel, "lobby");
                                switchToPanel("lobby");
                            }
                        } else if(finalResponse.startsWith("RESPONSE:REGISTER")){
                            String[] parts = finalResponse.split(":");
                            if(parts[2].equals("FAILED")){
                                showMessage("Register Failed");
                            }else if(parts[2].equals("SUCCESS")) {
                                showMessage("Register Success");
                            }
                        } else if(finalResponse.startsWith("RESPONSE:JOIN")){
                            String[] parts = finalResponse.split(":");
                            if(parts[2].equals("FAILED")){
                                showMessage("Failed to join the room.");
                            }else if(parts[2].equals("SUCCESS")) {
                                roomId = Integer.parseInt(parts[4]);
                                showMessage("Joined Room: " + roomId+"\nRoom Name: "+parts[3]);
                                switchToPanel("game");
                            }
                        } else if(finalResponse.startsWith("RESPONSE:CREATE")){
                            String[] parts = finalResponse.split(":");
                            if(parts[2].equals("FAILED")){
                                showMessage("Failed to create the room.");
                            }else if(parts[2].equals("SUCCESS")) {
                                roomId = Integer.parseInt(parts[4]);
                                showMessage("Created Room: " + roomId+"\nRoom Name: "+parts[3]);
                                switchToPanel("game");
                            }
                        } else if(finalResponse.startsWith("RESPONSE:LEAVE")){
                            String[] parts = finalResponse.split(":");
                            if(parts[2].equals("SUCCESS")){
                                showMessage("Left the room.");
                                roomId = -1;
                                switchToPanel("lobby");
                            }else {
                                showMessage("Failed to leave the room.");
                            }
                        } else if(finalResponse.startsWith("RESPONSE:EXIT")){
                            String[] parts = finalResponse.split(":");
                            if(parts[2].equals("SUCCESS")){
                                showMessage("Goodbye!");
                                running = false;
                                System.exit(0);
                            }
                        } else if (finalResponse.startsWith("RESPONSE:LEAVE")) {
                            String[] parts = finalResponse.split(":");
                            if (parts[2].equals("SUCCESS")) {
                                showMessage("Left the game.");
                                roomId = -1;
                                switchToPanel("lobby");
                            } else {
                                showMessage("Failed to leave the game.");
                            }
                        } else {
                            messagesArea.append(finalResponse + "\n");
                        }
                    });
                }
            }
        }catch (IOException e){
            if(running) {
                e.printStackTrace();
            }
        }
    }


    public void connect() {
        try {
            connectToServer();
            getStreams();
            new Thread(this::processIncomingData).start();
            setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
//            showMessage("Failed to connect to server: " + e.getMessage());
        }
    }

    private void connectToServer() throws IOException {
        socket = new Socket(host, port);
    }

    private void getStreams() throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessClientV2 client = new ChessClientV2("localhost", 8080);
//            client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            client.setVisible(true);
        });
    }
}
