package Model.Tournament

import java.util.*

class Tournaments(var tournamentID: Int, var studentNumber: Int, var date: Date, var location: String){
    override fun toString(): String {
        return "Tournaments(tournamentID=$tournamentID, studentNumber=$studentNumber, date=$date, location='$location')"
    }
}