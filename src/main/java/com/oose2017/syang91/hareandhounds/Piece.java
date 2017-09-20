package com.oose2017.syang91.hareandhounds;

/**
 * Created by syang91 on 17/9/7.
 *
 */
public class Piece {
    private int x;
    private int y;
    private String pieceType;


    public Piece(int x, int y, String pieceType){
        this.x = x;
        this.y = y;
        this.pieceType = pieceType;
    }

    public String getPieceType() { return pieceType; }
    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }

//    public int getX() { return x;}
//    public void setX(int x) {this.x = x;}
//
//    public int getY() { return y;}
//    public void setY(int y) {this.x = y;}


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}

