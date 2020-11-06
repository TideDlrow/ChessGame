package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 将/帅
 */
public class King extends Piece{
    @Override
    public boolean verify(byte nextX, byte nextY, Board board) {
        return false;
    }
}
