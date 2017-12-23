package controller
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import view.WrappedImageView


open class Square(private var light: Boolean , private var x: Int , private var y: Int) : Button() {

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
        if (light)
            styleClass.add("chess-space-light")
        else
            styleClass.add("chess-space-dark")
    }


     fun removePiece() {
         if (this.piece != null) {
             this.img = null
             this.imageView = null
             this.graphic = null
             this.piece = null
         }
    }

    override fun arm() {
        styleClass.add("chess-space-active")
        super.arm()
    }

    override fun disarm() {
        this.isPressed = false
        styleClass.remove("chess-space-active")
        super.disarm()
    }

    override fun toString(): String {
        return "Square(light=$light, x=$x, y=$y, piece=$piece)"
    }
}

