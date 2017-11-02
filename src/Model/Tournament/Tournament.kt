package Model.Tournament

import java.util.*

class Tournament(var tournamentID: Int, var studentNumber: Int, var date: Date, var location: String){
    override fun toString(): String {
        return "Tournament(tournamentID=$tournamentID, studentNumber=$studentNumber, date=$date, location='$location')"
    }
}