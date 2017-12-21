package controller.pieces

import controller.MoveList
import controller.Piece

class Knight(colour: Boolean) : Piece(colour){
    override fun getPieceMoves(): Array<MoveList> {
        return arrayOf(MoveList.UP)
    }
}
