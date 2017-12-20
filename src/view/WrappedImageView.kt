package view

import javafx.scene.image.Image
import javafx.scene.image.ImageView




class WrappedImageView(i: Image?) : ImageView() {
    private var i: Image? = null

    init {
        this.image = i
        isPreserveRatio = true

    }

    override fun minWidth(height: Double): Double {
        return 50.0
    }

    override fun prefWidth(height: Double): Double {
         i = image ?: return minWidth(height)
        return i!!.width
    }

    override fun maxWidth(height: Double): Double {
        return 10000.0
    }

    override fun minHeight(width: Double): Double {
        return 50.0
    }

    override fun prefHeight(width: Double): Double {
        i = image ?: return minHeight(width)
        return i!!.height
    }

    override fun maxHeight(width: Double): Double {
        return 10000.0
    }

    override fun isResizable(): Boolean {
        return true
    }

    override fun resize(width: Double, height: Double) {
        fitWidth = width
        fitHeight = width

    }

}
