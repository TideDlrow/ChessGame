package com.chess.controller;

import com.chess.pieces.Piece;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 棋盘
 */
@Data
public class Board {
    private Map<String, Piece> currentBoard = new HashMap<>();
}
