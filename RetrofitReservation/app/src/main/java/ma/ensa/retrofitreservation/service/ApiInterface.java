package ma.ensa.retrofitreservation.service;

import java.util.List;

import ma.ensa.retrofitreservation.models.Chambre;
import ma.ensa.retrofitreservation.models.Client;
import ma.ensa.retrofitreservation.models.Reservation;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    // Endpoints pour Chambre
    @GET("api/chambres")
    Call<List<Chambre>> getAllChambres();

    @POST("api/chambres")
    Call<Chambre> createChambre(@Body Chambre chambre);

    @GET("api/chambres/{id}")
    Call<Chambre> getChambreById(@Path("id") Long id);

    @PUT("api/chambres/{id}")
    Call<Chambre> updateChambre(@Path("id") Long id, @Body Chambre chambre);

    @DELETE("api/chambres/{id}")
    Call<Void> deleteChambre(@Path("id") Long id);

    // Endpoints pour Client
    @GET("api/clients")
    Call<List<Client>> getAllClients();

    @GET("api/clients/names")
    Call<List<String>> getClientsNames();

    @POST("api/clients")
    Call<Client> createClient(@Body Client client);

    @GET("api/clients/{id}")
    Call<Client> getClientById(@Path("id") Long id);

    @PUT("api/clients/{id}")
    Call<Client> updateClient(@Path("id") Long id, @Body Client client);

    @DELETE("api/clients/{id}")
    Call<Void> deleteClient(@Path("id") Long id);

    // Ajouter une méthode dans votre API pour récupérer un client par son nom et prénom
    @GET("clients/find")
    Call<Client> getClientByNameAndSurname(@Query("name") String name, @Query("surname") String surname);

    // Récupérer toutes les réservations
    @GET("api/reservations")
    Call<List<Reservation>> getAllReservations();

    // Créer une nouvelle réservation
    @POST("api/reservations")
    Call<Reservation> createReservation(@Body Reservation reservation);

    // Récupérer une réservation par ID
    @GET("api/reservations/{id}")
    Call<Reservation> getReservationById(@Path("id") Long id);

    // Mettre à jour une réservation par ID
    @PUT("api/reservations/{id}")
    Call<Reservation> updateReservation(@Path("id") Long id, @Body Reservation reservation);

    // Supprimer une réservation par ID
    @DELETE("api/reservations/{id}")
    Call<Void> deleteReservation(@Path("id") Long id);
}
