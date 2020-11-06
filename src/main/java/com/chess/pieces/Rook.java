package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 车
 */
public class Rook extends Piece{
    @Override
    public boolean verify(byte nextX, byte nextY, Board board) {
        return false;
    }
}
