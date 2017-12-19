package controller;

import controller.pieces.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ChessBoard extends GridPane {
    private final Space[][] spaces = new Space[8][8];

    public ChessBoard () {
        super();
        boolean light;
        char ascii = 65;

        for (int x = 1;x <= 8;x++) {
            for (int y = 0;y < 8;y++) {

                light = ((x + y) % 2 == 0);
                spaces[x - 1][y] = new Space (light,x,y);
                final int Xval = (x - 1);
                final int Yval = y;
                spaces[x - 1][(y)].setOnAction (e -> onSpaceClick (Xval,Yval));
                this.add (spaces[(x - 1)][y],x,y);
            }
            //sets row labels
            Label xchar = new Label (Character.toString (ascii++));
            xchar.getStyleClass ().add ("label-fill");
            this.add (xchar,x,8);
            //sets column labels
            Label ynum = new Label ((Integer.toString (x)));
            ynum.getStyleClass ().add ("label-fill");
            int columns = 8;
            this.add (ynum,0,(columns - x));
        }
        defineStartPositions ();
    }

    private void defineStartPositions () {
        this.spaces[0][0].setPiece( new Rook  (true) );
//        this.spaces[1][0].setPiece( new Knight(true) );
//        this.spaces[2][0].setPiece( new Bishop(true) );
//        this.spaces[3][0].setPiece( new Queen (true) );
//        this.spaces[4][0].setPiece( new King  (true) );
//        this.spaces[5][0].setPiece( new Bishop(true) );
//        this.spaces[6][0].setPiece( new Knight(true) );
//        this.spaces[7][0].setPiece( new Rook  (true) );
//
//        for (int i = 0; i < this.spaces[0].length; i++)
//            this.spaces[i][1].setPiece( new Pawn(true) );
//
//        // black pieces
//        this.spaces[0][7].setPiece( new Rook  (false) );
//        this.spaces[1][7].setPiece( new Knight(false) );
//        this.spaces[2][7].setPiece( new Bishop(false) );
//        this.spaces[3][7].setPiece( new Queen (false) );
//        this.spaces[4][7].setPiece( new King  (false) );
//        this.spaces[5][7].setPiece( new Bishop(false) );
//        this.spaces[6][7].setPiece( new Knight(false) );
//        this.spaces[7][7].setPiece( new Rook  (false) );
//
//        for (int i = 0; i < this.spaces[0].length; i++)
//            this.spaces[i][6].setPiece( new Pawn(false) );

    }

    private void onSpaceClick (int x,int y) {
        System.out.println ("the x value is" + x);
        System.out.println ("the y value is" + y);

    }


}
