package Model.Tournament

class Player(var studentNumber: Int, var firstName : String, var lastName: String, var firstYear: Boolean) {
    override fun toString(): String {
        return "Player name is'$firstName' '$lastName')"
    }
}