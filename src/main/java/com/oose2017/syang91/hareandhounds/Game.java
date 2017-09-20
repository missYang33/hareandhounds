package com.oose2017.syang91.hareandhounds;

import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by syang91 on 9/19/17.
 */
public class Game {

    public String pieceType;
    public String state;
    public ArrayList<Board> boards;
    public HashMap <Board, Integer> boardHistory;

    public Game(String pieceType) {

        this.pieceType = pieceType;
        this.state = "WAITING_FOR_SECOND_PLAYER";
        this.boards = new ArrayList<>();
        this.boardHistory = new HashMap<>();
    }
}
