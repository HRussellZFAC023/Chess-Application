package controller

import javafx.scene.image.Image


abstract class Piece(private var colour: Boolean){
    internal val imageString: Image = Image("assets/pieces/" + this.colour.toString() + this.javaClass.simpleName + ".png")

    override fun toString(): String {
        var c = "white"
        if(!colour) c = "black"
        return "Piece(colour=$c, Type='${javaClass.name.substringAfterLast(".")}')"
    }
    fun getColour(): Boolean {
        return colour
    }

    //promise to implement these methods
    protected abstract fun getPieceMoves(): Array<MoveList>
    protected abstract fun getRange():Int


}






