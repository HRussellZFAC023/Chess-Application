package Model.Tournament

import java.util.*

class Tournaments(var tournamentID: Int, var date: Date, var location: String){
    override fun toString(): String {
        return "Tournaments(tournamentID=$tournamentID, date=$date, location='$location')"
    }
}