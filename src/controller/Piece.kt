package controller

import javafx.scene.image.Image


abstract class Piece(private var colour: Boolean){
    internal val imageString: Image = Image("assets/pieces/" + this.colour.toString() + this.javaClass.simpleName + ".png")


    override fun toString(): String {
        var c = "white"
        if(!colour) c = "black"
        return "Piece(colour=$c, Type='${javaClass.name.substringAfterLast(".")}')"
    }

     abstract fun getPieceMoves(): Array<MoveList> //promise to implement this method

    fun getColour(): Boolean {
            return colour
    }


}






