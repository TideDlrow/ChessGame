package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 士
 */
public class Assistants extends Piece{
    @Override
    public boolean verify(byte nextX, byte nextY, Board board) {
        return false;
    }
}
