package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 马
 */
public class Knight extends Piece{
    @Override
    public boolean verify(byte nextX, byte nextY, Board board) {
        return false;
    }
}
