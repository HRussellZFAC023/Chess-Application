package controller

import javafx.scene.control.Button
import javafx.scene.image.ImageView


data class Space(private var light: Boolean, var x: Int, var y: Int, private var piece: Piece) : Button() {

    init {
        styleClass.add("chess-space")
        if (light)
            this.styleClass.add("chess-space-light")
        else
            this.styleClass.add("chess-space-dark")
    }

    fun setPiece(piece: Piece) {
        //todo fix error here
        this!!.graphic = ImageView(this.piece.toString())
    }

}
