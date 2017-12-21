package controller
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import view.WrappedImageView


open class Square(private var light: Boolean , var x: Int , var y: Int) : Button() {

    private var img: Image? = null
    private var imageView: ImageView? = null
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

    }

    private fun removePiece(piece: Piece?) {
        this.img = null
        this.imageView = null
        this.graphic = null
        this.piece = null
    }

    private fun setDefault(){
        if (light)
            styleClass.add("chess-space-light")
        else
            styleClass.add("chess-space-dark")
    }

    override fun arm() {
        styleClass.add("chess-space-active")
        super.arm()
    }

    override fun disarm() {
        styleClass.remove("chess-space-active")
        super.disarm()
    }
}

