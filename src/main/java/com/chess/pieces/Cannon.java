package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 炮
 */
public class Cannon extends Piece{
    @Override
    public boolean verify(byte nextX, byte nextY, Board board) {
        return false;
    }
}
