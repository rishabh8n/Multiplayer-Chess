package chess.board;

public class BoardState {
    // Bitboards for pieces
    final long whitePawns, whiteKnights, whiteBishops, whiteRooks, whiteQueens, whiteKing;
    final long blackPawns, blackKnights, blackBishops, blackRooks, blackQueens, blackKing;

    // Game state
    final boolean whiteToMove, whiteKingsideCastle, whiteQueensideCastle, blackKingsideCastle, blackQueensideCastle;
    final int enPassantSquare;

    public BoardState(long whitePawns, long whiteKnights, long whiteBishops, long whiteRooks, long whiteQueens, long whiteKing,
                      long blackPawns, long blackKnights, long blackBishops, long blackRooks, long blackQueens, long blackKing,
                      boolean whiteToMove, boolean whiteKingsideCastle, boolean whiteQueensideCastle, boolean blackKingsideCastle, boolean blackQueensideCastle,
                      int enPassantSquare) {
        this.whitePawns = whitePawns;
        this.whiteKnights = whiteKnights;
        this.whiteBishops = whiteBishops;
        this.whiteRooks = whiteRooks;
        this.whiteQueens = whiteQueens;
        this.whiteKing = whiteKing;
        this.blackPawns = blackPawns;
        this.blackKnights = blackKnights;
        this.blackBishops = blackBishops;
        this.blackRooks = blackRooks;
        this.blackQueens = blackQueens;
        this.blackKing = blackKing;
        this.whiteToMove = whiteToMove;
        this.whiteKingsideCastle = whiteKingsideCastle;
        this.whiteQueensideCastle = whiteQueensideCastle;
        this.blackKingsideCastle = blackKingsideCastle;
        this.blackQueensideCastle = blackQueensideCastle;
        this.enPassantSquare = enPassantSquare;
    }
}
