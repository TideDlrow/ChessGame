package com.chess.bean;

import lombok.Data;

import com.chess.pieces.Piece;

import java.util.HashMap;
import java.util.Map;


/**
 * 棋盘
 */
@Data
public class Board {
    /**
     * 棋子的排列
     * key -->  棋子的xy坐标组合而成的字符串 第一个是x坐标  第二个是y坐标 如 “10,5”表示x坐标是10 y坐标是5
     * value --> 棋子对象
     */
    private Map<String, Piece> currentBoard = new HashMap<>();
    /**
     * 列数
     */
    private final byte COL_NUM = 9;
    /**
     * 行数
     */
    private final byte ROW_NUM = 10;

    /**
     * 根据坐标获取棋子
     *
     * @param x 坐标x
     * @param y 坐标y
     * @return 棋子
     */
    public Piece getPieceByCoordinate(byte x, byte y) {
        return currentBoard.get(x + "," + y);
    }

    /**
     * 某列是否存在棋子
     *
     * @param colNum 列数
     * @return true存在
     */
    public boolean colIsExistPiece(byte colNum) {
        for (byte i = 1; i <= ROW_NUM; ++i) {
            if (getPieceByCoordinate(colNum, i) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 某行是否存在棋子
     *
     * @param rowNum 行数
     * @return true存在
     */
    public boolean rowIsExistPiece(byte rowNum) {
        for (byte i = 1; i <= ROW_NUM; ++i) {
            if (getPieceByCoordinate(i, rowNum) != null) {
                return true;
            }
        }
        return false;
    }
}
