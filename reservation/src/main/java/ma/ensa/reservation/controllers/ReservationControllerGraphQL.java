package ma.ensa.reservation.controllers;

import ma.ensa.reservation.models.*;
import ma.ensa.reservation.repositories.*;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
public class ReservationControllerGraphQL {

    private final ReservationRepository reservationRepository;
    private final ClientRepository clientRepository;
    private final ChambreRepository chambreRepository;

    public ReservationControllerGraphQL(ReservationRepository reservationRepository,
                                        ClientRepository clientRepository,
                                        ChambreRepository chambreRepository) {
        this.reservationRepository = reservationRepository;
        this.clientRepository = clientRepository;
        this.chambreRepository = chambreRepository;
    }

    private Date parseDate(String dateString) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd"); // Adjust format as needed
            return formatter.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format. Expected format is yyyy-MM-dd.");
        }
    }

    @QueryMapping
    public List<Reservation> allReservations() {
        return reservationRepository.findAll();
    }

    @QueryMapping
    public Reservation reservationById(@Argument Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + id));
    }

    @MutationMapping
    public Reservation saveReservation(@Argument ReservationInput reservation) {
        Client client = clientRepository.findById(reservation.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + reservation.getClientId()));

        Chambre chambre = chambreRepository.findById(reservation.getChambreId())
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + reservation.getChambreId()));

        Date dateDebut = parseDate(reservation.getDateDebut());
        Date dateFin = parseDate(reservation.getDateFin());

        Reservation newReservation = new Reservation(client, chambre, dateDebut, dateFin, reservation.getPreferences());
        return reservationRepository.save(newReservation);
    }

    @MutationMapping
    public Reservation updateReservation(@Argument Long id, @Argument ReservationInput updatedReservation) {
        // Find the existing reservation by ID
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + id));

        // Find the client and room by their respective IDs
        Client client = clientRepository.findById(updatedReservation.getClientId())
                .orElseThrow(() -> new RuntimeException("Client not found with ID: " + updatedReservation.getClientId()));

        Chambre chambre = chambreRepository.findById(updatedReservation.getChambreId())
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + updatedReservation.getChambreId()));

        // Update the reservation with the new details
        existingReservation.setClient(client);
        existingReservation.setChambre(chambre);
        existingReservation.setDateDebut(parseDate(updatedReservation.getDateDebut()));
        existingReservation.setDateFin(parseDate(updatedReservation.getDateFin()));
        existingReservation.setPreferences(updatedReservation.getPreferences());

        // Save the updated reservation
        return reservationRepository.save(existingReservation);
    }


    @MutationMapping
    public String deleteReservation(@Argument Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reservation not found with ID: " + id);
        }
        reservationRepository.deleteById(id);
        return "Reservation with ID " + id + " has been deleted successfully.";
    }
}