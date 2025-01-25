package ma.ensaj.reservationretrofit_kotlin.models

import com.google.gson.annotations.SerializedName

class Client {

    var id: Long? = null

    @SerializedName("nom")
    var nom: String? = null

    @SerializedName("prenom")
    var prenom: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("telephone")
    var telephone: String? = null


    // Empty constructor
    constructor()

    // Full constructor
    constructor(id: Long?, nom: String?, prenom: String?, email: String?, telephone: String?) {
        this.id = id
        this.nom = nom
        this.prenom = prenom
        this.email = email
        this.telephone = telephone
    }

    override fun toString(): String {
        return " Client : $id $nom $prenom"
    }
}
