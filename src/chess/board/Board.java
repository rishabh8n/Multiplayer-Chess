package chess.board;

import chess.move.Move;
import chess.move.MoveGenerator;

import java.util.List;
import java.util.Stack;

public class Board {
    // Bitboards for pieces
    private long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing;
    private long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing;

    // Game state
    private boolean whiteToMove, whiteKingsideCastle, whiteQueensideCastle, blackKingsideCastle, blackQueensideCastle;
    private int enPassantSquare;

    private final Stack<BoardState> boardStates = new Stack<>();

    private static final int WHITE_PAWN = 64, WHITE_KNIGHT = 65, WHITE_BISHOP = 66, WHITE_ROOK = 67, WHITE_QUEEN = 68, WHITE_KING = 69;
    private static final int BLACK_PAWN = 70, BLACK_KNIGHT = 71, BLACK_BISHOP = 72, BLACK_ROOK = 73, BLACK_QUEEN = 74, BLACK_KING = 75;
    private static final int EMPTY_SQUARE = 76;

    public Board() {
        whitePawns = BitboardConstants.WHITE_PAWNS_INITIAL;
        whiteKnights = BitboardConstants.WHITE_KNIGHTS_INITIAL;
        whiteBishops = BitboardConstants.WHITE_BISHOPS_INITIAL;
        whiteRooks = BitboardConstants.WHITE_ROOKS_INITIAL;
        whiteQueens = BitboardConstants.WHITE_QUEENS_INITIAL;
        whiteKing = BitboardConstants.WHITE_KINGS_INITIAL;

        blackPawns = BitboardConstants.BLACK_PAWNS_INITIAL;
        blackKnights = BitboardConstants.BLACK_KNIGHTS_INITIAL;
        blackBishops = BitboardConstants.BLACK_BISHOPS_INITIAL;
        blackRooks = BitboardConstants.BLACK_ROOKS_INITIAL;
        blackQueens = BitboardConstants.BLACK_QUEENS_INITIAL;
        blackKing = BitboardConstants.BLACK_KINGS_INITIAL;

        whiteToMove = true;
        whiteKingsideCastle = true;
        whiteQueensideCastle = true;
        blackKingsideCastle = true;
        blackQueensideCastle = true;
        enPassantSquare = -1;
    }

    public long getWhitePawns() {
        return whitePawns;
    }

    public long getWhiteKnights() {
        return whiteKnights;
    }

    public long getWhiteBishops() {
        return whiteBishops;
    }

    public long getWhiteRooks() {
        return whiteRooks;
    }

    public long getWhiteQueens() {
        return whiteQueens;
    }

    public long getWhiteKing() {
        return whiteKing;
    }

    public long getBlackPawns() {
        return blackPawns;
    }

    public long getBlackKnights() {
        return blackKnights;
    }

    public long getBlackBishops() {
        return blackBishops;
    }

    public long getBlackRooks() {
        return blackRooks;
    }

    public long getBlackQueens() {
        return blackQueens;
    }

    public long getBlackKing() {
        return blackKing;
    }

    public int getEnPassantSquare() {
        return enPassantSquare;
    }

    public boolean canWhiteSideCastleKingside() {
        return whiteKingsideCastle;
    }

    public boolean canWhiteSideCastleQueenside() {
        return whiteQueensideCastle;
    }

    public boolean canBlackSideCastleKingside() {
        return blackKingsideCastle;
    }

    public boolean canBlackSideCastleQueenside() {
        return blackQueensideCastle;
    }

    public boolean isWhiteToMove() {
        return whiteToMove;
    }

    public long getWhitePieces() {
        return whitePawns | whiteKnights | whiteBishops | whiteRooks | whiteQueens | whiteKing;
    }

    public long getBlackPieces() {
        return blackPawns | blackKnights | blackBishops | blackRooks | blackQueens | blackKing;
    }

    public long getOccupied() {
        return getWhitePieces() | getBlackPieces();
    }

    public long getEmpty() {
        return ~getOccupied();
    }

