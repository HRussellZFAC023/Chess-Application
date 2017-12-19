package controller

import javafx.scene.image.Image

abstract class Piece(private var colour: Boolean){
    private val imageString: Image = Image("assets/pieces/" + this.colour.toString() + this.javaClass.simpleName + ".png")



    override fun toString(): String {
        var c = "white"
        if(!colour) c = "black"
        return "Piece(colour=$c, imageString='$imageString')"
    }

    fun getImageString(): Image? {
        return imageString
    }


}

