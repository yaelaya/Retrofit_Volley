package ma.ensa.reservation.controllers;



import ma.ensa.reservation.models.Chambre;
import ma.ensa.reservation.repositories.ChambreRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ChambreControllerGraphQL {

    private final ChambreRepository chambreRepository;

    // Explicit constructor for dependency injection
    public ChambreControllerGraphQL(ChambreRepository chambreRepository) {
        this.chambreRepository = chambreRepository;
    }

    // Query to get all rooms (Chambres)
    @QueryMapping
    public List<Chambre> allChambres() {
        return chambreRepository.findAll();
    }

    // Query to get a room by ID
    @QueryMapping
    public Chambre chambreById(@Argument Long id) {
        return chambreRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Chambre not found with ID: " + id));
    }

    // Mutation to save a new or existing room
    @MutationMapping
    public Chambre saveChambre(@Argument Chambre chambre) {
        return chambreRepository.save(chambre);
    }

    // Mutation to delete a room by ID
    @MutationMapping
    public String deleteChambre(@Argument Long id) {
        if (chambreRepository.existsById(id)) {
            chambreRepository.deleteById(id);
            return "Chambre with ID " + id + " has been deleted successfully.";
        } else {
            throw new RuntimeException("Chambre not found with ID: " + id);
        }
    }

    // Mutation to update an existing room
    @MutationMapping
    public Chambre updateChambre(@Argument Long id, @Argument Chambre updatedChambre) {
        return chambreRepository.findById(id).map(chambre -> {
            chambre.setTypeChambre(updatedChambre.getTypeChambre());
            chambre.setPrix(updatedChambre.getPrix());
            chambre.setDispoChambre(updatedChambre.getDispoChambre());
            return chambreRepository.save(chambre);
        }).orElseThrow(() -> new RuntimeException("Chambre not found with ID: " + id));
    }
}
