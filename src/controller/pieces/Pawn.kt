package controller.pieces

import controller.MoveList
import controller.Piece

class Pawn(colour: Boolean) : Piece(colour){
    override fun getPieceMoves(): Array<MoveList> {
        return arrayOf(MoveList.UP)
    }
}
