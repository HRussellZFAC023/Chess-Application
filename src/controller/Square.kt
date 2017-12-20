package controller
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import view.WrappedImageView


open class Square(private var light: Boolean , var x: Int , var y: Int) : Button() {

    private var img: Image? = null
    private var imageView: ImageView? = null
    private var selected: Boolean = false
    var piece: Piece? = null
        set(value) {
            field = value
            this.img = piece!!.imageString
            this.imageView = WrappedImageView(img)
            this.graphic = imageView
        }


    init {
        styleClass.add("chess-space")
        setDefault()
    }

    fun setActive() {
        //if square is not red, make it red. If red make it normal

        if(!this.selected) {
            this.selected = true
            styleClass.add("chess-space-active")
        } else {
            setDefault()
        }
        if (this.piece != null){
            println(piece.toString())
            //todo when a piece is clicked, save its current position
            //todo if last space pressed was a piece (current position != null) then move it to space clicked
            //removePiece(this.piece)
        }

    }

    private fun removePiece(piece: Piece?) {
        this.img = null
        this.imageView = null
        this.graphic = null
        this.piece = null
    }

    fun setDefault(){

        if(this.selected) {
            this.selected = false
            styleClass.remove("chess-space-active")
        }

        if (light)
            styleClass.add("chess-space-light")
        else
            styleClass.add("chess-space-dark")
    }
}

