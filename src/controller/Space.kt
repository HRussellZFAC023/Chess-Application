package controller

import javafx.scene.control.Button
import javafx.scene.image.ImageView


data class Space(private var light: Boolean, var x: Int, var y: Int ) : Button() {
    private var piece: Piece? = null

    init {
        styleClass.add("chess-space")
        if (light)
            this.styleClass.add("chess-space-light")
        else
            this.styleClass.add("chess-space-dark")
    }

    fun setPiece(piece: Piece) {
        print(piece.toString())
        this.piece = piece
        this.graphic = ImageView(piece.getImageString())
    }

}
