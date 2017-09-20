package com.oose2017.syang91.hareandhounds;

/**
 * Created by syang91
 * let 0 denote the empty position
 * let 1 denote the hare
 * let 2 denote the hound
 */



public class Board {

    int[][] boardGrid = new int[5][3];
    Piece hound1;
    Piece hound2;
    Piece hound3;

    Piece hare;

    public Board() {

        this.hound1 = new Piece(1, 0, "HOUND");
        this.hound2 = new Piece(0, 1, "HOUND");
        this.hound3 = new Piece(1, 2, "HOUND");
        this.hare = new Piece(4, 1, "HARE");

        this.boardGrid = refreshBoardGrid(hound1, hound2, hound3, hare);
    }

 /*   public Board(Piece hound1, Piece hound2, Piece hound3, Piece hare) {
        this.hound1 = hound1;
        this.hound2 = hound2;
        this.hound3 = hound3;
        this.hare = hare;

        this.boardGrid = refreshBoardGrid(hound1, hound2, hound3, hare);
    }*/

    // hound = 2, hare = 1, others = 0, (0,0), (0,2), (4,0), (4,2) are not legal
    public int[][] refreshBoardGrid(Piece hound1, Piece hound2, Piece hound3, Piece hare) {
        int[][] newBoardGrid = new int[5][3];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                if ((i == hound1.getX() && j == hound1.getY()) || (i == hound2.getX() && j == hound2.getY()) || (i == hound3.getX() && j == hound3.getY())) {
                    newBoardGrid[i][j] = 2;
                } else if ((i == hare.getX() && j == hare.getY())) {
                    newBoardGrid[i][j] = 1;
                } else {
                    newBoardGrid[i][j] = 0;
                }
            }
        }
        return newBoardGrid;
    }


    public boolean isValid(int fromX, int fromY, int toX, int toY) {
        // out of bound
        if (toX < 0) {
            return false;
        }
        if (toY < 0) {
            return false;
        }
        if (toX > 4) {
            return false;
        }
        if (toY > 2) {
            return false;
        }
        // four corners
        if ((toX == 0 || toX == 4) && (toY == 0 || toY == 2)) {
            return false;
        }
        if ((Math.abs(fromX - toX) > 1) || (Math.abs(fromY - toY) > 1)) {
            return false;
        }
        if (fromX == toX && fromY == toY) {
            return false;
        }
        return true;
    }

    public Board move(int fromX, int fromY, int toX, int toY, String playerType) throws GamePlayerPositionMismatch,
            GameIllegalMoveException, GameIllegalPlayerException{

        Board refreshBoard = new Board();
        refreshBoard.hound1.setX(this.hound1.getX());
        refreshBoard.hound1.setY(this.hound1.getY());
        refreshBoard.hound2.setX(this.hound2.getX());
        refreshBoard.hound2.setY(this.hound2.getY());
        refreshBoard.hound3.setX(this.hound3.getX());
        refreshBoard.hound3.setY(this.hound3.getY());
        refreshBoard.hare.setX(this.hare.getX());
        refreshBoard.hare.setY(this.hare.getY());
        refreshBoard.boardGrid =refreshBoardGrid(refreshBoard.hound1,refreshBoard.hound2,refreshBoard.hound3,refreshBoard.hare);

        if (isValid(fromX, fromY, toX, toY)) {
            if (playerType.equals("HOUND")) {
                if ((toX - fromX) >= 0) {

                    if (refreshBoard.hound1.getX() == fromX && refreshBoard.hound1.getY() == fromY) {
                        refreshBoard.hound1.setX(toX);
                        refreshBoard.hound1.setY(toY);
                        refreshBoard.boardGrid = refreshBoardGrid(refreshBoard.hound1, refreshBoard.hound2, refreshBoard.hound3, refreshBoard.hare);

                    }else if (refreshBoard.hound2.getX() == fromX && refreshBoard.hound2.getY() == fromY) {
                        refreshBoard.hound2.setX(toX);
                        refreshBoard.hound2.setY(toY);
                        refreshBoard.boardGrid = refreshBoardGrid(refreshBoard.hound1, refreshBoard.hound2, refreshBoard.hound3, refreshBoard.hare);

                    }else if (refreshBoard.hound3.getX() == fromX && refreshBoard.hound3.getY() == fromY) {
                        refreshBoard.hound3.setX(toX);
                        refreshBoard.hound3.setY(toY);
                        refreshBoard.boardGrid = refreshBoardGrid(refreshBoard.hound1, refreshBoard.hound2, refreshBoard.hound3, refreshBoard.hare);

                    }else {
                        throw new GamePlayerPositionMismatch("game player's position mismatch", null);
                    }

                } else {
                    throw new GameIllegalMoveException("hound can not move backword", null);
                }

            } else if (playerType.equals("HARE")) {
                if (refreshBoard.hare.getX() == fromX && refreshBoard.hare.getY() == fromY) {
                    refreshBoard.hare.setX(toX);
                    refreshBoard.hare.setY(toY);
                    refreshBoard.boardGrid = refreshBoardGrid(refreshBoard.hound1, refreshBoard.hound2, refreshBoard.hound3, refreshBoard.hare);

                } else {
                    throw new GamePlayerPositionMismatch("game player's position mismatch", null);
                }

            } else {
                throw new GameIllegalPlayerException("This is not a valid player", null);
            }
        }else{
            throw new GameIllegalMoveException("Illegal move",null);
        }
        return refreshBoard;




    }
    Boolean isEqual(Board comparedBoard){
        Boolean hound1 = (this.hound1.getX() == comparedBoard.hound1.getX()) && (this.hound1.getY() == comparedBoard.hound1.getY());
        Boolean hound2 = (this.hound2.getX() == comparedBoard.hound2.getX()) && (this.hound2.getY() == comparedBoard.hound2.getY());
        Boolean hound3 = (this.hound3.getX() == comparedBoard.hound3.getX()) && (this.hound3.getY() == comparedBoard.hound3.getY());
        Boolean hare = (this.hare.getX() == comparedBoard.hare.getX()) && (this.hare.getY() == comparedBoard.hare.getY());
        return hound1 && hound2 && hound3 && hare;
    }

    public static class GameIllegalPlayerException extends Exception {
        public GameIllegalPlayerException(String message, Throwable cause){
            super(message,cause);
        }
    }
    public static class GameIllegalMoveException extends Exception {
        public GameIllegalMoveException(String message, Throwable cause){
            super(message,cause);
        }
    }
    public static class GamePlayerPositionMismatch extends Exception {
        public GamePlayerPositionMismatch(String message, Throwable cause){
            super(message,cause);
        }
    }




}


