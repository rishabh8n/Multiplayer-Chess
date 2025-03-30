package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChessBoardPanel extends JPanel {
    private static final int BOARD_SIZE = 8;
    private JButton[][] squares = new JButton[BOARD_SIZE][BOARD_SIZE];
    private Color lightColor = new Color(240, 217, 181);
    private Color darkColor = new Color(181, 136, 99);
    private Color highlightColor = new Color(124, 192, 203);

    private int fromRow = -1;
    private int fromCol = -1;
    private Map<Character, ImageIcon> pieceIcons = new HashMap<>();

    private SquareClickListener clickListener;

    public interface SquareClickListener {
        void onMove(int fromRow, int fromCol, int toRow, int toCol);
    }

    public ChessBoardPanel() {
        setLayout(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        loadPieceIcons();
        initializeBoard();
    }

    private void loadPieceIcons() {
        try {
            String[] pieces = {"K", "Q", "R", "B", "N", "P", "k", "q", "r", "b", "n", "p"};
            String[] pieceNames = {"WK", "WQ", "WR", "WB", "WN", "WP", "k", "q", "r", "b", "n", "p"};

            for (int i = 0; i < pieces.length; i++) {
                // Use forward slash for resource paths
                String path = "/resources/pieces/" + pieceNames[i] + ".gif";
                URL resourceUrl = getClass().getResource(path);

                if (resourceUrl != null) {
                    pieceIcons.put(pieces[i].charAt(0), new ImageIcon(resourceUrl));
                    System.out.println("Loaded icon for " + pieces[i] + " from " + path);
                } else {
                    System.err.println("Could not find resource: " + path);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load piece icons: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void initializeBoard() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JButton square = new JButton();
                square.setPreferredSize(new Dimension(60, 60));
                square.setBackground((row + col) % 2 == 0 ? lightColor : darkColor);
                square.setBorderPainted(false);
                square.setFocusPainted(false);

                final int r = row;
                final int c = col;
                square.addActionListener(e -> handleSquareClick(r, c));

                squares[row][col] = square;
                add(square);
            }
        }
    }

    public void addSquareClickListener(SquareClickListener listener) {
        this.clickListener = listener;
    }

    private void handleSquareClick(int row, int col) {
        if (fromRow == -1 && fromCol == -1) {
            // First click - select piece
            if (squares[row][col].getIcon() != null) {
                fromRow = row;
                fromCol = col;
                squares[row][col].setBackground(highlightColor);
            }
        } else {
            // Second click - move piece
            if (row == fromRow && col == fromCol) {
                // Clicked on the same square - deselect
                resetHighlight();
            } else {
                // Attempt to make a move
                if (clickListener != null) {
                    clickListener.onMove(fromRow, fromCol, row, col);
                }
                resetHighlight();
            }
        }
    }

    private void resetHighlight() {
        if (fromRow != -1 && fromCol != -1) {
            squares[fromRow][fromCol].setBackground(
                    (fromRow + fromCol) % 2 == 0 ? lightColor : darkColor);
        }
        fromRow = -1;
        fromCol = -1;
    }

    public void updateBoard(String boardRepresentation) {
        try {
            System.out.println("Updating board with: \n" + boardRepresentation);
            String[] lines = boardRepresentation.split("\n");

            if (lines.length < 8) {
                System.err.println("Invalid board representation: not enough lines");
                return;
            }

            for (int row = 0; row < 8; row++) {
                String line = lines[row].trim().replaceAll(" ","");
                for (int col = 0; col < 8; col++) {
                    char piece = '.';

                    // Extract the piece character based on your board format
                    // Adjust this based on how your server sends the board
                    if (line.length() > col) {
                        piece = line.charAt(col);
                    }

                    // Set the piece or clear the square
                    if (piece == '.' || piece == ' ') {
                        squares[7-row][col].setIcon(null); // Flip row to display correctly
                    } else {
                        // Try to get the piece icon
                        Icon icon = pieceIcons.get(piece);
                        if (icon != null) {
                            squares[7-row][col].setIcon(icon);
                        } else {
                            // If no icon is found, display the piece character
                            squares[7-row][col].setIcon(null);
                            squares[7-row][col].setText(String.valueOf(piece));
                        }
                    }
                }
            }

            revalidate();
            repaint();
        } catch (Exception e) {
            System.err.println("Failed to update board: " + e.getMessage());
            e.printStackTrace();
        }
    }

}