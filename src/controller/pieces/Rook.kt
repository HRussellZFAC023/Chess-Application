package controller.pieces

import controller.MoveList
import controller.Piece

class Rook(colour: Boolean) : Piece(colour){
    override fun getPieceMoves(): Array<MoveList> {
        return arrayOf(
                MoveList.UP ,
                MoveList.RIGHT ,
                MoveList.DOWN ,
                MoveList.LEFT
        )
    }
    override fun getRange(): Int {
        return 8
    }
}

