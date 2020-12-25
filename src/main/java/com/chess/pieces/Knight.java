package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 马
 */
public class Knight extends Piece {
    public Knight(int x, int y, boolean camp) {
        super(x, y, camp);
    }

    @Override
    public boolean verify(int nextX, int nextY, Board board) {
        Piece targetPiece = board.getPieceByCoordinate(nextX, nextY);
        //马脚点
        int blockPointX = -1, blockPointY = -1;
        if ((nextX - x) == 2 && Math.abs(nextY - y) == 1) {
            //2点和4点方向是同一个马脚
            blockPointX = x + 1;
            blockPointY = y;
        } else if ((x - nextX) == 2 && Math.abs(nextY - y) == 1) {
            //8点和10点方向是同一个马脚
            blockPointX = x - 1;
            blockPointY = y;
        } else if ((y - nextY) == 2 && Math.abs(x - nextX) == 1) {
            //11点和1点方向是同一个马脚
            blockPointX = x;
            blockPointY = y - 1;
        } else if ((nextY - y) == 2 && Math.abs(x - nextX) == 1) {
            //5点和7点方向是同一个马脚
            blockPointX = x;
            blockPointY = y + 1;
        }
        return
                //走的不是直线
                !board.isStraightLine(x, y, nextX, nextY) &&
                        //按规则走
                        ((Math.abs(nextX - x) + Math.abs(nextY - y)) == 3) &&
                        //马脚处没有棋子
                        board.getPieceByCoordinate(blockPointX, blockPointY) == null &&
                        //目标位置不存在棋子，或存在敌方棋子
                        (targetPiece == null || targetPiece.isCamp() != camp);
    }
}
