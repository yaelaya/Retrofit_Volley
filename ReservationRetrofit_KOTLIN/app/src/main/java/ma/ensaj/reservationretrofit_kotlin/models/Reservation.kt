package ma.ensaj.reservationretrofit_kotlin.models

class Reservation {

    var id: Long? = null
    var startDate: String? = null
    var endDate: String? = null
    var clientId: Long? = null // Ensure these fields match those from the backend
    var chambreId: Long? = null
    var client: Client? = null
    var chambre: Chambre? = null
}
