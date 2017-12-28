package controller
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import view.WrappedImageView

open class Square(val x: Int , val y: Int) : Button() {

    private var img: Image? = null
    private var imageView: ImageView? = null
    protected var piece: Piece? = null
        set(value) {
            field = value
            this.img = piece?.image
            this.imageView = WrappedImageView(img)
            this.graphic = imageView
        }


    init {
        styleClass.add("chess-space")
    }


     fun removePiece() {
         if (this.piece != null) {
             this.img = null
             this.imageView = null
             this.graphic = null
             this.piece = null
         }
    }

    fun armButton() {
        styleClass.add("chess-space-active")
    }

    fun disarmButton() {
        this.isPressed = false
        styleClass.remove("chess-space-active")
    }

    override fun toString(): String {
        return "Square( x=$x, y=$y, piece=$piece)"
    }


}

