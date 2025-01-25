package ma.ensa.reservation.controllers;

import ma.ensa.reservation.models.Client;
import ma.ensa.reservation.repositories.ClientRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class ClientControllerGraphQL {

    private final ClientRepository clientRepository;

    // Explicit constructor for dependency injection
    public ClientControllerGraphQL(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    // Query to get all clients
    @QueryMapping
    public List<Client> allClients() {
        return clientRepository.findAll();
    }

    // Query to get a client by ID
    @QueryMapping
    public Client clientById(@Argument Long id) {
        return clientRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Client not found with ID: " + id));
    }

    // Mutation to save a new or existing client
    @MutationMapping
    public Client saveClient(@Argument Client client) {
        return clientRepository.save(client);
    }

    // Mutation to delete a client by ID
    @MutationMapping
    public String deleteClient(@Argument Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return "Client with ID " + id + " has been deleted successfully.";
        } else {
            throw new RuntimeException("Client not found with ID: " + id);
        }
    }

    // Mutation to update an existing client
    @MutationMapping
    public Client updateClient(@Argument Long id, @Argument Client updatedClient) {
        return clientRepository.findById(id).map(client -> {
            client.setNom(updatedClient.getNom());
            client.setPrenom(updatedClient.getPrenom());
            client.setEmail(updatedClient.getEmail());
            client.setTelephone(updatedClient.getTelephone());
            return clientRepository.save(client);
        }).orElseThrow(() -> new RuntimeException("Client not found with ID: " + id));
    }
}
