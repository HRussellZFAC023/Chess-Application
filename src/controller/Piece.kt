package controller

import javafx.scene.image.Image


abstract class Piece(private var colour: Boolean){
    protected val imgString: String = ("assets/pieces/" + this.colour.toString() + this.javaClass.simpleName + ".png")
    protected var moveCounter: Int = 0
    internal val image: Image = Image(imgString)


    override fun toString(): String {
        var c = "white"
        if(!colour) c = "black"
        return "Piece(colour=$c, Type='${javaClass.name.substringAfterLast(".")}')"
    }
    fun getColour(): Boolean {
        return colour
    }
    fun getPieceName(): String {
        return javaClass.name.substringAfterLast(".")
    }



    //promise to implement these methods
    protected abstract fun getPieceMoves(): Array<MoveList>
    protected abstract fun getRange():Int


}






