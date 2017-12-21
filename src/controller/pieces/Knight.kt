package controller.pieces

import controller.MoveList
import controller.Piece

class Knight(colour: Boolean) : Piece(colour) {
    override fun getPieceMoves(): Array<MoveList> {
        return arrayOf(
                MoveList.KNIGHT_LEFT_UP ,
                MoveList.KNIGHT_UP_LEFT ,
                MoveList.KNIGHT_UP_RIGHT ,
                MoveList.KNIGHT_RIGHT_UP ,
                MoveList.KNIGHT_RIGHT_DOWN ,
                MoveList.KNIGHT_DOWN_RIGHT ,
                MoveList.KNIGHT_DOWN_LEFT ,
                MoveList.KNIGHT_LEFT_DOWN
        )
    }

}
