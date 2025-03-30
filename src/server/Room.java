package server;

import chess.board.Board;
import chess.move.Move;

public class Room {
    private String name;
    private boolean isPrivate;
    private int id;
    private int currentPlayers;
    private ClientHandlerV2 whitePlayer;
    private ClientHandlerV2 blackPlayer;
    private Board board;
    private boolean isGameOver;
    private boolean whiteToMove;

    public Room(String name, boolean isPrivate, int id) {
        this.name = name;
        this.isPrivate = isPrivate;
        this.id = id;
        this.currentPlayers = 0;
        this.board = new Board();
        this.whiteToMove = true; // White starts in chess
        this.isGameOver = false;
    }

    public int getId() {
        return id;

    }

    public void addClient(ClientHandlerV2 client) {
        if (currentPlayers == 0) {
            whitePlayer = client;
            currentPlayers++;
            whitePlayer.sendMessage("RESPONSE: You joined as White");
            whitePlayer.sendMessage("ROOMID: " + id);
            whitePlayer.sendMessage("BOARD: " + board.toString());
        } else if (currentPlayers == 1) {
            if(blackPlayer != null) {
                whitePlayer = client;
                currentPlayers++;
                whitePlayer.sendMessage("RESPONSE: You joined as White");
                whitePlayer.sendMessage("ROOMID: " + id);
                whitePlayer.sendMessage("BOARD: " + board.toString());
                blackPlayer.sendMessage("RESPONSE: Both player has joined. Game starting!");
            }else{
                blackPlayer = client;
                currentPlayers++;
                blackPlayer.sendMessage("RESPONSE: You joined as Black");
                blackPlayer.sendMessage("ROOMID: " + id);
                blackPlayer.sendMessage("BOARD: " + board.toString());
                whitePlayer.sendMessage("RESPONSE: Both player has joined. Game starting!");
            }

        } else {
            client.sendMessage("RESPONSE: Room is full");
        }
    }

    public boolean playMove(ClientHandlerV2 client, String move) {
        if(currentPlayers < 2) {
            client.sendMessage("RESPONSE: Waiting for another player to join");
            return false;
        }
        // Check if it's the player's turn
        if ((whiteToMove && client != whitePlayer) || (!whiteToMove && client != blackPlayer)) {
            client.sendMessage("RESPONSE: Not your turn");
            return false;
        }

        if (this.isGameOver){
            client.sendMessage("RESPONSE: Game is over. Please start a new game.");
            return false;
        }

        try {
            // Parse the move
            String from = move.substring(0, 2);
            String to = move.substring(2);

            // Convert chess notation to board indices
            int fromCol = from.charAt(0) - 'a';
            int fromRow = from.charAt(1) - '1';
            int toCol = to.charAt(0) - 'a';
            int toRow = to.charAt(1) - '1';

            int fromIndex = fromRow * 8 + fromCol;
            int toIndex = toRow * 8 + toCol;
            int moveType = Board.determineMoveType(board, fromIndex, toIndex, 3);
            Move chessMove = new Move(fromIndex, toIndex,moveType);
            if (board.generateLegalMoves().contains(chessMove)) {
                board.makeMove(chessMove);
            }else {
                client.sendMessage("RESPONSE: Illegal move");
                return false;
            }

            // Update turn
            whiteToMove = !whiteToMove;

            // Notify both players
            String boardState = getBoardState();
            whitePlayer.sendMessage("BOARD: " + boardState);
            blackPlayer.sendMessage("BOARD: " + boardState);

            // Notify whose turn it is now
            String turnMessage = whiteToMove ? "White's turn" : "Black's turn";
            whitePlayer.sendMessage("RESPONSE: " + turnMessage);
            blackPlayer.sendMessage("RESPONSE: " + turnMessage);

            // Check for checkmate or draw
//            if (board.generateLegalMoves()) {
//                String winner = !whiteToMove ? "White" : "Black";
//                whitePlayer.sendMessage("RESPONSE: Checkmate! " + winner + " wins!");
//                blackPlayer.sendMessage("RESPONSE: Checkmate! " + winner + " wins!");
//            } else if (board.isStalemate()) {
//                whitePlayer.sendMessage("RESPONSE: Stalemate! Game is a draw.");
//                blackPlayer.sendMessage("RESPONSE: Stalemate! Game is a draw.");
//            }

            // Check for game over conditions
            if (board.isCheckmate()) {
                String winner = !whiteToMove ? "White" : "Black";
                whitePlayer.sendMessage("RESPONSE: Checkmate! " + winner + " wins!");
                blackPlayer.sendMessage("RESPONSE: Checkmate! " + winner + " wins!");
                isGameOver = true;
            } else if (board.isStalemate()) {
                whitePlayer.sendMessage("RESPONSE: Stalemate! Game is a draw.");
                blackPlayer.sendMessage("RESPONSE: Stalemate! Game is a draw.");
                isGameOver = true;
            }

            return true;
        } catch (Exception e) {
            client.sendMessage("RESPONSE: Invalid move format");
            return false;
        }
    }

    public String getBoardState() {
        return board.toString();
    }

    public String getName() {
        return name;
    }

    public int getCurrentPlayers() {
        return currentPlayers;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void addPlayer() {
        currentPlayers++;
    }

    public void removePlayer() {
        currentPlayers--;
    }

    public void removeClient(ClientHandlerV2 client) {
        if (whitePlayer == client) {
            whitePlayer = null;
            if (blackPlayer != null) {
                blackPlayer.sendMessage("RESPONSE: White player has left the game.");
            }
        } else {
            blackPlayer = null;
            if (whitePlayer != null) {
                whitePlayer.sendMessage("RESPONSE: Black player has left the game.");
            }
        }
        currentPlayers--;
    }

    public boolean isEmpty() {
        return currentPlayers == 0;
    }
}