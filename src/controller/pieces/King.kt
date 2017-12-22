package controller.pieces

import controller.MoveList
import controller.Piece

class King(colour: Boolean) : Piece(colour) {
    override fun getPieceMoves(): Array<MoveList> {
        return arrayOf(
                MoveList.UP ,
                MoveList.UP_RIGHT ,
                MoveList.RIGHT ,
                MoveList.DOWN_RIGHT ,
                MoveList.DOWN ,
                MoveList.DOWN_LEFT ,
                MoveList.LEFT ,
                MoveList.UP_LEFT
        )
    }
    override fun getRange(): Int {
        return 1
    }
}






