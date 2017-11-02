package Model.Tournament

class  Pairing(var roundNo: Int, var p1Number: Int, p2Number: Int, var roundId: Int, var tournamentId: Int){
    override fun toString(): String {
        return "Pairing(roundNo=$roundNo, p1Number=$p1Number, roundId=$roundId, tournamentId=$tournamentId)"
    }
}
