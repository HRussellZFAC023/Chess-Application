package controller;

import controller.pieces.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import model.MoveView;
import model.game.MovesService;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ChessBoard extends GridPane {
    //Attributes describing chessboards state
    private DataController controller;
    private final Square[][] space = new Square[8][8];
    private List<Square> legalMoves = new ArrayList<>();
    private Square lastClickedSquare = null;
    private Square enpassant = null;    //stores the position of enpassant
    private int turnCounter = 0;
    private boolean whitesTurn;
    private String moveString;

    public ChessBoard(boolean white , DataController controller) {
        boolean light;
        char ascii = 65;
        whitesTurn = white;
        this.controller = controller;

        for ( int x = 1; x <= 8; x++ ) {
            for ( int y = 0; y < 8; y++ ) {
                light = ((x + y) % 2 == 0);

                final int xVal = (x - 1);

                space[xVal][y] = new Square( x , y );
                if ( light )
                    space[xVal][y].getStyleClass().add( "chess-space-light" );
                else
                    space[xVal][y].getStyleClass().add( "chess-space-dark" );

                if ( white ) {
                    this.add( space[xVal][y] , x , 7 - y );
                } else {
                    this.add( space[xVal][y] , x , y );
                }
                setActionEvents( xVal , y );
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


    public void defineStartPositions() {
        //set new pieces
        space[0][0].setPiece( new Rook( true ) );
        space[1][0].setPiece( new Knight( true ) );
        space[2][0].setPiece( new Bishop( true ) );
        space[3][0].setPiece( new Queen( true ) );
        space[4][0].setPiece( new King( true ) );
        space[5][0].setPiece( new Bishop( true ) );
        space[6][0].setPiece( new Knight( true ) );
        space[7][0].setPiece( new Rook( true ) );

        for ( int i = 0; i < space[0].length; i++ )
            space[i][1].setPiece( new Pawn( true ) );

        // black pieces
        space[0][7].setPiece( new Rook( false ) );
        space[1][7].setPiece( new Knight( false ) );
        space[2][7].setPiece( new Bishop( false ) );
        space[3][7].setPiece( new Queen( false ) );
        space[4][7].setPiece( new King( false ) );
        space[5][7].setPiece( new Bishop( false ) );
        space[6][7].setPiece( new Knight( false ) );
        space[7][7].setPiece( new Rook( false ) );

        for ( int i = 0; i < space[0].length; i++ )
            space[i][6].setPiece( new Pawn( false ) );

    }

    public void removeAllPieces() {
        //remove any old pieces
        for ( int i = 0; i < 8; i++ ) {
            for ( int j = 0; j < 8; j++ ) {
                space[i][j].removePiece();
            }
        }
    }

    private void onSpaceClick(final int X , final int Y) {
        //System.out.println( ( char ) (X + 97) + "" + (Y + 1) );
        //System.out.println( "the X value is " + X + "\nthe Y value is " + Y );
        //because null this section is skipped until after showAvailableMoves()

        boolean capture = false;
        boolean castledKingside = false;
        boolean castledQueensise = false;
        String captureMoveString = "";


        if ( lastClickedSquare != null &&
                lastClickedSquare.getPiece() != null &&
                lastClickedSquare != space[X][Y] &&
                legalMoves.contains( space[X][Y] ) ) {


            if ( space[X][Y].getPiece() == null &&  //if the space is empty
                    legalMoves.contains( space[X][Y] ) || //AND legal move
                    space[X][Y].getPiece().getColour() != lastClickedSquare.getPiece().getColour() &&//OR enemy piece
                            legalMoves.contains( space[X][Y] ) ) {//AND legal move

                if ( whitesTurn == lastClickedSquare.getPiece().getColour() ) {

                    turnCounter++;//increment turn counter
                    lastClickedSquare.getPiece().setMoveCounter(
                            lastClickedSquare.getPiece().getMoveCounter() + 1 );//increment move counter

                    if ( space[X][Y].getPiece() != null ) {
                        capture = true;
                        captureMoveString =
                                lastClickedSquare.getPiece().getPieceName() +
                                        ( char ) (lastClickedSquare.getX() + 96) +
                                        (lastClickedSquare.getY() + 1);

                    }
                    if ( enpassant != null ) {
                        enpassant.removePiece();
                        enpassant = null;
                        capture = true;
                        captureMoveString =
                                lastClickedSquare.getPiece().getPieceName() +
                                        ( char ) (lastClickedSquare.getX() + 96) +
                                        (lastClickedSquare.getY() + 1);
                    }
                    space[X][Y].setPiece( lastClickedSquare.getPiece() );


                    moveString = space[X][Y].getPiece().getPieceName()
                            + " (" + ( char ) (lastClickedSquare.getX() + 96) + ") to "
                            + ( char ) (X + 97) + "" + (Y + 1);

                    lastClickedSquare.removePiece();

                    if ( space[X][Y].getPiece().getPieceName().equals( "Pawn" ) && (
                            (Y == 7 && space[X][Y].getPiece().getColour()) ||
                                    (Y == 0 && ! space[X][Y].getPiece().getColour())) ) {
                        space[X][Y].setPiece( choosePiece( space[X][Y].getPiece().getColour() ) );
                    }

                    if ( space[X][Y].getPiece().getPieceName().equals( "King" ) &&
                            space[X][Y].getPiece().getMoveCounter() == 1 ) {
                        if ( X == 6 ) {
                            space[X - 1][Y].setPiece( space[X + 1][Y].getPiece() );
                            space[X + 1][Y].removePiece();
                            castledKingside = true;
                        }
                        if ( X == 2 ) {
                            space[X + 1][Y].setPiece( space[X - 2][Y].getPiece() );
                            space[X - 2][Y].removePiece();
                            castledQueensise = true;
                        }
                    }


                    if ( turnCounter % 2 == 0 ) {
                        System.out.println( "whites turn" );
                        whitesTurn = true;
                    } else {
                        System.out.println( "blacks turn" );
                        whitesTurn = false;
                    }
                    recordMove( capture , castledKingside , castledQueensise , moveString , captureMoveString ,
                            space[X][Y] );
                }

            }
        }

        showAvailableMoves( X , Y );// if the space contains a piece
        lastClickedSquare = space[X][Y];//stores last piece click


    }

    private void recordMove(final boolean capture , final boolean castledKingside , final boolean castledQueenside , final String moveString , final String captureMoveString , Square currentSquare) {
        System.out.println( captureMoveString );
        System.out.println( moveString );
        String reformattedMove;

        if ( capture ) {
            reformattedMove = letter( captureMoveString , ambiguityCheck( currentSquare , true ) , true ) + "x" +
                    moveString.substring( moveString.length() - 2 ); //todo ambiguity check capture
        } else if ( castledKingside ) {
            reformattedMove = "O-O";
        } else if ( castledQueenside ) {
            reformattedMove = "O-O-O";
        } else reformattedMove = letter( moveString , ambiguityCheck( currentSquare , false ) , false );
        controller.updateTable( reformattedMove );
    }

    private boolean ambiguityCheck(Square currentSquare , boolean capture) {
        //(this is after the move has been made so it is checking for other pieces of same type which can also go to space)

        for ( int x = 0; x < 8; x++ ) {
            for ( int y = 0; y < 8; y++ ) {
                if ( space[x][y].getPiece() != null &&
                        space[x][y].getPiece().getPieceName().equals( currentSquare.getPiece().getPieceName() ) &&
                        space[x][y].getPiece().getColour() == currentSquare.getPiece().getColour() ) {
                    Piece piece = currentSquare.getPiece();
                    currentSquare
                            .removePiece(); //wont work because a piece is there !! therefore have to remove piece first
                    if ( capture ) currentSquare.setPiece( new Pawn( whitesTurn ) );
                    showAvailableMoves( x , y );
                    currentSquare.removePiece();
                    currentSquare.setPiece( piece );
                    disarmLegalMoves();
                    space[x][y].disarmButton();
                    if ( legalMoves.contains( currentSquare ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @NotNull
    private String letter(final String moveString , final boolean ambiguityCheck , final boolean capture) {
        String x = moveString.substring( moveString.indexOf( "(" ) + 1 , moveString.indexOf( ")" ) );
        String y = moveString.substring( moveString.length() - 2 );
        String z = moveString.substring( moveString.length() - 2 , moveString.length() - 1 );

        //returns letter + co-ordinates
        if ( ! moveString.substring( 0 , 1 ).equals( "P" ) && ! moveString.substring( 0 , 1 ).equals( "K" ) ) {
            if ( ambiguityCheck ) {
                if ( ! capture ) return moveString.substring( 0 , 1 ) + x + y;
                return moveString.substring( 0 , 1 ) +
                        z;
            } else if ( ! capture )
                return moveString.substring( 0 , 1 ) + "" + y;
            else return moveString.substring( 0 , 1 );
        } else if ( moveString.substring( 0 , 1 ).equals( "K" ) && moveString.substring( 0 , 2 ).equals( "Kn" ) ) {
            if ( ambiguityCheck ) {
                if ( ! capture )
                    return "N" + x + y;
                return "N" + z;
            } else if ( ! capture ) return "N" + "" + y;
            else return "N";
        } else if ( moveString.substring( 0 , 1 ).equals( "K" ) && moveString.substring( 0 , 2 ).equals( "Ki" ) ) {
            if ( ambiguityCheck ) {
                if ( ! capture )
                    return "K" + x + y;
                return "K" + z;
            } else if ( ! capture ) return "K" + "" + y;
            else return "K";
        } else if ( ! capture ) return y;
        else if ( ! ambiguityCheck ) {
            return z;
        } else {
            return y;
        }
    }
    


    @NotNull
    private Piece choosePiece(final boolean colour) {
        ButtonType option1 = new ButtonType( "♕" , ButtonBar.ButtonData.OTHER );
        ButtonType option2 = new ButtonType( "♖" , ButtonBar.ButtonData.OTHER );
        ButtonType option3 = new ButtonType( "♗" , ButtonBar.ButtonData.OTHER );
        ButtonType option4 = new ButtonType( "♘" , ButtonBar.ButtonData.OTHER );

        Alert alert = new Alert( Alert.AlertType.NONE , "Promotion:" , option1 , option2 , option3 , option4 );
        alert.showAndWait();
        if ( alert.getResult() == option2 ) return new Rook( colour );
        else if ( alert.getResult() == option3 ) return new Bishop( colour );
        else if ( alert.getResult() == option4 ) return new Knight( colour );
        else return new Queen( colour );
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
                    space[X][Y].getPiece().getMoveCounter() == 0 ) {
                //Queen-side castling
                if ( moveString != null && (
                        space[0][0].getPiece() != null &&
                        space[0][0].getPiece().getMoveCounter() == 0 ||
                                space[0][7].getPiece().getMoveCounter() == 0) ) {
                    if ( space[X][Y].getPiece().getColour() &&
                            space[1][0].getPiece() == null &&
                            space[2][0].getPiece() == null &&
                            space[3][0].getPiece() == null ) {
                        legalMoves.add( space[2][0] );
                        space[2][0].armButton();
                    } else if ( ! space[X][Y].getPiece().getColour() &&
                            space[1][7].getPiece() == null &&
                            space[2][7].getPiece() == null &&
                            space[3][7].getPiece() == null ) {
                        legalMoves.add( space[2][7] );
                        space[2][7].armButton();
                    }
                }

                //king-side castling
                if ( moveString != null && (
                        space[7][0].getPiece() != null &&
                                space[7][7] != null &&
                        space[7][0].getPiece().getMoveCounter() == 0 ||
                                space[7][7].getPiece().getMoveCounter() == 0) ) {
                    if ( space[X][Y].getPiece().getColour() &&
                            space[5][0].getPiece() == null &&
                            space[6][0].getPiece() == null ) {
                        legalMoves.add( space[6][0] );
                        space[6][0].armButton();
                    } else if ( ! space[X][Y].getPiece().getColour() &&
                            space[5][7].getPiece() == null &&
                            space[6][7].getPiece() == null ) {
                        legalMoves.add( space[6][7] );
                        space[6][7].armButton();
                    }
                }

            }

            if ( moveString != null && Y == 4 && space[X][Y].getPiece().getColour() && moveString.contains( "Pawn" ) ||
                    moveString != null && Y == 3 && ! space[X][Y].getPiece().getColour() &&
                            moveString.contains( "Pawn" ) ) {
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
                for ( int i = 1; i <= space[X][Y].getPiece().getRange(); i++ ) {


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
                                space[xVal][yVal].getPiece().getColour() != space[X][Y].getPiece().getColour() &&
                                        ! space[xVal][yVal].getPiece().getPieceName()
                                                .equals( "King" ) ) { //stops enemy taking king
                            if ( space[X][Y].getPiece().getPieceName().equals( "Pawn" ) &&
                                    (m == MoveList.UP || m == MoveList.DOUBLE_UP) &&
                                    space[xVal][yVal].getPiece() != null ) break;
                            legalMoves.add( ai , space[xVal][yVal] );
                            legalMoves.get( ai ).armButton();
                            ai++;
                            if ( space[xVal][yVal].getPiece() != null ) break; //stops collision after one capture
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


    public void setSize(final double size) {
        this.setMinSize( size , size );
        this.setMaxSize( size , size );
        this.setPrefSize( size , size );
    }

    public void undo() {
        this.moveString = null;
        if ( turnCounter > 0 ) {
            turnCounter--;
            whitesTurn = (! whitesTurn);
        }
        removeAllPieces();
        defineStartPositions();
        MovesService.deleteByMoveId( controller.getMoveId() , controller.gameDatabase );
        controller.updateTable();
        //controller.allMoves.remove(controller.allMoves.size()-1);
        for ( MoveView m : controller.allMoves ) {
            makeMove( m.getWhite() , true );
            if ( ! m.getBlack().equals( "" ) )
                makeMove( m.getBlack() , false ); //because table stores "" for unmade black moves
        }
    }

    public void load(){
        for ( MoveView m : controller.allMoves ) {
            makeMove( m.getWhite() , true );
            if ( ! m.getBlack().equals( "" ) )
                makeMove( m.getBlack() , false ); //because table stores "" for unmade black moves
        }
    }

    private void makeMove(String moveString , boolean colour) {

        switch (moveString) {
            case "O-O": {
                int i = 7;
                if ( colour ) i = 0;
                space[6][i].setPiece( space[4][i].getPiece() );
                space[5][i].setPiece( space[7][i].getPiece() );
                space[7][i].removePiece();
                space[4][i].removePiece();

                break;
            }
            case "O-O-O": {
                int i = 7;
                if ( colour ) i = 0;
                space[2][i].setPiece( space[4][i].getPiece() );
                space[3][i].setPiece( space[0][i].getPiece() );
                space[0][i].removePiece();
                space[4][i].removePiece();
                break;
            }
            default:

                Piece piece;
                moveString = moveString.replace( "+" , "" );
                Square destination = space[moveString.charAt( moveString.length() - 2 ) - 97]
                        [Character.getNumericValue( moveString.charAt( moveString.length() - 1 ) ) - 1];

                switch (moveString.charAt( 0 )) {
                    case 'K':
                        piece = new King( colour );
                        break;
                    case 'Q':
                        piece = new Queen( colour );
                        break;
                    case 'R':
                        piece = new Rook( colour );
                        break;
                    case 'B':
                        piece = new Bishop( colour );
                        break;
                    case 'N':
                        piece = new Knight( colour );
                        break;
                    default:
                        piece = new Pawn( colour );
                        break;
                }

                Matcher m = Pattern.compile( ".*[a-h]{2}.*|.[a-z]{3}.*|[a-h]\\d[a-z]{2}\\d|[A-Z][a-z]\\d..\\d" )
                        .matcher( moveString );
                if ( m.matches() ) { //checking for ambiguous move
                    //get first lower case
                    String lowerCase = moveString.replaceAll( "[A-Z]|\\d" , "" );
                    int x = (lowerCase.charAt( 0 ) - 97);
                    moveLoop( x , destination , piece );
                } else {
                    for ( int x = 0; x < 8; x++ ) {
                        moveLoop( x , destination , piece );
                    }
                }

                break;
        }
    }

    private void moveLoop(int x , Square destination , Piece piece) {
        for ( int y = 0; y < 8; y++ ) {
            if ( space[x][y].getPiece() != null &&
                    space[x][y].getPiece().getPieceName().equals( piece.getPieceName() ) &&
                    space[x][y].getPiece().getColour() == piece.getColour() ) {
                showAvailableMoves( x , y );
                disarmLegalMoves();
                space[x][y].disarmButton();

                if ( legalMoves.contains( destination ) ) {
                    space[x][y].getPiece().setMoveCounter(
                            space[x][y].getPiece().getMoveCounter() + 1 ); //increment move counter of piece
                    destination.setPiece( space[x][y].getPiece() );
                    space[x][y].removePiece();
                }


            }
        }
    }

}

