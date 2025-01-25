package ma.ensa.reservation.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import ma.ensa.reservation.models.Chambre;
import ma.ensa.reservation.models.Client;
import ma.ensa.reservation.models.Reservation;
import ma.ensa.reservation.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@WebService(serviceName = "ReservationWS")
public class ReservationSoapService {

    @Autowired
    private ReservationRepository reservationRepository;

    /**
     * Retrieve all reservations.
     * @return List of all reservations.
     */
    @WebMethod
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Retrieve a reservation by its ID.
     * @param id The ID of the reservation.
     * @return The reservation if found, otherwise null.
     */
    @WebMethod
    public Reservation getReservationById(@WebParam(name = "id") Long id) {
        Optional<Reservation> reservation = reservationRepository.findById(id);
        return reservation.orElse(null);
    }

    /**
     * Create a new reservation.
     * @param clientId ID of the client.
     * @param chambreId ID of the room.
     * @param dateDebut Start date of the reservation.
     * @param dateFin End date of the reservation.
     * @param preferences Preferences of the client for the reservation.
     * @return The created reservation.
     */
    @WebMethod
    public Reservation createReservation(
            @WebParam(name = "clientId") Long clientId,
            @WebParam(name = "chambreId") Long chambreId,
            @WebParam(name = "dateDebut") Date dateDebut,
            @WebParam(name = "dateFin") Date dateFin,
            @WebParam(name = "preferences") String preferences) {

        if (dateDebut == null || dateFin == null || dateDebut.after(dateFin)) {
            throw new IllegalArgumentException("Invalid date range: 'dateDebut' must be before 'dateFin'.");
        }

        Reservation reservation = new Reservation();
        reservation.setClient(new Client(clientId));
        reservation.setChambre(new Chambre(chambreId));
        reservation.setDateDebut(dateDebut);
        reservation.setDateFin(dateFin);
        reservation.setPreferences(preferences);

        return reservationRepository.save(reservation);
    }

    /**
     * Update an existing reservation.
     * @param id ID of the reservation to update.
     * @param clientId Updated client ID.
     * @param chambreId Updated room ID.
     * @param dateDebut Updated start date.
     * @param dateFin Updated end date.
     * @param preferences Updated preferences.
     * @return The updated reservation, or null if not found.
     */
    @WebMethod
    public Reservation updateReservation(
            @WebParam(name = "id") Long id,
            @WebParam(name = "clientId") Long clientId,
            @WebParam(name = "chambreId") Long chambreId,
            @WebParam(name = "dateDebut") Date dateDebut,
            @WebParam(name = "dateFin") Date dateFin,
            @WebParam(name = "preferences") String preferences) {

        if (dateDebut == null || dateFin == null || dateDebut.after(dateFin)) {
            throw new IllegalArgumentException("Invalid date range: 'dateDebut' must be before 'dateFin'.");
        }

        Optional<Reservation> optionalReservation = reservationRepository.findById(id);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.setClient(new Client(clientId));
            reservation.setChambre(new Chambre(chambreId));
            reservation.setDateDebut(dateDebut);
            reservation.setDateFin(dateFin);
            reservation.setPreferences(preferences);

            return reservationRepository.save(reservation);
        } else {
            return null;
        }
    }

    /**
     * Delete a reservation by its ID.
     * @param id The ID of the reservation to delete.
     * @return True if deletion was successful, false otherwise.
     */
    @WebMethod
    public boolean deleteReservation(@WebParam(name = "id") Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
