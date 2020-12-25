package com.chess.bean;

import com.chess.exception.BusinessError;
import com.chess.exception.BusinessException;
import com.chess.pieces.*;
import lombok.Data;

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
//    private Map<String, Piece> currentBoard = new HashMap<>();
    /**
     * 列数
     */
    private final int COL_NUM = 9;
    /**
     * 行数
     */
    private final int ROW_NUM = 10;

    /**
     * 棋盘中棋子的排列
     */
    private Piece[][] board = new Piece[ROW_NUM][COL_NUM];
    /**
     * 玩家阵营，决定那方棋子处于棋盘下方
     */
    private boolean playerCamp;
    /**
     * 当前能移动的玩家
     */
    private boolean currentCamp;

    public Board(boolean camp) {
        this.playerCamp = camp;
        this.init(camp);
    }

    /**
     * 初始化棋子的位置
     *
     * @param camp 阵营 false代表红方 true代表黑方 表示那方的棋子在棋盘下方
     */
    private void init(boolean camp) {
        boolean anotherCamp = !camp;
        //上方的棋子
        setPiece(1, 1, new Rook(1, 1, anotherCamp));
        setPiece(1, 2, new Knight(1, 2, anotherCamp));
        setPiece(1, 3, new Bishop(1, 3, anotherCamp));
        setPiece(1, 4, new Assistants(1, 4, anotherCamp));
        setPiece(1, 5, new King(1, 5, anotherCamp));
        setPiece(1, 6, new Assistants(1, 6, anotherCamp));
        setPiece(1, 7, new Bishop(1, 7, anotherCamp));
        setPiece(1, 8, new Knight(1, 8, anotherCamp));
        setPiece(1, 9, new Rook(1, 9, anotherCamp));

        setPiece(3, 2, new Cannon(3, 2, anotherCamp));
        setPiece(3, 8, new Cannon(3, 8, anotherCamp));

        setPiece(4, 1, new Pawn(5, 1, anotherCamp));
        setPiece(4, 3, new Pawn(5, 3, anotherCamp));
        setPiece(4, 5, new Pawn(5, 5, anotherCamp));
        setPiece(4, 7, new Pawn(5, 7, anotherCamp));
        setPiece(4, 9, new Pawn(5, 9, anotherCamp));
        //下方的棋子
        setPiece(7, 1, new Pawn(7, 1, camp));
        setPiece(7, 3, new Pawn(7, 3, camp));
        setPiece(7, 5, new Pawn(7, 5, camp));
        setPiece(7, 7, new Pawn(7, 7, camp));
        setPiece(7, 9, new Pawn(7, 9, camp));

        setPiece(8, 2, new Cannon(8, 2, camp));
        setPiece(8, 8, new Cannon(8, 8, camp));

        setPiece(10, 1, new Rook(10, 1, camp));
        setPiece(10, 2, new Knight(10, 2, camp));
        setPiece(10, 3, new Bishop(10, 3, camp));
        setPiece(10, 4, new Assistants(10, 4, camp));
        setPiece(10, 5, new King(10, 5, camp));
        setPiece(10, 6, new Assistants(10, 6, camp));
        setPiece(10, 7, new Bishop(10, 7, camp));
        setPiece(10, 8, new Knight(10, 8, camp));
        setPiece(10, 9, new Rook(10, 9, camp));
    }

    public void setPiece(int x, int y, Piece piece) {
        board[y - 1][x - 1] = piece;
    }

    /**
     * 根据坐标获取棋子
     *
     * @param x 坐标x
     * @param y 坐标y
     * @return 棋子
     */
    public Piece getPieceByCoordinate(int x, int y) {
//        return currentBoard.get(x + "," + y);
        if ((x < 1 || y < 1) && (x > COL_NUM || y > ROW_NUM)) {
            //坐标异常
            return null;
        }
        return board[y - 1][x - 1];
    }

    /**
     * 某列是否存在棋子
     *
     * @param colNum 列数
     * @return true存在
     */
    public boolean colIsExistPiece(int colNum) {
        for (int i = 1; i <= ROW_NUM; ++i) {
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
    public boolean rowIsExistPiece(int rowNum) {
        for (int i = 1; i <= ROW_NUM; ++i) {
            if (getPieceByCoordinate(i, rowNum) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * 两坐标是否处于一条直线
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public boolean isStraightLine(int x1, int y1, int x2, int y2) {
        return (x1 - x2) * (y1 - y2) == 0;
    }

    /**
     * 是否只移动了一步
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    public boolean isOneStep(int x1, int y1, int x2, int y2) {
        return isStraightLine(x1, y1, x2, y2) &&
                (Math.abs(x2 - x1) + Math.abs(y2 - y1)) == 1;
    }

    /**
     * 获取从x1,y1到x2,y2的棋子数量。不包括这两个点
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return -1表示这两个点不是直线，>=0则是棋子数量
     */
    public int getPieceNum(int x1,int y1,int x2,int y2){
        if (!isStraightLine(x1,y1,x2,y2)){
            return -1;
        }
        int count = 0;
        //列
        if (x1==x2){
            int begin = Math.min(y1, y2);
            int end = Math.max(y1,y2);
            for (int i = begin; i < end-1; i++) {
                if (getPieceByCoordinate(x1,i) != null){
                    count++;
                }
            }
        }else {
            int begin = Math.min(x1, x2);
            int end = Math.max(x1,x2);
            for (int i = begin; i < end-1; i++) {
                if (getPieceByCoordinate(x1,i) != null){
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 把棋子从x1,y1处移动到x2,y2处
     *
     * @param x1 初始坐标
     * @param y1 初始坐标
     * @param x2 目的坐标
     * @param y2 目的坐标
     */
    public void movePiece(int x1, int y1, int x2, int y2) throws BusinessException {
        Piece piece = getPieceByCoordinate(x1, y1);
        if (piece == null) {
            throw new BusinessException(BusinessError.NO_PIECE);
        }
        if (!piece.verify(x2, y2, this)) {
            throw new BusinessException(BusinessError.INVALID_MOVE);
        }
        //把棋子挪到指定位置，原先位置置空
        setPiece(x2, y2, piece);
        setPiece(x1, y1, null);
        piece.setX(x2);
        piece.setY(y2);
    }
}
