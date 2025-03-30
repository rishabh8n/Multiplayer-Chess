import chess.board.Board;
import chess.move.Move;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board = new Board();
        System.out.println(board);
        boolean isGameOver = false;
        while (!isGameOver) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter your move (or 'exit' to quit): ");
            System.out.println(board.generateLegalMoves());
            String move = scanner.nextLine();
            if (move.equals("exit")) {
                System.out.println("Game exited.");
                break;
            }

            // Parse the move and update the board
            int from = move.charAt(0) - 'a' + (move.charAt(1) - '1') * 8;
            int to = move.charAt(2) - 'a' + (move.charAt(3) - '1') * 8;
            int moveType = Board.determineMoveType(board,from,to,1);
            Move parsedMove = new Move(from, to, moveType);
            System.out.println(board.getPieceChar(1L<<to));
            // Check if the move is valid
            if(board.generateLegalMoves().contains(parsedMove)) {
                System.out.println("Valid move.");
                // Make the move
                board.makeMove(parsedMove);
                System.out.println(board);
            } else {
                System.out.println("Invalid move. Try again.");
                continue;
            }

            // Check for game over conditions
            if (board.isCheckmate()) {
                System.out.println("Checkmate! Game over.");
                isGameOver = true;
            } else if (board.isStalemate()) {
                System.out.println("Stalemate! Game over.");
                isGameOver = true;
            }
        }
    }
}