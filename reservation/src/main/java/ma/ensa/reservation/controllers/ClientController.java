package ma.ensa.reservation.controllers;

import ma.ensa.reservation.models.Client;
import ma.ensa.reservation.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }

    @PostMapping
    public Client createClient(@RequestBody Client client) {
        return clientService.createClient(client);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client updatedClient) {
        Optional<Client> client = Optional.ofNullable(clientService.updateClient(id, updatedClient));
        return client
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/names")
    public List<String> getClientsNames() {
        List<Client> clients = clientService.getAllClients();
        List<String> clientNames = new ArrayList<>();

        for (Client client : clients) {
            // Concaténer nom et prénom pour chaque client
            clientNames.add(client.getNom() + " " + client.getPrenom());
        }

        return clientNames;
    }

    @GetMapping("/clients/find")
    public ResponseEntity<Client> getClientByNameAndSurname(@RequestParam String name, @RequestParam String surname) {
        Client client = clientService.findClientByNameAndSurname(name, surname);
        if (client != null) {
            return ResponseEntity.ok(client);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}