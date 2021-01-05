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
     * 列数
     */
    private final int COL_NUM = 9;
    /**
     * 行数
     */
    private final int ROW_NUM = 10;

    /**
     * 棋盘中棋子的排列
     * 左上角的车坐标为1,1  马为2,1 象为3,1 ……
     */
    private Piece[][] board = new Piece[ROW_NUM][COL_NUM];
    /**
     * 玩家阵营，决定那方棋子处于棋盘下方
     */
    private boolean playerCamp;
    /**
     * 当前能移动的玩家,默认红方先手
     */
    private boolean currentCamp = false;

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
        setPiece(2, 1, new Knight(2, 1, anotherCamp));
        setPiece(3, 1, new Bishop(3, 1, anotherCamp));
        setPiece(4, 1, new Assistants(4, 1, anotherCamp));
        setPiece(5, 1, new King(5, 1, anotherCamp));
        setPiece(6, 1, new Assistants(6, 1, anotherCamp));
        setPiece(7, 1, new Bishop(7, 1, anotherCamp));
        setPiece(8, 1, new Knight(8, 1, anotherCamp));
        setPiece(9, 1, new Rook(9, 1, anotherCamp));

        setPiece(2, 3, new Cannon(2, 3, anotherCamp));
        setPiece(8, 3, new Cannon(8, 3, anotherCamp));

        setPiece(1, 4, new Pawn(1, 4, anotherCamp));
        setPiece(3, 4, new Pawn(3, 4, anotherCamp));
        setPiece(5, 4, new Pawn(5, 4, anotherCamp));
        setPiece(7, 4, new Pawn(7, 4, anotherCamp));
        setPiece(9, 4, new Pawn(9, 4, anotherCamp));
        //下方的棋子
        setPiece(1, 7, new Pawn(1, 7, camp));
        setPiece(3, 7, new Pawn(3, 7, camp));
        setPiece(5, 7, new Pawn(5, 7, camp));
        setPiece(7, 7, new Pawn(7, 7, camp));
        setPiece(9, 7, new Pawn(9, 7, camp));

        setPiece(2, 8, new Cannon(2, 8, camp));
        setPiece(8, 8, new Cannon(8, 8, camp));

        setPiece(1, 10, new Rook(1, 10, camp));
        setPiece(2, 10, new Knight(2, 10, camp));
        setPiece(3, 10, new Bishop(3, 10, camp));
        setPiece(4, 10, new Assistants(4, 10, camp));
        setPiece(5, 10, new King(5, 10, camp));
        setPiece(6, 10, new Assistants(6, 10, camp));
        setPiece(7, 10, new Bishop(7, 10, camp));
        setPiece(8, 10, new Knight(8, 10, camp));
        setPiece(9, 10, new Rook(9, 10, camp));
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
        if ((x < 1 || y < 1) || (x > COL_NUM || y > ROW_NUM)) {
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
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return -1表示这两个点不是直线，>=0则是棋子数量
     */
    public int getPieceNum(int x1, int y1, int x2, int y2) {
        if (!isStraightLine(x1, y1, x2, y2)) {
            return -1;
        }
        int count = 0;
        //列
        if (x1 == x2) {
            int begin = Math.min(y1, y2);
            int end = Math.max(y1, y2);
            for (int i = begin + 1; i < end; i++) {
                if (getPieceByCoordinate(x1, i) != null) {
                    count++;
                }
            }
        } else {
            int begin = Math.min(x1, x2);
            int end = Math.max(x1, x2);
            for (int i = begin + 1; i < end; i++) {
                if (getPieceByCoordinate(i, y1) != null) {
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
     * @return 棋局是否结束  false未结束，true结束
     */
    public boolean movePiece(int x1, int y1, int x2, int y2) throws BusinessException {
        Piece piece = getPieceByCoordinate(x1, y1);
        if (piece == null) {
            throw new BusinessException(BusinessError.NO_PIECE);
        }
        if (!piece.verify(x2, y2, this)) {
            throw new BusinessException(BusinessError.INVALID_MOVE);
        }
        //如果棋子移动到指定位置，检测将或帅是否死亡
        Piece targetPiece = getPieceByCoordinate(x2, y2);
        if (targetPiece instanceof King) {
            return true;
        }
        //将/帅未死亡，棋局未结束。把棋子挪到指定位置，原先位置置空
        setPiece(x2, y2, piece);
        setPiece(x1, y1, null);
        piece.setX(x2);
        piece.setY(y2);
        //棋子移动后翻转下棋方
        currentCamp = !piece.isCamp();
        return false;
    }

}
