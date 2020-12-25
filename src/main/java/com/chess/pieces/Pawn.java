package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 兵
 */
public class Pawn extends Piece{
    public Pawn(int x, int y, boolean camp) {
        super(x, y, camp);
    }

    @Override
    public boolean verify(int nextX, int nextY, Board board) {
        Piece targetPiece = board.getPieceByCoordinate(nextX,nextY);
        //先把走了不止一步的排除
        if (!board.isOneStep(x, y, nextX, nextY)) {
            return false;
        }
        boolean flag = false;
        //棋盘下方的兵/卒
        if (camp == board.isPlayerCamp()) {
            //未过河
            if (y >= 6) {
                flag = (x == nextX && nextY < y);
            } else {
                //过河后
                flag = (nextY <= y);
            }
        } else {
            //棋盘上方的兵/卒
            //未过河
            if (y <= 5) {
                flag = (x == nextX && nextY > y);
            } else {
                //过河后
                flag = (nextY >= y);
            }
        }
        return flag && (targetPiece == null || targetPiece.camp != camp);
    }
}
