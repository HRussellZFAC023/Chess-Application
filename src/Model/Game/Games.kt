package Model.Game

class Games(var gameId: Int, var gameDate: String, var white: String, var black: String){
    override fun toString(): String {
        return "Games(gameId=$gameId, gameDate=$gameDate)"
    }
}