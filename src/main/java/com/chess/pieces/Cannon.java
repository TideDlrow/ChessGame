package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 炮
 */
public class Cannon extends Piece {
    public Cannon(int x, int y, boolean camp) {
        super(x, y, camp);
    }

    @Override
    public boolean verify(int nextX, int nextY, Board board) {
        if (!board.isStraightLine(x, y, nextX, nextY)) {
            return false;
        }
        int pieceNum = board.getPieceNum(x, y, nextX, nextY);
        Piece piece = board.getPieceByCoordinate(nextX, nextY);
        if (piece == null && pieceNum == 0) {
            //目标位置没有棋子，且原位置与目标位置之间也没有棋子
            return true;
        } else if (piece == null && pieceNum > 0) {
            //这里是为了短路下面的条件，防止piece是null报错
            return false;
        } else if (piece.isCamp() != camp && pieceNum == 1) {
            //目标位置为敌方棋子，且原位置与目标位置中间只有有一个棋子
            return true;
        }
        return false;
    }
}
