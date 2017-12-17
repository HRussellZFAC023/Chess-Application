package model;

import javafx.beans.property.SimpleStringProperty;

public class MoveView {
    private final SimpleStringProperty white;
    private final SimpleStringProperty black;

    public MoveView(String white, String black) {
        this.white = new SimpleStringProperty(white);
        this.black = new SimpleStringProperty(black);
    }

    public String getWhite() {
        return white.get();
    }

    public void setWhite(String white) {
        this.white.set(white);
    }

    public String getBlack() {
        return black.get();
    }

    public void setBlack(String white) {
        this.black.set(white);
    }
}