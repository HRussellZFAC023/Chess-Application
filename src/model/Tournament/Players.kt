package model.Tournament

class Players(var studentNumber: Int, var firstName : String, var lastName: String, var firstYear: Boolean) {
    override fun toString(): String {
        return "Players name is $firstName $lastName"
    }
}