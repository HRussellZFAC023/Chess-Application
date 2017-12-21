package controller.pieces

import controller.MoveList
import controller.Piece

class Pawn(colour: Boolean) : Piece(colour){
    override fun getPieceMoves(): Array<MoveList> {
        return arrayOf(MoveList.UP, MoveList.DOUBLE_UP, MoveList.UP_RIGHT, MoveList.UP_LEFT)
    }
}
