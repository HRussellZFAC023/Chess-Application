import javafx.scene.control.Button
import javafx.scene.image.ImageView


class Space(light: Boolean, var x: Int, var y: Int) : Button() {

    init {
        //this.piece = null
        styleClass.add("chess-space")

        if (light)
            this.styleClass.add("chess-space-light")
        else
            this.styleClass.add("chess-space-dark")
    }



}
