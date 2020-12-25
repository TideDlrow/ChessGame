package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 士
 */
public class Assistants extends Piece {
    public Assistants(int x, int y, boolean camp) {
        super(x, y, camp);
    }

    @Override
    public boolean verify(int nextX, int nextY, Board board) {
        Piece piece = board.getPieceByCoordinate(nextX, nextY);
        boolean playerCamp = board.isPlayerCamp();

        return (
                //目标位置没有棋子或是敌方棋子
                (piece == null || camp != piece.isCamp()) &&
                        //目标位置在规定范围
                        isEffectiveRange(nextX, nextY, playerCamp) &&
                        //士是斜向移动，目标位置与原位置的坐标差值为1
                        (Math.abs(nextX - x) == 1 && Math.abs(nextY - 1) == 1)
        );
    }

    /**
     * 判断士是否处于棋盘规定范围
     *
     * @param x
     * @param y
     * @param playerCamp
     * @return
     */
    public boolean isEffectiveRange(int x, int y, boolean playerCamp) {
        if (x>=4 && x<=6){
            //玩家阵营与棋子阵营相同意味着棋子被放在了棋盘的下方
            if (playerCamp==isCamp()){
                return y>=8 && y<=10;
            }else {
                return y>=1 && y<=3;
            }
        }
        return false;
    }
}
