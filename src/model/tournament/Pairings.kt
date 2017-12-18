package model.tournament

class Pairings(private var roundNo: Int, private var p1Number: Int, p2Number: Int, private var roundId: Int, private var tournamentId: Int){
    override fun toString(): String {
        return "Pairings(roundNo=$roundNo, p1Number=$p1Number, roundId=$roundId, tournamentId=$tournamentId)"
    }
}
