package controller
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import view.WrappedImageView


open class Square(private var light: Boolean , var x: Int , var y: Int) : Button() {
    private var piece: Piece? = null
    private var img: Image? = null
    private var imageView: ImageView? = null
    private var selected: Boolean = false


    init {
        styleClass.add("chess-space")
        setDefault()
    }



    fun setActive() {
        if(!this.selected) {
            styleClass.add("chess-space-active")
            this.selected = true
        } else {
            styleClass.remove("chess-space-active")
            this.selected = false
        }

    }


    fun setDefault(){
        if (light)
            styleClass.add("chess-space-light")
        else
            styleClass.add("chess-space-dark")

        if (selected) {
            this.selected = false
            styleClass.remove("chess-space-active")
        }
    }
    fun setPiece(piece: Piece) {
        this.piece = piece
        this.img = piece.getImage()
        this.imageView = WrappedImageView(img)
        this.graphic = imageView
    }
}

