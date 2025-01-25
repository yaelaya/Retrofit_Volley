package ma.ensa.reservation.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import ma.ensa.reservation.models.Client;
import ma.ensa.reservation.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@WebService(serviceName = "ClientWS")
public class ClientSoapService {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Retrieve all clients.
     * @return List of all clients.
     */
    @WebMethod
    public List<Client> getClients() {
        return clientRepository.findAll();
    }

    /**
     * Retrieve a client by ID.
     * @param id The ID of the client.
     * @return The client if found, otherwise null.
     */
    @WebMethod
    public Client getClientById(@WebParam(name = "id") Long id) {
        Optional<Client> client = clientRepository.findById(id);
        return client.orElse(null);
    }

    /**
     * Create a new client.
     * @param nom Client's first name.
     * @param prenom Client's last name.
     * @param email Client's email.
     * @param telephone Client's phone number.
     * @return The created client.
     */
    @WebMethod
    public Client createClient(
            @WebParam(name = "nom") String nom,
            @WebParam(name = "prenom") String prenom,
            @WebParam(name = "email") String email,
            @WebParam(name = "telephone") String telephone) {
        Client client = new Client(nom, prenom, email, telephone);
        return clientRepository.save(client);
    }

    /**
     * Update an existing client.
     * @param id Client ID.
     * @param nom Updated first name.
     * @param prenom Updated last name.
     * @param email Updated email.
     * @param telephone Updated phone number.
     * @return The updated client, or null if not found.
     */
    @WebMethod
    public Client updateClient(
            @WebParam(name = "id") Long id,
            @WebParam(name = "nom") String nom,
            @WebParam(name = "prenom") String prenom,
            @WebParam(name = "email") String email,
            @WebParam(name = "telephone") String telephone) {
        Optional<Client> optionalClient = clientRepository.findById(id);

        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            client.setNom(nom);
            client.setPrenom(prenom);
            client.setEmail(email);
            client.setTelephone(telephone);
            return clientRepository.save(client);
        }
        return null;
    }

    /**
     * Delete a client by ID.
     * @param id The ID of the client to delete.
     * @return True if deletion was successful, false otherwise.
     */
    @WebMethod
    public boolean deleteClient(@WebParam(name = "id") Long id) {
        if (clientRepository.existsById(id)) {
            clientRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
