package controller
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import view.WrappedImageView


data class Square(private var light: Boolean, var x: Int, var y: Int ) : Button() {
    private var piece: Piece? = null
    private var img: Image? = null
    private var imageView: ImageView? = null


    init {
        styleClass.add("chess-space")

        if (light)
            this.styleClass.add("chess-space-light")
        else
            this.styleClass.add("chess-space-dark")
    }

    fun setPiece(piece: Piece) {
       // print(piece.toString())
        this.piece = piece
        this.img = piece.getImage()
        this.imageView = WrappedImageView(img)
        this.graphic = imageView
    }

}

