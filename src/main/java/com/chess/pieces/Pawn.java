package com.chess.pieces;

/**
 * 兵
 */
public class Pawn extends Piece{
    @Override
    public boolean conformRules(Piece piece) {
        return false;
    }
}
