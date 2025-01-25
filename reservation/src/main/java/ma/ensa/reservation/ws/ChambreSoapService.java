package ma.ensa.reservation.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import ma.ensa.reservation.models.Chambre;
import ma.ensa.reservation.models.DispoChambre;
import ma.ensa.reservation.models.TypeChambre;
import ma.ensa.reservation.repositories.ChambreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@WebService(serviceName = "ChambreWS")
public class ChambreSoapService {

    @Autowired
    private ChambreRepository chambreRepository;

    /**
     * Retrieve all rooms.
     * @return List of all rooms.
     */
    @WebMethod
    public List<Chambre> getChambres() {
        return chambreRepository.findAll();
    }

    /**
     * Retrieve a room by ID.
     * @param id The ID of the room.
     * @return The room if found, otherwise null.
     */
    @WebMethod
    public Chambre getChambreById(@WebParam(name = "id") Long id) {
        Optional<Chambre> chambre = chambreRepository.findById(id);
        return chambre.orElse(null);
    }

    /**
     * Create a new room.
     * @param typeChambre The type of the room.
     * @param prix The price of the room.
     * @param dispoChambre The availability status of the room.
     * @return The created room.
     */
    @WebMethod
    public Chambre createChambre(
            @WebParam(name = "typeChambre") TypeChambre typeChambre,
            @WebParam(name = "prix") double prix,
            @WebParam(name = "dispoChambre") DispoChambre dispoChambre) {
        Chambre chambre = new Chambre();
        chambre.setTypeChambre(typeChambre);
        chambre.setPrix(prix);
        chambre.setDispoChambre(dispoChambre);
        return chambreRepository.save(chambre);
    }

    /**
     * Update an existing room.
     * @param id Room ID.
     * @param typeChambre Updated type of the room.
     * @param prix Updated price.
     * @param dispoChambre Updated availability status.
     * @return The updated room, or null if not found.
     */
    @WebMethod
    public Chambre updateChambre(
            @WebParam(name = "id") Long id,
            @WebParam(name = "typeChambre") TypeChambre typeChambre,
            @WebParam(name = "prix") double prix,
            @WebParam(name = "dispoChambre") DispoChambre dispoChambre) {
        Optional<Chambre> optionalChambre = chambreRepository.findById(id);

        if (optionalChambre.isPresent()) {
            Chambre chambre = optionalChambre.get();
            chambre.setTypeChambre(typeChambre);
            chambre.setPrix(prix);
            chambre.setDispoChambre(dispoChambre);
            return chambreRepository.save(chambre);
        }
        return null;
    }

    /**
     * Delete a room by ID.
     * @param id The ID of the room to delete.
     * @return True if deletion was successful, false otherwise.
     */
    @WebMethod
    public boolean deleteChambre(@WebParam(name = "id") Long id) {
        if (chambreRepository.existsById(id)) {
            chambreRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
