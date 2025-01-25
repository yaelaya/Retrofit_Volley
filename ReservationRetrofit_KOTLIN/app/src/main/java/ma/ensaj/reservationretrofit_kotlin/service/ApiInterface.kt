package ma.ensaj.reservationretrofit_kotlin.service

import ma.ensaj.reservationretrofit_kotlin.models.Chambre
import ma.ensaj.reservationretrofit_kotlin.models.Client
import ma.ensaj.reservationretrofit_kotlin.models.Reservation
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
interface ApiInterface {

    // Endpoints pour Chambre
    @GET("api/chambres")
    fun getAllChambres(): Call<List<Chambre>>

    @POST("api/chambres")
    fun createChambre(@Body chambre: Chambre): Call<Chambre>

    @GET("api/chambres/{id}")
    fun getChambreById(@Path("id") id: Long): Call<Chambre>

    @PUT("api/chambres/{id}")
    fun updateChambre(@Path("id") id: Long, @Body chambre: Chambre): Call<Chambre>

    @DELETE("api/chambres/{id}")
    fun deleteChambre(@Path("id") id: Long): Call<Void>

    // Endpoints pour Client
    @GET("api/clients")
    fun getAllClients(): Call<List<Client>>

    @GET("api/clients/names")
    fun getClientsNames(): Call<List<String>>

    @POST("api/clients")
    fun createClient(@Body client: Client): Call<Client>

    @GET("api/clients/{id}")
    fun getClientById(@Path("id") id: Long): Call<Client>

    @PUT("api/clients/{id}")
    fun updateClient(@Path("id") id: Long, @Body client: Client): Call<Client>

    @DELETE("api/clients/{id}")
    fun deleteClient(@Path("id") id: Long): Call<Void>

    // Ajouter une méthode dans votre API pour récupérer un client par son nom et prénom
    @GET("clients/find")
    fun getClientByNameAndSurname(@Query("name") name: String, @Query("surname") surname: String): Call<Client>

    // Récupérer toutes les réservations
    @GET("api/reservations")
    fun getAllReservations(): Call<List<Reservation>>

    // Créer une nouvelle réservation
    @POST("api/reservations")
    fun createReservation(@Body reservation: Reservation): Call<Reservation>

    // Récupérer une réservation par ID
    @GET("api/reservations/{id}")
    fun getReservationById(@Path("id") id: Long): Call<Reservation>

    // Mettre à jour une réservation par ID
    @PUT("api/reservations/{id}")
    fun updateReservation(@Path("id") id: Long, @Body reservation: Reservation): Call<Reservation>

    // Supprimer une réservation par ID
    @DELETE("api/reservations/{id}")
    fun deleteReservation(@Path("id") id: Long): Call<Void>

}
