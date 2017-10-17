import javafx.beans.property.SimpleStringProperty;

public class Moves {
    private final SimpleStringProperty white;
    private final SimpleStringProperty black;

    public Moves(String white, String black) {
        this.white = new SimpleStringProperty(white);
        this.black = new SimpleStringProperty(black);
    }

    public String getWhite() { return white.get(); }
    public void setWhite(String white) { this.white.set(white); }

    public String getBlack() { return black.get(); }
    public void setBlack(String white ) { this.black.set(white); }


}