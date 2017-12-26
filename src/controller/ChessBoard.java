package controller;

import controller.pieces.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;


public class ChessBoard extends GridPane {
    //Attributes describing chessboards state
    private final Square[][] space = new Square[8][8];
    private List<Square> legalMoves = new ArrayList<>();
    private Square lastClickedSquare = null;
    private Square enpassant = null;    //stores the position of enpassant

    private int turnCounter = 0;
    private boolean whitesTurn;
    private String moveString;

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

    private void onSpaceClick(final int X , final int Y) {
        //todo fix error when pieces travel through other pieces, pawn logic and castling logic

        //System.out.println( ( char ) (X + 97) + "" + (Y + 1) );
        //System.out.println( "the X value is " + X + "\nthe Y value is " + Y );
        //because null this section is skipped until after showAvailableMoves()


        if ( lastClickedSquare != null &&
                lastClickedSquare.getPiece() != null &&
                lastClickedSquare != space[X][Y] &&
                legalMoves.contains( space[X][Y] ) ) {


            //noinspection ConstantConditions (NullPointer imposible)
            if ( space[X][Y].getPiece() == null &&  //if the space is empty
                    legalMoves.contains( space[X][Y] ) || //AND legal move
                    space[X][Y].getPiece().getColour() != lastClickedSquare.getPiece().getColour() &&//OR enemy piece
                            legalMoves.contains( space[X][Y] ) ) {//AND legal move

                if ( whitesTurn == lastClickedSquare.getPiece().getColour() ) {

                    turnCounter++;//increment turn counter
                    lastClickedSquare.getPiece().setMoveCounter(lastClickedSquare.getPiece().getMoveCounter()+1);//increment move counter


                    space[X][Y].setPiece( lastClickedSquare.getPiece() );


                    moveString = space[X][Y].getPiece().getPieceName() + " to " + ( char ) (X + 97) + "" + (Y + 1);
                    System.out.println( moveString );
                    lastClickedSquare.removePiece();

                    if(enpassant != null){
                        enpassant.removePiece();
                        enpassant = null;
                    }

                    if(space[X][Y].getPiece().getPieceName().equals("Pawn") &&
                            (Y == 7 && space[X][Y].getPiece().getColour()) ||
                            (Y == 0 &&!space[X][Y].getPiece().getColour()))
                    {
                        space[X][Y].setPiece(choosePiece(space[X][Y].getPiece().getColour()));
                    }
                    if(space[X][Y].getPiece().getPieceName().equals("King") &&
                            space[X][Y].getPiece().getMoveCounter() == 1)
                    {
                        if ( X == 6 ) {
                            space[X+1][Y].removePiece();
                            space[X-1][Y].setPiece(new Rook(space[X][Y].getPiece().getColour() ));
                        }
                        if ( X == 2 ) {
                            space[X-2][Y].removePiece();
                            space[X+1][Y].setPiece(new Rook(space[X][Y].getPiece().getColour() ));
                        }


                        System.out.println( "you castled" );
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
        }

        showAvailableMoves( X , Y );// if the space contains a piece
        lastClickedSquare = space[X][Y];//stores last piece click


    }

    private Piece choosePiece(boolean colour) {
        ButtonType option1 = new ButtonType("♕", ButtonBar.ButtonData.OTHER);
        ButtonType option2 = new ButtonType("♖",ButtonBar.ButtonData.OTHER);
        ButtonType option3 = new ButtonType("♗",ButtonBar.ButtonData.OTHER);
        ButtonType option4 = new ButtonType("♘",ButtonBar.ButtonData.OTHER);

        Alert alert = new Alert(Alert.AlertType.NONE,"Promotion:" ,option1,option2,option3,option4);
        alert.showAndWait();
        if (alert.getResult() == option2) return new Rook( colour );
        else if (alert.getResult() == option3) return new Bishop( colour );
        else if (alert.getResult() == option4) return new Knight( colour );
        else return new Queen( true );
    }


    private void showAvailableMoves(final int X , final int Y) {
        legalMoves.clear();
        space[X][Y].armButton();

        int ai = 0;
        int xVal;
        int yVal;
        boolean enpassantLeft = false;
        boolean enpassantRight = false;


        if ( space[X][Y].getPiece() != null ) {
            MoveList[] moves = space[X][Y].getPiece().getPieceMoves();

            if ( space[X][Y].getPiece().getPieceName().equals( "King" ) &&
                    space[X][Y].getPiece().getMoveCounter() == 0) {
                //Queen-side castling
                if ( space[0][0].getPiece() != null &&
                        space[0][0].getPiece().getMoveCounter() == 0 ||
                        space[0][7].getPiece().getMoveCounter() == 0 ) {
                    if ( space[X][Y].getPiece().getColour() &&
                            space[1][0].getPiece() == null &&
                            space[2][0].getPiece() == null &&
                            space[3][0].getPiece() == null ) {
                        legalMoves.add( space[2][0] );
                        space[2][0].armButton();
                    }
                    else if ( !space[X][Y].getPiece().getColour()  &&
                            space[1][7].getPiece() == null &&
                            space[2][7].getPiece() == null &&
                            space[3][7].getPiece() == null ) {
                        legalMoves.add( space[2][7] );
                        space[2][7].armButton();
                    }
                }

                //king-side castling
                if ( space[7][0].getPiece() != null &&
                        space[7][0].getPiece().getMoveCounter() == 0 ||
                        space[7][7].getPiece().getMoveCounter() == 0 ) {
                    if ( space[X][Y].getPiece().getColour() &&
                            space[5][0].getPiece() == null &&
                            space[6][0].getPiece() == null)
                    {
                        legalMoves.add( space[6][0] );
                        space[6][0].armButton();
                    }
                    else if ( !space[X][Y].getPiece().getColour()  &&
                            space[5][7].getPiece() == null &&
                            space[6][7].getPiece() == null) {
                        legalMoves.add( space[6][7] );
                        space[6][7].armButton();
                    }
                }

            }



            if ( Y == 4 && space[X][Y].getPiece().getColour() && moveString.contains( "Pawn" ) ||
                    Y == 3 && ! space[X][Y].getPiece().getColour() && moveString.contains( "Pawn" ) ) {
                if ( indexExists( (X - 1) , (Y) ) &&
                        space[X - 1][Y].getPiece() != null &&
                        space[X - 1][Y].getPiece().getPieceName().equals( "Pawn" ) &&
                        space[X - 1][Y].getPiece().getMoveCounter() == 1 &&
                        moveString.contains( Character.toString( ( char ) (X - 1 + 97) ) ) ) {
                    if ( space[X][Y].getPiece().getColour() ) enpassantLeft = true;
                    else enpassantRight = true;
                    enpassant = space[X - 1][Y];

                }
                if ( indexExists( (X + 1) , (Y) ) &&
                        space[X + 1][Y].getPiece() != null &&
                        space[X + 1][Y].getPiece().getPieceName().equals( "Pawn" ) &&
                        space[X + 1][Y].getPiece().getMoveCounter() == 1 &&
                        moveString.contains( Character.toString( ( char ) (X + 1 + 97) ) ) ) {
                    if ( space[X][Y].getPiece().getColour() ) enpassantRight = true;
                    else enpassantLeft = true;
                    enpassant = space[X + 1][Y];
                }

            }

            for ( MoveList m : moves ) {
                for ( int i = 1;i <= space[X][Y].getPiece().getRange();i++ ) {


                    xVal = X + m.getX() * i;
                    yVal = Y + m.getY() * i;

                    if ( space[X][Y].getPiece().getPieceName().equals( "Pawn" ) ) {
                        if ( ! space[X][Y].getPiece().getColour() ) {
                            xVal = X + (- m.getX()) * i;
                            yVal = Y + (- m.getY()) * i;
                        }
                        if ( space[X][Y].getPiece().getMoveCounter() != 0 ) {
                            //allow jump two space only first move
                            if ( m == MoveList.DOUBLE_UP ) {
                                break;
                            }
                        }

                        if ( indexExists( (X + 1) , (Y + 1) ) && space[X + 1][Y + 1].getPiece() == null &&
                                space[X][Y].getPiece().getColour() ||
                                indexExists( (X - 1) , (Y - 1) ) && space[X - 1][Y - 1].getPiece() == null &&
                                        ! space[X][Y].getPiece().getColour() ) {
                            //allow diagonal capture
                            if ( m == MoveList.UP_RIGHT && ! enpassantRight ) {
                                break;
                            }
                        }
                        if ( indexExists( (X - 1) , (Y + 1) ) && space[X - 1][Y + 1].getPiece() == null &&
                                space[X][Y].getPiece().getColour() ||
                                indexExists( (X + 1) , (Y - 1) ) && space[X + 1][Y - 1].getPiece() == null &&
                                        ! space[X][Y].getPiece().getColour() ) {
                            //allow diagonal capture
                            if ( m == MoveList.UP_LEFT && ! enpassantLeft ) {
                                break;
                            }
                        }


                    }

                    if ( yVal >= 0 && yVal < 8 && xVal >= 0 && xVal < 8 )//if square exists on board
                    {

                        if ( space[xVal][yVal].getPiece() == null ||
                                space[xVal][yVal].getPiece().getColour() != space[X][Y].getPiece().getColour() ) {
                            legalMoves.add( ai , space[xVal][yVal] );
                            legalMoves.get( ai ).armButton();
                            ai++;
                            if ( space[xVal][yVal].getPiece() != null ) break; //stops black collision
                        } else {
                            //stops checking this move if pieces are the same color
                            break;
                        }

                    }

                }
            }
        }
    }

    private boolean indexExists(final int X , final int Y) {
        return (X >= 0 && X < 8) && (Y >= 0 && Y < 8);
    }


    public void setSize(double size) {
        this.setMinSize( size , size );
        this.setMaxSize( size , size );
        this.setPrefSize( size , size );
    }
}
