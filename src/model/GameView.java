package model;

import javafx.beans.property.SimpleStringProperty;

public class GameView {
    private final SimpleStringProperty date;
    private final SimpleStringProperty white;
    private final SimpleStringProperty black;
    private final SimpleStringProperty result;
    private int id;

    public GameView(int id , String date , String white , String black , String result) {
        this.date = new SimpleStringProperty( date );
        this.white = new SimpleStringProperty( white );
        this.black = new SimpleStringProperty( black );
        this.result = new SimpleStringProperty( result );
        this.id = id;
    }

    public String getWhite() {
        return white.get();
    }

    public void setWhite(String white) {
        this.white.set( white );
    }

    public String getBlack() {
        return black.get();
    }

    public void setBlack(String white) {
        this.black.set( white );
    }

    public String getDate() {
        return date.get();
    }

    public void setDate(String date) {
        this.date.set( date );
    }

    public String getResult() {
        return result.get();
    }

    public void setResult(String result) {
        this.result.set( result );
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
