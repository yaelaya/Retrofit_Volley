package ma.ensa.reservation.repositories;

import ma.ensa.reservation.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findByNomAndPrenom(String nom, String prenom);

}

