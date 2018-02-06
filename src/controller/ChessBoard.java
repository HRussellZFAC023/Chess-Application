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
        setWhitesTurn( white );
        this.setController( controller );

        for ( int x = 1; x <= 8; x++ ) {
            for ( int y = 0; y < 8; y++ ) {
                light = ((x + y) % 2 == 0);

                final int xVal = (x - 1);

                getSpace()[xVal][y] = new Square( x , y );
                if ( light )
                    getSpace()[xVal][y].getStyleClass().add( "chess-space-light" );
                else
                    getSpace()[xVal][y].getStyleClass().add( "chess-space-dark" );

                if ( white ) {
                    this.add( getSpace()[xVal][y] , x , 7 - y );
                } else {
                    this.add( getSpace()[xVal][y] , x , y );
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
        getSpace()[x][y].setOnMousePressed( event -> onSpaceClick( x , y ) );
        getSpace()[x][y].setOnMouseReleased( event -> disarmLegalMoves() );

        getSpace()[x][y].setId( this.getClass().getSimpleName() + System.currentTimeMillis() );
        getSpace()[x][y].setOnDragDetected( event -> {
            Dragboard db = getSpace()[x][y].startDragAndDrop( TransferMode.MOVE );
            ClipboardContent content = new ClipboardContent();
            //visual
            if ( getSpace()[x][y].getPiece() != null ) {
                Image image = new Image( getSpace()[x][y].getPiece().getImgString() ,
                        getSpace()[x][y].getWidth() , getSpace()[x][y].getHeight() ,
                        true , true );
                db.setDragView( image , 50 , 50 );
            }
            // Store node ID in order to know what is dragged.
            content.putString( getSpace()[x][y].getId() );
            db.setContent( content );
            event.consume();
        } );

        getSpace()[x][y].setOnDragDropped( (DragEvent event) -> {
            disarmLegalMoves();
            onSpaceClick( x , y );
            event.setDropCompleted( true );
            event.consume();
        } );

        getSpace()[x][y].setOnDragOver( (DragEvent event) -> {
            if ( getLegalMoves().contains( getSpace()[x][y] ) ) {
                event.acceptTransferModes( TransferMode.MOVE );
            }
            event.consume();
        } );

        getSpace()[x][y].setOnDragDone( (DragEvent event) -> {
            disarmLegalMoves();
            event.consume();
        } );


    }

    private void disarmLegalMoves() {
        if ( getLastClickedSquare() != null ) {
            getLastClickedSquare().disarmButton();
        }
        for ( Square legalMove : getLegalMoves() ) {
            legalMove.disarmButton();
        }

    }


    public void defineStartPositions() {
        //set new pieces
        getSpace()[0][0].setPiece( new Rook( true ) );
        getSpace()[1][0].setPiece( new Knight( true ) );
        getSpace()[2][0].setPiece( new Bishop( true ) );
        getSpace()[3][0].setPiece( new Queen( true ) );
        getSpace()[4][0].setPiece( new King( true ) );
        getSpace()[5][0].setPiece( new Bishop( true ) );
        getSpace()[6][0].setPiece( new Knight( true ) );
        getSpace()[7][0].setPiece( new Rook( true ) );

        for ( int i = 0; i < getSpace()[0].length; i++ )
            getSpace()[i][1].setPiece( new Pawn( true ) );

        // black pieces
        getSpace()[0][7].setPiece( new Rook( false ) );
        getSpace()[1][7].setPiece( new Knight( false ) );
        getSpace()[2][7].setPiece( new Bishop( false ) );
        getSpace()[3][7].setPiece( new Queen( false ) );
        getSpace()[4][7].setPiece( new King( false ) );
        getSpace()[5][7].setPiece( new Bishop( false ) );
        getSpace()[6][7].setPiece( new Knight( false ) );
        getSpace()[7][7].setPiece( new Rook( false ) );

        for ( int i = 0; i < getSpace()[0].length; i++ )
            getSpace()[i][6].setPiece( new Pawn( false ) );

    }

    public void removeAllPieces() {
        //remove any old pieces
        for ( int i = 0; i < 8; i++ ) {
            for ( int j = 0; j < 8; j++ ) {
                getSpace()[i][j].removePiece();
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


        if ( getLastClickedSquare() != null &&
                getLastClickedSquare().getPiece() != null &&
                getLastClickedSquare() != getSpace()[X][Y] &&
                getLegalMoves().contains( getSpace()[X][Y] ) ) {


            if ( getSpace()[X][Y].getPiece() == null &&  //if the space is empty
                    getLegalMoves().contains( getSpace()[X][Y] ) || //AND legal move
                    getSpace()[X][Y].getPiece().getColour() != getLastClickedSquare().getPiece().getColour() &&//OR enemy piece
                            getLegalMoves().contains( getSpace()[X][Y] ) ) {//AND legal move

                if ( isWhitesTurn() == getLastClickedSquare().getPiece().getColour() ) {

                    setTurnCounter( getTurnCounter() + 1 );//increment turn counter
                    getLastClickedSquare().getPiece().setMoveCounter(
                            getLastClickedSquare().getPiece().getMoveCounter() + 1 );//increment move counter

                    if ( getSpace()[X][Y].getPiece() != null ) {
                        capture = true;
                        captureMoveString =
                                getLastClickedSquare().getPiece().getPieceName() +
                                        ( char ) (getLastClickedSquare().getX() + 96) +
                                        (getLastClickedSquare().getY() + 1);

                    }
                    if ( getEnpassant() != null ) {
                        getEnpassant().removePiece();
                        setEnpassant( null );
                        capture = true;
                        captureMoveString =
                                getLastClickedSquare().getPiece().getPieceName() +
                                        ( char ) (getLastClickedSquare().getX() + 96) +
                                        (getLastClickedSquare().getY() + 1);
                    }
                    getSpace()[X][Y].setPiece( getLastClickedSquare().getPiece() );


                    setMoveString( getSpace()[X][Y].getPiece().getPieceName()
                            + " (" + ( char ) (getLastClickedSquare().getX() + 96) + ") to "
                            + ( char ) (X + 97) + "" + (Y + 1) );

                    getLastClickedSquare().removePiece();

                    if ( getSpace()[X][Y].getPiece().getPieceName().equals( "Pawn" ) && (
                            (Y == 7 && getSpace()[X][Y].getPiece().getColour()) ||
                                    (Y == 0 && ! getSpace()[X][Y].getPiece().getColour())) ) {
                        getSpace()[X][Y].setPiece( choosePiece( getSpace()[X][Y].getPiece().getColour() ) );
                    }

                    if ( getSpace()[X][Y].getPiece().getPieceName().equals( "King" ) &&
                            getSpace()[X][Y].getPiece().getMoveCounter() == 1 ) {
                        if ( X == 6 ) {
                            getSpace()[X - 1][Y].setPiece( getSpace()[X + 1][Y].getPiece() );
                            getSpace()[X + 1][Y].removePiece();
                            castledKingside = true;
                        }
                        if ( X == 2 ) {
                            getSpace()[X + 1][Y].setPiece( getSpace()[X - 2][Y].getPiece() );
                            getSpace()[X - 2][Y].removePiece();
                            castledQueensise = true;
                        }
                    }


                    if ( getTurnCounter() % 2 == 0 ) {
                        System.out.println( "whites turn" );
                        setWhitesTurn( true );
                    } else {
                        System.out.println( "blacks turn" );
                        setWhitesTurn( false );
                    }
                    recordMove( capture , castledKingside , castledQueensise , getMoveString() , captureMoveString ,
                            getSpace()[X][Y] );
                }

            }
        }

        showAvailableMoves( X , Y );// if the space contains a piece
        setLastClickedSquare( getSpace()[X][Y] );//stores last piece click


    }

    private void recordMove(final boolean capture , final boolean castledKingside , final boolean castledQueenside , final String moveString , final String captureMoveString , Square currentSquare) {
        System.out.println( captureMoveString );
        System.out.println( moveString );
        String reformattedMove;

        if ( capture ) {
            reformattedMove = pgnLettering( captureMoveString , ambiguityCheck( currentSquare , true ) , true ) + "x" +
                    moveString.substring( moveString.length() - 2 );  //todo ambiguity check capture
        } else if ( castledKingside ) {
            reformattedMove = "O-O";
        } else if ( castledQueenside ) {
            reformattedMove = "O-O-O";
        } else reformattedMove = pgnLettering( moveString , ambiguityCheck( currentSquare , false ) , false );
        getController().updateTable( reformattedMove );
    }

    private boolean ambiguityCheck(Square currentSquare , boolean capture) {
        //(this is after the move has been made so it is checking for other pieces of same type which can also go to space)

        for ( int x = 0; x < 8; x++ ) {
            for ( int y = 0; y < 8; y++ ) {
                if ( getSpace()[x][y].getPiece() != null &&
                        getSpace()[x][y].getPiece().getPieceName().equals( currentSquare.getPiece().getPieceName() ) &&
                        getSpace()[x][y].getPiece().getColour() == currentSquare.getPiece().getColour() ) {
                    Piece piece = currentSquare.getPiece();
                    currentSquare
                            .removePiece(); //wont work because a piece is there !! therefore have to remove piece first
                    if ( capture ) currentSquare.setPiece( new Pawn( isWhitesTurn() ) );
                    showAvailableMoves( x , y );
                    currentSquare.removePiece();
                    currentSquare.setPiece( piece );
                    disarmLegalMoves();
                    getSpace()[x][y].disarmButton();
                    if ( getLegalMoves().contains( currentSquare ) ) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private String pgnLettering(final String moveString , final boolean ambiguityCheck , final boolean capture) {

       final String xyCoordinates =  moveString.substring( moveString.length() - 2 );
       String letterOfPiece = moveString.substring( 0 , 1 );
       String ambiguityLetter = "";
       if(!capture){/*else no parenthesis in string*/
            ambiguityLetter = moveString.substring( moveString.indexOf( "(" ) + 1 , moveString.indexOf( ")" ) );
       }

        //returns pgnLettering co-ordinates
        if ( ! letterOfPiece.equals( "P" ) && ! letterOfPiece.equals( "K" ) ) {
            return getPgnMove(ambiguityCheck,capture, letterOfPiece,ambiguityLetter,xyCoordinates);
        } else if ( letterOfPiece.equals( "K" ) && moveString.substring( 0 , 2 ).equals( "Kn" ) ) {
           letterOfPiece = "N";
            return getPgnMove(ambiguityCheck,capture, letterOfPiece,ambiguityLetter,xyCoordinates);
        } else if ( letterOfPiece.equals( "K" ) && letterOfPiece.equals( "K" ) ) {
            return getPgnMove(ambiguityCheck,capture, letterOfPiece,ambiguityLetter,xyCoordinates);
        } else if ( ! capture ) return xyCoordinates; //pawn move
        else if ( ! ambiguityCheck ) {
            return xyCoordinates;
        } else return xyCoordinates;
    }

    private String getPgnMove(boolean ambiguityCheck, boolean capture, String letterOfPiece, String ambiguityLetter, String xyCoordinates){
        if ( ambiguityCheck ) {
            if ( ! capture ) return letterOfPiece + ambiguityLetter + xyCoordinates;
            return letterOfPiece + xyCoordinates;
        } else if ( ! capture ) return letterOfPiece + xyCoordinates;
        else return letterOfPiece;

    }


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
        getLegalMoves().clear();
        getSpace()[X][Y].armButton();

        int ai = 0;
        int xVal;
        int yVal;
        boolean enpassantLeft = false;
        boolean enpassantRight = false;


        if ( getSpace()[X][Y].getPiece() != null ) {
            MoveList[] moves = getSpace()[X][Y].getPiece().getPieceMoves();

            if ( getSpace()[X][Y].getPiece().getPieceName().equals( "King" ) &&
                    getSpace()[X][Y].getPiece().getMoveCounter() == 0 ) {
                //Queen-side castling
                if ( getMoveString() != null && (
                        getSpace()[0][0].getPiece() != null &&
                        getSpace()[0][0].getPiece().getMoveCounter() == 0 ||
                                getSpace()[0][7].getPiece().getMoveCounter() == 0) ) {
                    if ( getSpace()[X][Y].getPiece().getColour() &&
                            getSpace()[1][0].getPiece() == null &&
                            getSpace()[2][0].getPiece() == null &&
                            getSpace()[3][0].getPiece() == null ) {
                        getLegalMoves().add( getSpace()[2][0] );
                        getSpace()[2][0].armButton();
                    } else if ( ! getSpace()[X][Y].getPiece().getColour() &&
                            getSpace()[1][7].getPiece() == null &&
                            getSpace()[2][7].getPiece() == null &&
                            getSpace()[3][7].getPiece() == null ) {
                        getLegalMoves().add( getSpace()[2][7] );
                        getSpace()[2][7].armButton();
                    }
                }

                //king-side castling
                if ( getMoveString() != null && (
                        getSpace()[7][0].getPiece() != null &&
                                getSpace()[7][7] != null &&
                        getSpace()[7][0].getPiece().getMoveCounter() == 0 ||
                                getSpace()[7][7].getPiece().getMoveCounter() == 0) ) {
                    if ( getSpace()[X][Y].getPiece().getColour() &&
                            getSpace()[5][0].getPiece() == null &&
                            getSpace()[6][0].getPiece() == null ) {
                        getLegalMoves().add( getSpace()[6][0] );
                        getSpace()[6][0].armButton();
                    } else if ( ! getSpace()[X][Y].getPiece().getColour() &&
                            getSpace()[5][7].getPiece() == null &&
                            getSpace()[6][7].getPiece() == null ) {
                        getLegalMoves().add( getSpace()[6][7] );
                        getSpace()[6][7].armButton();
                    }
                }

            }

            if ( getMoveString() != null && Y == 4 && getSpace()[X][Y].getPiece().getColour() && getMoveString().contains( "Pawn" ) ||
                    getMoveString() != null && Y == 3 && ! getSpace()[X][Y].getPiece().getColour() &&
                            getMoveString().contains( "Pawn" ) ) {
                if ( indexExists( (X - 1) , (Y) ) &&
                        getSpace()[X - 1][Y].getPiece() != null &&
                        getSpace()[X - 1][Y].getPiece().getPieceName().equals( "Pawn" ) &&
                        getSpace()[X - 1][Y].getPiece().getMoveCounter() == 1 &&
                        getMoveString().contains( Character.toString( ( char ) (X - 1 + 97) ) ) ) {
                    if ( getSpace()[X][Y].getPiece().getColour() ) enpassantLeft = true;
                    else enpassantRight = true;
                    setEnpassant( getSpace()[X - 1][Y] );

                }
                if ( indexExists( (X + 1) , (Y) ) &&
                        getSpace()[X + 1][Y].getPiece() != null &&
                        getSpace()[X + 1][Y].getPiece().getPieceName().equals( "Pawn" ) &&
                        getSpace()[X + 1][Y].getPiece().getMoveCounter() == 1 &&
                        getMoveString().contains( Character.toString( ( char ) (X + 1 + 97) ) ) ) {
                    if ( getSpace()[X][Y].getPiece().getColour() ) enpassantRight = true;
                    else enpassantLeft = true;
                    setEnpassant( getSpace()[X + 1][Y] );
                }

            }

            for ( MoveList m : moves ) {
                for ( int i = 1; i <= getSpace()[X][Y].getPiece().getRange(); i++ ) {


                    xVal = X + m.getX() * i;
                    yVal = Y + m.getY() * i;

                    if ( getSpace()[X][Y].getPiece().getPieceName().equals( "Pawn" ) ) {
                        if ( ! getSpace()[X][Y].getPiece().getColour() ) {
                            xVal = X + (- m.getX()) * i;
                            yVal = Y + (- m.getY()) * i;
                        }
                        if ( getSpace()[X][Y].getPiece().getMoveCounter() != 0 ) {
                            //allow jump two space only first move
                            if ( m == MoveList.DOUBLE_UP ) {
                                break;
                            }
                        }

                        if ( indexExists( (X + 1) , (Y + 1) ) && getSpace()[X + 1][Y + 1].getPiece() == null &&
                                getSpace()[X][Y].getPiece().getColour() ||
                                indexExists( (X - 1) , (Y - 1) ) && getSpace()[X - 1][Y - 1].getPiece() == null &&
                                        ! getSpace()[X][Y].getPiece().getColour() ) {
                            //allow diagonal capture
                            if ( m == MoveList.UP_RIGHT && ! enpassantRight ) {
                                break;
                            }
                        }
                        if ( indexExists( (X - 1) , (Y + 1) ) && getSpace()[X - 1][Y + 1].getPiece() == null &&
                                getSpace()[X][Y].getPiece().getColour() ||
                                indexExists( (X + 1) , (Y - 1) ) && getSpace()[X + 1][Y - 1].getPiece() == null &&
                                        ! getSpace()[X][Y].getPiece().getColour() ) {
                            //allow diagonal capture
                            if ( m == MoveList.UP_LEFT && ! enpassantLeft ) {
                                break;
                            }
                        }


                    }

                    if (indexExists( xVal, yVal ) )//if square exists on board
                    {

                        if ( getSpace()[xVal][yVal].getPiece() == null ||
                                getSpace()[xVal][yVal].getPiece().getColour() != getSpace()[X][Y].getPiece().getColour() &&
                                        ! getSpace()[xVal][yVal].getPiece().getPieceName()
                                                .equals( "King" ) ) { //stops enemy taking king
                            if ( getSpace()[X][Y].getPiece().getPieceName().equals( "Pawn" ) &&
                                    (m == MoveList.UP || m == MoveList.DOUBLE_UP) &&
                                    getSpace()[xVal][yVal].getPiece() != null ) break;
                            getLegalMoves().add( ai , getSpace()[xVal][yVal] );
                            getLegalMoves().get( ai ).armButton();
                            ai++;
                            if ( getSpace()[xVal][yVal].getPiece() != null ) break; //stops collision after one capture
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
        this.setMoveString( null );
        if ( getTurnCounter() > 0 ) {
            setTurnCounter( getTurnCounter() - 1 );
            setWhitesTurn( (! isWhitesTurn()) );
        }
        removeAllPieces();
        defineStartPositions();
        MovesService.deleteByMoveId( getController().getMoveId() , getController().getGameDatabase() );
        getController().updateTable();
        //controller.allMoves.remove(controller.allMoves.size()-1);
        for ( MoveView m : getController().getAllMoves() ) {
            makeMove( m.getWhite() , true );
            if ( ! m.getBlack().equals( "" ) )
                makeMove( m.getBlack() , false ); //because table stores "" for unmade black moves
        }
    }

    public void load(){
        for ( MoveView m : getController().getAllMoves() ) {
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
                getSpace()[6][i].setPiece( getSpace()[4][i].getPiece() );
                getSpace()[5][i].setPiece( getSpace()[7][i].getPiece() );
                getSpace()[7][i].removePiece();
                getSpace()[4][i].removePiece();

                break;
            }
            case "O-O-O": {
                int i = 7;
                if ( colour ) i = 0;
                getSpace()[2][i].setPiece( getSpace()[4][i].getPiece() );
                getSpace()[3][i].setPiece( getSpace()[0][i].getPiece() );
                getSpace()[0][i].removePiece();
                getSpace()[4][i].removePiece();
                break;
            }
            default:

                Piece piece;
                moveString = moveString.replace( "+" , "" );
                Square destination = getSpace()[moveString.charAt( moveString.length() - 2 ) - 97]
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
            if ( getSpace()[x][y].getPiece() != null &&
                    getSpace()[x][y].getPiece().getPieceName().equals( piece.getPieceName() ) &&
                    getSpace()[x][y].getPiece().getColour() == piece.getColour() ) {
                showAvailableMoves( x , y );
                disarmLegalMoves();
                getSpace()[x][y].disarmButton();

                if ( getLegalMoves().contains( destination ) ) {
                    getSpace()[x][y].getPiece().setMoveCounter(
                            getSpace()[x][y].getPiece().getMoveCounter() + 1 ); //increment move counter of piece
                    destination.setPiece( getSpace()[x][y].getPiece() );
                    getSpace()[x][y].removePiece();
                }


            }
        }
    }

    public DataController getController() {
        return controller;
    }

    public void setController(DataController controller) {
        this.controller = controller;
    }

    private Square[][] getSpace() {
        return space;
    }

    private List<Square> getLegalMoves() {
        return legalMoves;
    }

    public void setLegalMoves(List<Square> legalMoves) {
        this.legalMoves = legalMoves;
    }

    private Square getLastClickedSquare() {
        return lastClickedSquare;
    }

    private void setLastClickedSquare(Square lastClickedSquare) {
        this.lastClickedSquare = lastClickedSquare;
    }

    private Square getEnpassant() {
        return enpassant;
    }

    private void setEnpassant(Square enpassant) {
        this.enpassant = enpassant;
    }

    private int getTurnCounter() {
        return turnCounter;
    }

    private void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    private boolean isWhitesTurn() {
        return whitesTurn;
    }

    private void setWhitesTurn(boolean whitesTurn) {
        this.whitesTurn = whitesTurn;
    }

    private String getMoveString() {
        return moveString;
    }

    private void setMoveString(String moveString) {
        this.moveString = moveString;
    }
}

