package ma.ensa.reservation.repositories;

import ma.ensa.reservation.models.Chambre;
import ma.ensa.reservation.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChambreRepository extends JpaRepository<Chambre, Long> {
}