    public void printBoard() {
        for (int rank = 7; rank >= 0; rank--) {
            System.out.print((rank + 1) + " ");
            for (int file = 0; file < 8; file++) {
                int square = rank * 8 + file;
                long squareBit = 1L << square;
                char piece = getPieceChar(squareBit);
                System.out.print(piece + " ");
            }
            System.out.println();
        }
        System.out.println("  a b c d e f g h");
        System.out.println("Side to move: " + (whiteToMove ? "White" : "Black"));
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int rank = 7; rank >= 0; rank--) {
//            sb.append((rank + 1)).append(" ");
            for (int file = 0; file < 8; file++) {
                int square = rank * 8 + file;
                long squareBit = 1L << square;
                char piece = getPieceChar(squareBit);
                sb.append(piece).append(" ");
            }
            sb.append("\n");
        }
//        sb.append("  a b c d e f g h\n");
        return sb.toString();
    }

    public char getPieceChar(long squareBit) {
        if ((whitePawns & squareBit) != 0) return 'P';
        if ((whiteKnights & squareBit) != 0) return 'N';
        if ((whiteBishops & squareBit) != 0) return 'B';
        if ((whiteRooks & squareBit) != 0) return 'R';
        if ((whiteQueens & squareBit) != 0) return 'Q';
        if ((whiteKing & squareBit) != 0) return 'K';

        if ((blackPawns & squareBit) != 0) return 'p';
        if ((blackKnights & squareBit) != 0) return 'n';
        if ((blackBishops & squareBit) != 0) return 'b';
        if ((blackRooks & squareBit) != 0) return 'r';
        if ((blackQueens & squareBit) != 0) return 'q';
        if ((blackKing & squareBit) != 0) return 'k';

        return '.';
    }

    private void clearSquare(int square) {
        long squareMask = ~(1L << square);

        whitePawns &= squareMask;
        whiteKnights &= squareMask;
        whiteBishops &= squareMask;
        whiteRooks &= squareMask;
        whiteQueens &= squareMask;
        whiteKing &= squareMask;
        blackPawns &= squareMask;
        blackKnights &= squareMask;
        blackBishops &= squareMask;
        blackRooks &= squareMask;
        blackQueens &= squareMask;
        blackKing &= squareMask;
    }

    public int getPieceAt(int square) {
        long squareBB = 1L << square;

        if ((whitePawns & squareBB) != 0) return WHITE_PAWN;
        if ((whiteKnights & squareBB) != 0) return WHITE_KNIGHT;
        if ((whiteBishops & squareBB) != 0) return WHITE_BISHOP;
        if ((whiteRooks & squareBB) != 0) return WHITE_ROOK;
        if ((whiteQueens & squareBB) != 0) return WHITE_QUEEN;
        if ((whiteKing & squareBB) != 0) return WHITE_KING;
        if ((blackPawns & squareBB) != 0) return BLACK_PAWN;
        if ((blackKnights & squareBB) != 0) return BLACK_KNIGHT;
        if ((blackBishops & squareBB) != 0) return BLACK_BISHOP;
        if ((blackRooks & squareBB) != 0) return BLACK_ROOK;
        if ((blackQueens & squareBB) != 0) return BLACK_QUEEN;
        if ((blackKing & squareBB) != 0) return BLACK_KING;

        return EMPTY_SQUARE;
    }

    private void setPieceAt(int square, int pieceType) {
        long squareBB = 1L << square;

        switch (pieceType) {
            case WHITE_PAWN:
                whitePawns |= squareBB;
                break;
            case WHITE_KNIGHT:
                whiteKnights |= squareBB;
                break;
            case WHITE_BISHOP:
                whiteBishops |= squareBB;
                break;
            case WHITE_ROOK:
                whiteRooks |= squareBB;
                break;
            case WHITE_QUEEN:
                whiteQueens |= squareBB;
                break;
            case WHITE_KING:
                whiteKing |= squareBB;
                break;
            case BLACK_PAWN:
                blackPawns |= squareBB;
                break;
            case BLACK_KNIGHT:
                blackKnights |= squareBB;
                break;
            case BLACK_BISHOP:
                blackBishops |= squareBB;
                break;
            case BLACK_ROOK:
                blackRooks |= squareBB;
                break;
            case BLACK_QUEEN:
                blackQueens |= squareBB;
                break;
            case BLACK_KING:
                blackKing |= squareBB;
                break;
        }
    }

    private int determinePromotionPiece(int pawnPiece, int promotionType) {
        boolean isWhitePawn = (pawnPiece == WHITE_PAWN);

        return switch (promotionType) {
            case Move.KNIGHT_PROMOTION -> isWhitePawn ? WHITE_KNIGHT : BLACK_KNIGHT;
            case Move.BISHOP_PROMOTION -> isWhitePawn ? WHITE_BISHOP : BLACK_BISHOP;
            case Move.ROOK_PROMOTION -> isWhitePawn ? WHITE_ROOK : BLACK_ROOK;
            default -> isWhitePawn ? WHITE_QUEEN : BLACK_QUEEN;
        };
    }

    private void updateCastlingRights(int movingPiece, int fromSquare, int toSquare, int capturedPiece) {
        // When the king moves, lose all castling rights for that color
        if (movingPiece == WHITE_KING) {
            whiteKingsideCastle = false;
            whiteQueensideCastle = false;
        } else if (movingPiece == BLACK_KING) {
            blackKingsideCastle = false;
            blackQueensideCastle = false;
        }

        // When any rook moves from either color, lose the castling rights from that side
        else if (movingPiece == WHITE_ROOK) {
            if (fromSquare == 7) {
                whiteKingsideCastle = false;
            } else if (fromSquare == 0) {
                whiteQueensideCastle = false;
            }
        } else if (movingPiece == BLACK_ROOK) {
            if (fromSquare == 63) {
                blackKingsideCastle = false;
            } else if (fromSquare == 56) {
                blackQueensideCastle = false;
            }
        }

        if (capturedPiece == WHITE_ROOK) {
            if (toSquare == 7) {
                whiteKingsideCastle = false;
            } else if (toSquare == 0) {
                whiteQueensideCastle = false;
            }
        } else if (capturedPiece == BLACK_ROOK) {
            if (toSquare == 63) {
                blackKingsideCastle = false;
            } else if (toSquare == 56) {
                blackQueensideCastle = false;
            }
        }
    }

    private BoardState saveBoardState() {
        return new BoardState(
                whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing,
                blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing,
                whiteToMove, whiteKingsideCastle, whiteQueensideCastle, blackKingsideCastle, blackQueensideCastle,
                enPassantSquare
        );
    }

    public void makeMove(Move move) {
        // Store the board state before making a move so that we can undo this later
        BoardState previousBoardState = saveBoardState();
        boardStates.push(previousBoardState);

        // Get the from squares and to squares and convert them to bitboard along with the moveType
        // Even though this could have happened in 2 lines, this would help in understanding
        int fromSquare = move.getFrom();
        int toSquare = move.getTo();
        long fromBB = 1L << fromSquare;
        long toBB = 1L << toSquare;
        int moveType = move.getMoveType();

        // Get piece on the from square
        int movingPiece = getPieceAt(fromSquare);
        int capturedPiece = EMPTY_SQUARE; // Default value when no capture

        // Clear the from square (for all piece bitboards)
        clearSquare(fromSquare);

        // Handle captures - remove the captured piece from its bitboards
        if (moveType == Move.CAPTURE || moveType == Move.PROMOTION_CAPTURE) {
            capturedPiece = getPieceAt(toSquare);
            clearSquare(toSquare);
        }

        // Handle special en passant capture
        if (moveType == Move.EN_PASSANT_CAPTURE) {
            int epCaptureSquare = whiteToMove ?
                    (toSquare - 8) : (toSquare + 8);
            capturedPiece = getPieceAt(epCaptureSquare);
            clearSquare(epCaptureSquare);
        }

        // Place the piece on the destination square
        if (moveType == Move.PROMOTION || moveType == Move.PROMOTION_CAPTURE) {
            // Determine promotion piece type
            int promotionPiece = determinePromotionPiece(movingPiece, move.getPromotionPiece());
            setPieceAt(toSquare, promotionPiece);
        } else {
            // Standard move of the piece to the target square
            setPieceAt(toSquare, movingPiece);
        }

        // Handle castling - move the rook as well
        if (moveType == Move.KING_CASTLE) {
            if (whiteToMove) {
                // White kingside castling - move rook from h1 to f1
                clearSquare(7);  // h1
                setPieceAt(5, WHITE_ROOK);  // f1
            } else {
                // Black kingside castling - move rook from h8 to f8
                clearSquare(63);  // h8
                setPieceAt(61, BLACK_ROOK);  // f8
            }
        } else if (moveType == Move.QUEEN_CASTLE) {
            if (whiteToMove) {
                // White queenside castling - move rook from a1 to d1
                clearSquare(0);  // a1
                setPieceAt(3, WHITE_ROOK);  // d1
            } else {
                // Black queenside castling - move rook from a8 to d8
                clearSquare(56);  // a8
                setPieceAt(59, BLACK_ROOK);  // d8
            }
        }

        updateCastlingRights(movingPiece, fromSquare, toSquare, capturedPiece);

        // Update en passant square
        if (moveType == Move.DOUBLE_PAWN_PUSH) {
            enPassantSquare = whiteToMove ? (fromSquare + 8) : (fromSquare - 8);
        } else {
            enPassantSquare = -1;  // No en passant possible on the next move
        }

        whiteToMove = !whiteToMove;
    }

    public void unmakeMove() {
        if (boardStates.isEmpty()) {
            throw new IllegalStateException("No moves to undo!");
        }

        BoardState previousState = boardStates.pop();

        whitePawns = previousState.whitePawns;
        whiteKnights = previousState.whiteKnights;
        whiteBishops = previousState.whiteBishops;
        whiteRooks = previousState.whiteRooks;
        whiteQueens = previousState.whiteQueens;
        whiteKing = previousState.whiteKing;
        blackPawns = previousState.blackPawns;
        blackKnights = previousState.blackKnights;
        blackBishops = previousState.blackBishops;
        blackRooks = previousState.blackRooks;
        blackQueens = previousState.blackQueens;
        blackKing = previousState.blackKing;

        whiteToMove = previousState.whiteToMove;
        whiteKingsideCastle = previousState.whiteKingsideCastle;
        whiteQueensideCastle = previousState.whiteQueensideCastle;
        blackKingsideCastle = previousState.blackKingsideCastle;
        blackQueensideCastle = previousState.blackQueensideCastle;
        enPassantSquare = previousState.enPassantSquare;
    }

    public void setPosition(String fen) {
        // Clear the current position
        whitePawns = whiteKnights = whiteBishops = whiteRooks = whiteQueens = whiteKing = 0L;
        blackPawns = blackKnights = blackBishops = blackRooks = blackQueens = blackKing = 0L;

        String[] parts = fen.split(" ");
        String piecePositions = parts[0];
        String activeColor = parts[1];
        String castlingRights = parts[2];
        String epSquare = parts[3];

        // Parse piece positions
        int rank = 7;  // Start at the 8th rank (index 7)
        int file = 0;  // Start at the A file (index 0)

        for (char c : piecePositions.toCharArray()) {
            if (c == '/') {
                rank--;
                file = 0;
            } else if (Character.isDigit(c)) {
                file += Character.getNumericValue(c);
            } else {
                int square = rank * 8 + file;
                long bitboard = 1L << square;

                switch (c) {
                    case 'P': whitePawns |= bitboard; break;
                    case 'N': whiteKnights |= bitboard; break;
                    case 'B': whiteBishops |= bitboard; break;
                    case 'R': whiteRooks |= bitboard; break;
                    case 'Q': whiteQueens |= bitboard; break;
                    case 'K': whiteKing |= bitboard; break;
                    case 'p': blackPawns |= bitboard; break;
                    case 'n': blackKnights |= bitboard; break;
                    case 'b': blackBishops |= bitboard; break;
                    case 'r': blackRooks |= bitboard; break;
                    case 'q': blackQueens |= bitboard; break;
                    case 'k': blackKing |= bitboard; break;
                }
                file++;
            }
        }

        // Set side to move
        whiteToMove = activeColor.equals("w");

        // Set castling rights
        whiteKingsideCastle = castlingRights.contains("K");
        whiteQueensideCastle = castlingRights.contains("Q");
        blackKingsideCastle = castlingRights.contains("k");
        blackQueensideCastle = castlingRights.contains("q");

        // Set en passant square
        if (epSquare.equals("-")) {
            enPassantSquare = -1;
        } else {
            int file_ep = epSquare.charAt(0) - 'a';
            int rank_ep = epSquare.charAt(1) - '1';
            enPassantSquare = rank_ep * 8 + file_ep;
        }

        // Clear the board state history
        boardStates.clear();
    }

    // Generate all legal moves for the current position
    public List<Move> generateLegalMoves() {
        MoveGenerator moveGenerator = new MoveGenerator();
        return moveGenerator.generateLegalMoves(this);
    }

    public boolean isCheckmate() {
        // Check if the current player is in check and has no legal moves
        return isInCheck() && generateLegalMoves().isEmpty();
    }

    private boolean isInCheck() {
        long kingPosition = whiteToMove ? whiteKing : blackKing;
        long opponentPieces = whiteToMove ? getBlackPieces() : getWhitePieces();
        return MoveGenerator.isAttacked(kingPosition, opponentPieces, this);
    }

    public boolean isStalemate() {
        // Check if the current player is not in check and has no legal moves
        return !isInCheck() && generateLegalMoves().isEmpty();
    }

    /**
     * Determines the type of chess move based on the board state and move details.
     *
     * @param board The current board state
     * @param from The source square (0-63)
     * @param to The destination square (0-63)
     * @param promotionPiece Optional promotion piece type (0-3, corresponds to Move constants)
     * @return The move type as defined in the Move class constants
     */
    public static int determineMoveType(Board board, int from, int to, int promotionPiece) {
        // Get bitboards for the source and destination squares
        long fromBB = 1L << from;
        long toBB = 1L << to;

        // Check if the move is a capture
        boolean isCapture = (board.getOccupied() & toBB) != 0;

        // Identify the moving piece
        char piece = board.getPieceChar(fromBB);
        boolean isPawn = Character.toLowerCase(piece) == 'p';
        boolean isKing = Character.toLowerCase(piece) == 'k';

        // Check for pawn special moves
        if (isPawn) {
            // Promotion check - pawn reaches the 8th or 1st rank
            boolean isPromotion = (to / 8 == 7) || (to / 8 == 0);
            if (isPromotion) {
                return isCapture ? Move.PROMOTION_CAPTURE : Move.PROMOTION;
            }

            // Double pawn push - moving two squares from starting position
            if (Math.abs(from - to) == 16) {
                return Move.DOUBLE_PAWN_PUSH;
            }

            // En passant capture - pawn captures diagonally but destination is empty
            if (isCapture) {
                return Move.CAPTURE;
            } else if ((Math.abs(from % 8 - to % 8) == 1) && (Math.abs(from / 8 - to / 8) == 1)) {
                return Move.EN_PASSANT_CAPTURE;
            }
        }

        // Check for castling
        if (isKing && Math.abs(from % 8 - to % 8) == 2) {
            return (to % 8 > from % 8) ? Move.KING_CASTLE : Move.QUEEN_CASTLE;
        }

        // Regular captures
        if (isCapture) {
            return Move.CAPTURE;
        }

        // Regular quiet move
        return Move.QUIET_MOVE;
    }
}
