package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 车
 */
public class Rook extends Piece {
    public Rook(int x, int y, boolean camp) {
        super(x, y, camp);
    }

    @Override
    public boolean verify(int nextX, int nextY, Board board) {
        Piece targetPiece = board.getPieceByCoordinate(nextX, nextY);

        return
                //走的是直线
                board.isStraightLine(x, y, nextX, nextY) &&
                        //原位置与目标位置不存在其他棋子
                        board.getPieceNum(x, y, nextX, nextY) == 0 &&
                        //目标位置不存在棋子或者是敌方棋子
                        (targetPiece == null || targetPiece.isCamp() != camp);
    }
}
