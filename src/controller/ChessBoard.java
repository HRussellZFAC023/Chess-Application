package controller;

import controller.pieces.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class ChessBoard extends GridPane {
    private final Square[][] space = new Square[8][8];
    private Square lastClickedSquare = null;

    public ChessBoard (boolean white) {
        super();
        boolean light;
        char ascii = 65;
       // white = ! white;

        for ( int x = 1;x <= 8;x++ ) {
            for ( int y = 0;y < 8;y++ ) {
                light = ((x + y) % 2 == 0);

                space[x - 1][y] = new Square( light , x , y );
                final int Xval = (x - 1);
                final int Yval = y;

                if (white) { this.add(space[x - 1][y], x, 7 - y); }
                else { this.add( space[x - 1][y] , x , y ); }
                space[x - 1][(y)].setOnAction( e -> onSpaceClick( Xval , Yval ) );

            }
            //sets row labels
            Label xchar = new Label( Character.toString( ascii++ ) );
            this.add( xchar , x , 8 );

            //sets column labels
            Label ynum = new Label( (Integer.toString( x )) );
            if( white ) {
                this.add( ynum , 0 , (8 - x) ); //number of columns - x
            } else {
                this.add( ynum , 0 , x - 1);
            }

        }
        defineStartPositions();

    }

    private void defineStartPositions () {
        this.space[0][0].setPiece( new Rook  (true) );
        this.space[1][0].setPiece( new Knight(true) );
        this.space[2][0].setPiece( new Bishop(true) );
        this.space[3][0].setPiece( new Queen (true) );
        this.space[4][0].setPiece( new King  (true) );
        this.space[5][0].setPiece( new Bishop(true) );
        this.space[6][0].setPiece( new Knight(true) );
        this.space[7][0].setPiece( new Rook  (true) );

        for (int i = 0;i < this.space[0].length;i++)
            this.space[i][1].setPiece( new Pawn(true) );

        // black pieces
        this.space[0][7].setPiece( new Rook  (false) );
        this.space[1][7].setPiece( new Knight(false) );
        this.space[2][7].setPiece( new Bishop(false) );
        this.space[3][7].setPiece( new Queen (false) );
        this.space[4][7].setPiece( new King  (false) );
        this.space[5][7].setPiece( new Bishop(false) );
        this.space[6][7].setPiece( new Knight(false) );
        this.space[7][7].setPiece( new Rook  (false) );

        for (int i = 0;i < this.space[0].length;i++)
            this.space[i][6].setPiece( new Pawn(false) );

    }

    private void onSpaceClick (int x, int y) {
        System.out.println( ( char ) (x + 97) + "" + (y + 1) );
        System.out.println( "the x value is " + x + "\nthe y value is " + y );

        if(lastClickedSquare!=null) {
            lastClickedSquare.setDefault();
        }
        space[x][y].setActive();
        //space[x][y].setDefault();
        lastClickedSquare = space[x][y];
    }



    public void setSize(double size) {
        this.setMinSize(size,size);
        this.setMaxSize(size,size);
        this.setPrefSize(size,size);

    }

}
