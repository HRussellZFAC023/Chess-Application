package controller;

import controller.pieces.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;


public class ChessBoard extends GridPane {
    //Attributes describing chessboards state
    private final Square[][] space = new Square[8][8];
    private Square lastClickedSquare = null;
    private int turnCounter = 0;
    private List<Square> legalMoves = new ArrayList<>();
    private boolean whitesTurn;


    public ChessBoard(boolean white) {
        boolean light;
        char ascii = 65;
        whitesTurn = white;

        for ( int x = 1;x <= 8;x++ ) {
            for ( int y = 0;y < 8;y++ ) {
                light = ((x + y) % 2 == 0);

                final int xVal = (x - 1);
                final int yVal = y;
                space[xVal][y] = new Square( light , x , y );

                if ( white ) {
                    this.add( space[xVal][yVal] , x , 7 - y );
                } else {
                    this.add( space[xVal][yVal] , x , y );
                }
                setActionEvents( xVal , yVal );
            }
            //sets row labels
            Label xchar = new Label( Character.toString( ascii++ ) );
            this.add( xchar , x , 8 );

            //sets column labels
            Label ynum = new Label( (Integer.toString( x )) );
            if ( white ) {
                this.add( ynum , 0 , (8 - x) ); //number of columns - x
            } else {
                this.add( ynum , 0 , x - 1 );
            }

        }
        defineStartPositions();

    }

    private void setActionEvents(int x , int y) {
        space[x][y].setOnMousePressed( event -> onSpaceClick( x , y ) );
        space[x][y].setOnMouseReleased( event -> disarmLegalMoves() );

        space[x][y].setId( this.getClass().getSimpleName() + System.currentTimeMillis() );
        space[x][y].setOnDragDetected( event -> {
            Dragboard db = space[x][y].startDragAndDrop( TransferMode.MOVE );
            ClipboardContent content = new ClipboardContent();
            //visual
            if ( space[x][y].getPiece() != null ) {
                Image image = new Image( space[x][y].getPiece().getImgString() ,
                        space[x][y].getWidth() , space[x][y].getHeight() ,
                        true , true );
                db.setDragView( image , 50 , 50 );
            }
            // Store node ID in order to know what is dragged.
            content.putString( space[x][y].getId() );
            db.setContent( content );
            event.consume();
        } );

        space[x][y].setOnDragDropped( (DragEvent event) -> {
            disarmLegalMoves();
            onSpaceClick( x , y );
            event.setDropCompleted( true );
            event.consume();
        } );

        space[x][y].setOnDragOver( (DragEvent event) -> {
            if ( legalMoves.contains( space[x][y] ) ) {
                event.acceptTransferModes( TransferMode.MOVE );
            }
            event.consume();
        } );

        space[x][y].setOnDragDone( (DragEvent event) -> {
            disarmLegalMoves();
            event.consume();
        } );


    }

    private void disarmLegalMoves() {
        if ( lastClickedSquare != null ) {
            lastClickedSquare.disarmButton();
        }
        for ( Square legalMove : legalMoves ) {
            legalMove.disarmButton();
        }

    }


    private void defineStartPositions() {
        this.space[0][0].setPiece( new Rook( true ) );
        this.space[1][0].setPiece( new Knight( true ) );
        this.space[2][0].setPiece( new Bishop( true ) );
        this.space[3][0].setPiece( new Queen( true ) );
        this.space[4][0].setPiece( new King( true ) );
        this.space[5][0].setPiece( new Bishop( true ) );
        this.space[6][0].setPiece( new Knight( true ) );
        this.space[7][0].setPiece( new Rook( true ) );

        for ( int i = 0;i < this.space[0].length;i++ )
            this.space[i][1].setPiece( new Pawn( true ) );

        // black pieces
        this.space[0][7].setPiece( new Rook( false ) );
        this.space[1][7].setPiece( new Knight( false ) );
        this.space[2][7].setPiece( new Bishop( false ) );
        this.space[3][7].setPiece( new Queen( false ) );
        this.space[4][7].setPiece( new King( false ) );
        this.space[5][7].setPiece( new Bishop( false ) );
        this.space[6][7].setPiece( new Knight( false ) );
        this.space[7][7].setPiece( new Rook( false ) );

        for ( int i = 0;i < this.space[0].length;i++ )
            this.space[i][6].setPiece( new Pawn( false ) );

    }

    private void onSpaceClick(int x , int y) {
        //todo fix error when pieces travel through other pieces, pawn logic and castling logic


        //System.out.println( ( char ) (x + 97) + "" + (y + 1) );
        //System.out.println( "the x value is " + x + "\nthe y value is " + y );
        //because null this section is skipped until after showAvailableMoves()


        if ( lastClickedSquare != null &&
                lastClickedSquare.getPiece() != null &&
                lastClickedSquare != space[x][y] &&
                legalMoves.contains( space[x][y] ) ) {

            //noinspection ConstantConditions (NullPointer imposible)
            if ( space[x][y].getPiece() == null &&  //if the space is empty
                    legalMoves.contains( space[x][y] ) || //AND legal move
                    space[x][y].getPiece().getColour() != lastClickedSquare.getPiece().getColour() &&//OR enemy piece
                            legalMoves.contains( space[x][y] ) ) {//AND legal move

                if ( whitesTurn == lastClickedSquare.getPiece().getColour() ) {

                    turnCounter++;
                    space[x][y].setPiece( lastClickedSquare.getPiece() );
                    lastClickedSquare.removePiece();
                }

                if ( turnCounter % 2 == 0 ) {
                    System.out.println( "whites turn" );
                    whitesTurn = true;
                } else {
                    System.out.println( "blacks turn" );
                    whitesTurn = false;
                }

            }
        }

        showAvailableMoves( x , y );// if the space contains a piece
        lastClickedSquare = space[x][y];//stores last piece click


    }


    private void showAvailableMoves(int x , int y) {
        legalMoves.clear();
        space[x][y].armButton();

        int ai = 0;
        int xVal;
        int yVal;


        if ( space[x][y].getPiece() != null ) {
            MoveList[] moves = space[x][y].getPiece().getPieceMoves();
            for ( MoveList m : moves ) {
                for ( int i = 1;i <= space[x][y].getPiece().getRange();i++ ) {


                    xVal = x + m.getX() * i;
                    yVal = y + m.getY() * i;

                    if(space[x][y].getPiece().getPieceName().equals("Pawn") && ! space[x][y].getPiece().getColour() ){
                        xVal = x + (-m.getX()) * i;
                        yVal = y + (-m.getY()) * i;
                    }

                    if ( yVal >= 0 && yVal < 8 && xVal >= 0 && xVal < 8 )//if square exists on board
                    {

                        if ( space[xVal][yVal].getPiece() == null ||
                                space[xVal][yVal].getPiece().getColour() != space[x][y].getPiece().getColour())
                        {
                            legalMoves.add( ai , space[xVal][yVal] );
                            legalMoves.get( ai ).armButton();
                            ai++;
                            if(space[xVal][yVal].getPiece() != null) break;
                        } else {
                            //stops checking this move if pieces are the same color
                            break;
                        }

                    }

                }
            }
        }
    }


    public void setSize(double size) {
        this.setMinSize( size , size );
        this.setMaxSize( size , size );
        this.setPrefSize( size , size );
    }

}
