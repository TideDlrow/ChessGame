package com.chess.pieces;

/**
 * 将/帅
 */
public class King extends Piece{
    @Override
    public boolean conformRules(Piece piece) {
        return false;
    }
}
