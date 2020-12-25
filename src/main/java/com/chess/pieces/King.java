package com.chess.pieces;

import com.chess.bean.Board;

/**
 * 将/帅
 */
public class King extends Piece {
    public King(int x, int y, boolean camp) {
        super(x, y, camp);
    }

    @Override
    public boolean verify(int nextX, int nextY, Board board) {
        Piece targetPiece = board.getPieceByCoordinate(nextX, nextY);
        int anotherKingX = -1, anotherKingY = -1;
        for (int i = 1; i <= board.getROW_NUM(); i++) {
            for (int j = 1; j <= board.getROW_NUM(); j++) {
                Piece piece = board.getPieceByCoordinate(i, j);
                if (piece instanceof King && piece.isCamp() != camp) {
                    anotherKingX = j;
                    anotherKingY = i;
                    break;
                }
            }

        }
        return
                //没有超过规定范围
                isEffectiveRange(nextX, nextY, board.isPlayerCamp()) &&
                //仅走了一步
                board.isOneStep(x, y, nextX, nextY) &&
                //目标位置和另一个将/帅之间不是直线，或者是直线且中间有其他棋子
                (!board.isStraightLine(nextX, nextY, anotherKingX, anotherKingY) || board.getPieceNum(nextX, nextY, anotherKingX, anotherKingY) > 0) &&
                //目标位置没有棋子，或为敌方棋子
                (targetPiece == null || targetPiece.isCamp() != camp);
    }

    public boolean isEffectiveRange(int x, int y, boolean playerCamp) {
        if (x >= 4 && x <= 6) {
            if (playerCamp == camp) {
                //如果棋子在棋盘下方，则判断y坐标是否在8~10之间
                return y >= 8 && y <= 10;
            } else {
                //如果棋子在棋盘上方方，则判断y坐标是否在1~3之间
                return y >= 1 && y <= 3;
            }
        }
        return false;
    }
}
