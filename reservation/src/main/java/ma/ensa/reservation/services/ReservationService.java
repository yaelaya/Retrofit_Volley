package ma.ensa.reservation.services;


import ma.ensa.reservation.models.Client;
import ma.ensa.reservation.models.Reservation;
import ma.ensa.reservation.repositories.ClientRepository;
import ma.ensa.reservation.repositories.ReservationRepository;
import org.aspectj.weaver.ast.Call;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;

@Service
public class ReservationService {


    @Autowired
    private ReservationRepository reservationRepository;

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }


    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }
    public Reservation updateReservation(Long id, Reservation updatedReservation) {
        return reservationRepository.findById(id).map(reservation -> {
            reservation.setClient(updatedReservation.getClient());
            reservation.setChambre(updatedReservation.getChambre());
            reservation.setDateDebut(updatedReservation.getDateDebut());
            reservation.setDateFin(updatedReservation.getDateFin());
            reservation.setPreferences(updatedReservation.getPreferences());
            return reservationRepository.save(reservation);
        }).orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }
}