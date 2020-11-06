package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 象
 */
public class Bishop extends Piece{
    @Override
    public boolean verify(byte nextX, byte nextY, Board board) {
        return false;
    }
}
