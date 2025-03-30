package chess.move;

public class Move {
    // Move types
    public static final int QUIET_MOVE = 0;
    public static final int DOUBLE_PAWN_PUSH = 1;
    public static final int KING_CASTLE = 2;
    public static final int QUEEN_CASTLE = 3;
    public static final int CAPTURE = 4;
    public static final int EN_PASSANT_CAPTURE = 5;
    public static final int PROMOTION = 6;
    public static final int PROMOTION_CAPTURE = 7;

    // Promotion piece types
    public static final int KNIGHT_PROMOTION = 0;
    public static final int BISHOP_PROMOTION = 1;
    public static final int ROOK_PROMOTION = 2;
    public static final int QUEEN_PROMOTION = 3;

    // Object constants
    private final int from;
    private final int to;
    private final int moveType;
    private final int promotionPiece;

    public Move(int from, int to) {
        this(from, to, QUIET_MOVE, 0);
    }

    public Move(int from, int to, int moveType) {
        this(from, to, moveType, 0);
    }

    public Move(int from, int to, int moveType, int promotionPiece) {
        this.from = from;
        this.to = to;
        this.moveType = moveType;
        this.promotionPiece = promotionPiece;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getMoveType() {
        return moveType;
    }

    public int getPromotionPiece() {
        return promotionPiece;
    }

    public boolean isCapture() {
        return moveType == CAPTURE || moveType == EN_PASSANT_CAPTURE || moveType == PROMOTION_CAPTURE;
    }

    public boolean isPromotion() {
        return moveType == PROMOTION || moveType == PROMOTION_CAPTURE;
    }

    public boolean isCastling() {
        return moveType == KING_CASTLE || moveType == QUEEN_CASTLE;
    }

    @Override
    public String toString() {
        // Convert to algebraic notation
        String[] files = {"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] ranks = {"1", "2", "3", "4", "5", "6", "7", "8"};

        String fromSquare = files[from % 8] + ranks[from / 8];
        String toSquare = files[to % 8] + ranks[to / 8];

        StringBuilder sb = new StringBuilder();
        sb.append(fromSquare).append(toSquare);
        sb.append(" ").append(moveType);

        // Add promotion piece if applicable
        if (isPromotion()) {
            switch (promotionPiece) {
                case KNIGHT_PROMOTION: sb.append("n"); break;
                case BISHOP_PROMOTION: sb.append("b"); break;
                case ROOK_PROMOTION: sb.append("r"); break;
                case QUEEN_PROMOTION: sb.append("q"); break;
            }
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Move other = (Move) obj;
        return from == other.from &&
                to == other.to &&
                moveType == other.moveType &&
                promotionPiece == other.promotionPiece;
    }

    @Override
    public int hashCode() {
        return (from) | (to << 6) | (moveType << 12) | (promotionPiece << 15);
    }
}
