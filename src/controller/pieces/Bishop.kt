package controller.pieces

import controller.MoveList
import controller.Piece

class Bishop(colour: Boolean) : Piece(colour) {
    override fun getPieceMoves(): Array<MoveList> {
        return arrayOf(
                MoveList.UP_RIGHT ,
                MoveList.DOWN_RIGHT ,
                MoveList.DOWN_LEFT ,
                MoveList.UP_LEFT
        )
    }

    override fun getRange(): Int {
        return 8
    }
}

