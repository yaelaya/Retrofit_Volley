package ma.ensaj.reservationvolley_kotlin.models
class Chambre {

    var id: Long? = null
    var typeChambre: TypeChambre? = null // Enum for the type of room
    var prix: Double = 0.0
    var dispoChambre: DispoChambre? = null // Enum for the availability of the room

    // Empty constructor
    constructor()

    // Full constructor
    constructor(id: Long?, typeChambre: TypeChambre?, prix: Double, dispoChambre: DispoChambre?) {
        this.id = id
        this.typeChambre = typeChambre
        this.prix = prix
        this.dispoChambre = dispoChambre
    }

    override fun toString(): String {
        return "Room: $id ( $typeChambre à $prix MAD)" // Customize as needed
    }
}
