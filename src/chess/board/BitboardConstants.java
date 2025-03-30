package chess.board;

public class BitboardConstants {
    // Files
    public static final long FILE_A = 0x8080808080808080L;
    public static final long FILE_B = 0x4040404040404040L;
    public static final long FILE_C = 0x2020202020202020L;
    public static final long FILE_D = 0x1010101010101010L;
    public static final long FILE_E = 0x0808080808080808L;
    public static final long FILE_F = 0x0404040404040404L;
    public static final long FILE_G = 0x0202020202020202L;
    public static final long FILE_H = 0x0101010101010101L;

    // Ranks
    public static final long RANK_1 = 0x00000000000000FFL;
    public static final long RANK_2 = 0x000000000000FF00L;
    public static final long RANK_3 = 0x0000000000FF0000L;
    public static final long RANK_4 = 0x00000000FF000000L;
    public static final long RANK_5 = 0x000000FF00000000L;
    public static final long RANK_6 = 0x0000FF0000000000L;
    public static final long RANK_7 = 0x00FF000000000000L;
    public static final long RANK_8 = 0xFF00000000000000L;

    // White Pieces Initial
    public static final long WHITE_PAWNS_INITIAL = 0x000000000000FF00L;
    public static final long WHITE_KNIGHTS_INITIAL = 0x0000000000000042L;
    public static final long WHITE_BISHOPS_INITIAL = 0x0000000000000024L;
    public static final long WHITE_ROOKS_INITIAL = 0x0000000000000081L;
    public static final long WHITE_QUEENS_INITIAL = 0x0000000000000008L;
    public static final long WHITE_KINGS_INITIAL = 0x0000000000000010L;

    // Black Pieces Initial
    public static final long BLACK_PAWNS_INITIAL = 0x00FF000000000000L;
    public static final long BLACK_KNIGHTS_INITIAL = 0x4200000000000000L;
    public static final long BLACK_BISHOPS_INITIAL = 0x2400000000000000L;
    public static final long BLACK_ROOKS_INITIAL = 0x8100000000000000L;
    public static final long BLACK_QUEENS_INITIAL = 0x0800000000000000L;
    public static final long BLACK_KINGS_INITIAL = 0x1000000000000000L;

    // Edge masks for move generation
    public static final long NOT_FILE_A = ~FILE_A;  // All squares except A-file
    public static final long NOT_FILE_H = ~FILE_H;  // All squares except H-file

    public static final long NOT_FILE_AB = ~(FILE_A | FILE_B);  // For knight moves that go 2 east
    public static final long NOT_FILE_GH = ~(FILE_G | FILE_H);  // For knight moves that go 2 west

    // Square Indices
    public static final int A1 = 0, B1 = 1, C1 = 2, D1 = 3, E1 = 4, F1 = 5, G1 = 6, H1 = 7;
    public static final int A2 = 8, B2 = 9, C2 = 10, D2 = 11, E2 = 12, F2 = 13, G2 = 14, H2 = 15;
    public static final int A3 = 16, B3 = 17, C3 = 18, D3 = 19, E3 = 20, F3 = 21, G3 = 22, H3 = 23;
    public static final int A4 = 24, B4 = 25, C4 = 26, D4 = 27, E4 = 28, F4 = 29, G4 = 30, H4 = 31;
    public static final int A5 = 32, B5 = 33, C5 = 34, D5 = 35, E5 = 36, F5 = 37, G5 = 38, H5 = 39;
    public static final int A6 = 40, B6 = 41, C6 = 42, D6 = 43, E6 = 44, F6 = 45, G6 = 46, H6 = 47;
    public static final int A7 = 48, B7 = 49, C7 = 50, D7 = 51, E7 = 52, F7 = 53, G7 = 54, H7 = 55;
    public static final int A8 = 56, B8 = 57, C8 = 58, D8 = 59, E8 = 60, F8 = 61, G8 = 62, H8 = 63;
}
