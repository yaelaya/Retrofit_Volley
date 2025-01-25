package ma.ensaj.reservationvolley;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import ma.ensaj.reservationvolley.models.Client;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AddClientActivity extends AppCompatActivity {

    private Button btnAddClient;
    private EditText nom;
    private EditText prenom;
    private EditText email;
    private EditText telephone;
    private final String Tag = "Client";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_client);

        btnAddClient = findViewById(R.id.btnAddClient);
        nom = findViewById(R.id.nom);
        prenom = findViewById(R.id.prenom);
        email = findViewById(R.id.email);
        telephone = findViewById(R.id.telephone);

        // Only call createClient() when the button is clicked
        btnAddClient.setOnClickListener(v -> createClient());
    }

    private void sendClientData(Client client) {
        String url = "http://192.168.0.217:8082/api/clients";  // Your API endpoint
        JSONObject requestBody = new JSONObject();
        try {
            // Creating JSON object for the client data
            requestBody.put("nom", client.getNom());
            requestBody.put("prenom", client.getPrenom());
            requestBody.put("email", client.getEmail());
            requestBody.put("telephone", client.getTelephone());

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                response -> Log.d("Response", "Client added successfully!"),
                error -> Log.d("Error", "Failed to add client: " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void createClient() {
        // Get the input data from EditText fields
        String nomValue = nom.getText().toString();
        String prenomValue = prenom.getText().toString();
        String emailValue = email.getText().toString();
        String telephoneValue = telephone.getText().toString();

        // Check if any field is empty
        if (nomValue.isEmpty() || prenomValue.isEmpty() || emailValue.isEmpty() || telephoneValue.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new client object
        Client client = new Client();
        client.setNom(nomValue);
        client.setPrenom(prenomValue);
        client.setEmail(emailValue);
        client.setTelephone(telephoneValue);

        // Send the client data to the server
        sendClientDataWithSize(client);

        // Optionally, show a success message after sending
        Toast.makeText(this, "Client added successfully!", Toast.LENGTH_SHORT).show();

        // Test with sample files (JSON and XML)
        measureAndSendData("json_10kb.png", "json");  // Example file for 10KB JSON
        measureAndSendData("json_100kb.png", "json");  // Example file for 100KB JSON
        measureAndSendData("json_500kb.png", "json");  // Example file for 500KB JSON
        measureAndSendData("json_1000kb.png", "json");  // Example file for 1000KB JSON
        measureAndSendData("xml_10kb.xml", "xml");  // Example file for 10KB XML
        measureAndSendData("xml_100kb.xml", "xml");  // Example file for 100KB XML

    }

    private void sendMessage(String filename, String fileType) {
        String data;
        if (fileType.equals("json")) {
            data = readFileFromAssets(filename, "json");  // Read JSON file content
        } else {
            data = readFileFromAssets(filename, "xml");  // Read XML file content
        }

        // Send the data using Volley
        String url = "http://192.168.0.217:8082/api/clients";  // Update this URL based on your API
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("data", data);  // Send the data as a single field
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                response -> Log.d("Response", "Message sent successfully!"),
                error -> Log.d("Error", "Failed to send message: " + error.getMessage())
        );

        Volley.newRequestQueue(this).add(request);
    }

    private long measureLatency(String filename, String fileType) {
        long startTime = System.currentTimeMillis();  // Capture start time

        // Send message with data from the file
        sendMessage(filename, fileType);

        long endTime = System.currentTimeMillis();  // Capture end time
        return endTime - startTime;  // Return latency
    }

    private void measureAndSendData(String filename, String fileType) {
        long latency = measureLatency(filename, fileType);
        Log.d("LatencyTest", "Latency for " + fileType + " " + filename + ": " + latency + " ms");

        // After measuring latency, send the data (you can send your actual client data as well)
        sendMessage(filename, fileType);
    }

    private String readFileFromAssets(String filename, String fileType) {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(filename)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private void sendClientDataWithSize(Client client) {
        String url = "http://192.168.0.217:8082/api/clients";  // Votre endpoint API
        JSONObject requestBody = new JSONObject();

        try {
            // Création de l'objet JSON avec les données du client
            requestBody.put("nom", client.getNom());
            requestBody.put("prenom", client.getPrenom());
            requestBody.put("email", client.getEmail());
            requestBody.put("telephone", client.getTelephone());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Calcul de la taille des données en bytes
        int dataSize = requestBody.toString().getBytes().length;
        Log.d("DataSize", "POST data size: " + dataSize + " bytes");

        // Measure latency after calculating size
        long startTime = System.currentTimeMillis();  // Capture start time

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                response -> {
                    long endTime = System.currentTimeMillis();  // Capture end time
                    long latency = endTime - startTime;
                    Log.d("Latency", "Request latency: " + latency + " ms");
                    Log.d("Response", "Client ajouté avec succès !");
                },
                error -> {
                    long endTime = System.currentTimeMillis();  // Capture end time
                    long latency = endTime - startTime;
                    Log.e("Error", "Erreur lors de l'ajout du client : " + error.getMessage());
                    Log.d("Latency", "Request latency: " + latency + " ms");
                }
        );

        Volley.newRequestQueue(this).add(request);
    }
}
