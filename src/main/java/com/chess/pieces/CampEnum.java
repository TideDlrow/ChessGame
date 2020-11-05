package com.chess.pieces;

/**
 * 阵营
 */
public enum CampEnum {
    RED, BLACK;

    public CampEnum fromString(String camp) {
        return switch (camp) {
            case "Red" -> RED;
            case "Black" -> BLACK;
            default -> null;
        };
    }
}
