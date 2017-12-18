package controller

import javafx.scene.image.Image

abstract class Piece(private var colour: Boolean){
    private var image: Image? = null
        get() = Image("assets/pieces/" + this.colour.toString() + this.javaClass.simpleName + ".png")
}