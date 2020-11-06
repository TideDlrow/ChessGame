package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 兵
 */
public class Pawn extends Piece{
    @Override
    public boolean verify(byte nextX, byte nextY, Board board) {
        return false;
    }
}
