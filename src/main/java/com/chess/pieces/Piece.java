package com.chess.pieces;

import lombok.Data;

/**
 * 棋子
 */
@Data
public abstract class Piece {
    /**
     * 坐标x值
     */
    private byte x;
    /**
     * 坐标y值
     */
    private byte y;
    /**
     * 所属阵营
     */
    private CampEnum camp;
    /**
     * 是否死亡
     */
    private boolean dead;

    /**
     * 是否符合行子规则
     * @param piece 下一步棋子的状态
     * @return true为符合 false不符合
     */
    public abstract boolean conformRules(Piece piece);


}
