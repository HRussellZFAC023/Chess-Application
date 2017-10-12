import javafx.beans.property.SimpleStringProperty;

public class Moves {
    private final SimpleStringProperty white;
    private final SimpleStringProperty black;

    public Moves(String white, String black) {
        this.white = new SimpleStringProperty(white);
        this.black = new SimpleStringProperty(black);
    }

    public String getWhitesMove() { return white.get(); }
    public void setWhitesMove(String firstName) { this.white.set(firstName); }

    public String getBlacksMove() { return black.get(); }
    public void setBlacksMove(String lastName) { this.black.set(lastName); }

  
}