package model.tournament

class Players(var studentNumber: Int, private var firstName : String, private var lastName: String, var firstYear: Boolean) {
    override fun toString(): String {
        return "Players name is $firstName $lastName"
    }
}