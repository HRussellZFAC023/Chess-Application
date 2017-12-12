package Model.Game

class Games(var gameId: Int, var gameDate: String ){
    override fun toString(): String {
        return "Games(gameId=$gameId, gameDate=$gameDate)"
    }
}