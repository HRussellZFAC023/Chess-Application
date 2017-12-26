package controller;

public enum MoveList {

    UP(0,1),
    DOWN(0,-1),
    LEFT(-1,0),
    RIGHT(1,0),
    DOUBLE_UP( 0 , 2 ),

    DOWN_LEFT(-1,-1),
    DOWN_RIGHT(1,-1),
    UP_LEFT(-1,1),
    UP_RIGHT(1,1),

    KNIGHT_LEFT_UP(-2, 1),
    KNIGHT_UP_LEFT(-1, 2),
    KNIGHT_UP_RIGHT(1, 2),
    KNIGHT_RIGHT_UP(2, 1),

    KNIGHT_RIGHT_DOWN(2, -1),
    KNIGHT_DOWN_RIGHT(1, -2),
    KNIGHT_DOWN_LEFT(-1, -2),
    KNIGHT_LEFT_DOWN( - 2 , - 1 );





    private int x;
    private int y;

    MoveList(int x , int y) {
        this.x = x;
        this.y = y;
    }


    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
