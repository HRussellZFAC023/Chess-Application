package model.tournament

class Tournaments(private var tournamentID: Int, private var date: String, private var location: String){
    override fun toString(): String {
        return "Tournaments(tournamentID=$tournamentID, date=$date, location='$location')"
    }
}