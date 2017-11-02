package Model.Game

class Moves(var moveId : Int, var gameId: Int,var Move: String){
    override fun toString(): String {
        return "Moves(Move='$Move')"
    }
}