package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 象
 */
public class Bishop extends Piece {
    public Bishop(int x, int y, boolean camp) {
        super(x, y, camp);
    }

    @Override
    public boolean verify(int nextX, int nextY, Board board) {
        Piece piece = board.getPieceByCoordinate(nextX, nextY);
        //原位置与目标位置的中间位置
        Piece centerPiece = board.getPieceByCoordinate((x + nextX) / 2, (y + nextY) / 2);
        return (
                //目标位置没有棋子或是敌方棋子
                (piece == null || piece.isCamp() == isCamp()) &&
                        //在规定范围
                        isEffectiveRange(nextX, nextY, board.isPlayerCamp()) &&
                        //走法合规
                        (Math.abs(nextX - x) == 2 && Math.abs(nextY - 2) == 2) &&
                        //不会卡象脚
                        centerPiece == null
        );
    }

    /**
     * 判断象是否处于棋盘规定范围
     *
     * @param x
     * @param y
     * @param playerCamp
     * @return
     */
    public boolean isEffectiveRange(int x, int y, boolean playerCamp) {
        if (x >= 1 && x <= 10) {
            //玩家阵营与棋子阵营相同意味着棋子被放在了棋盘的下方
            if (playerCamp == isCamp()) {
                return y >= 6 && y <= 10;
            } else {
                return y >= 1 && y <= 5;
            }
        }
        return false;
    }
}
