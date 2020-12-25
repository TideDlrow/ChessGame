package com.chess.pieces;

import com.chess.bean.Board;
import lombok.Data;

/**
 * 棋子
 */
@Data
public abstract class Piece {
    /**
     * 坐标x值
     */
    protected int x;
    /**
     * 坐标y值
     */
    protected int y;
    /**
     * 所属阵营
     * false 红方
     * true 黑方
     */
    protected boolean camp;
    /**
     * 是否死亡
     */
    protected boolean dead = false;

    public Piece(int x, int y, boolean camp) {
        this.x = x;
        this.y = y;
        this.camp = camp;
    }

    //    private final int WEIGHT;

    /**
     * 是否符合行子规则
     *
     * @param nextX 下一步的X坐标
     * @param nextY 下一步的X坐标
     * @param board 棋盘
     * @return true为符合 false不符合
     */
    public abstract boolean verify(int nextX, int nextY, Board board);

    /**
     * 移动棋子
     * 先判断是否符合行子规则 若符合则进行移动 否则抛出错误
     * 再判断落点是否存在敌方棋子  若存在则将敌方棋子的状态改为死亡
     *
     * @param nextX 下一步的X坐标
     * @param nextY 下一步的X坐标
     * @param board 棋盘
     */
    public void move(int nextX, int nextY, Board board) {
        //
        boolean verifyResult = verify(nextX, nextY, board);
        if (verifyResult) {
            this.x = nextX;
            this.y = nextY;
            Piece piece = board.getPieceByCoordinate(nextX, nextY);
            //只要存在棋子 必定是敌方棋子 否则验证行子规则时会失败
            if (piece != null) {
                piece.setDead(false);
            }
        }
    }

}
