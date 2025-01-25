package ma.ensa.retrofitreservation;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ma.ensa.retrofitreservation.models.Client;
import ma.ensa.retrofitreservation.network.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddClientActivity extends AppCompatActivity {

    private Button btnAddClient;
    private EditText nom, prenom, email, telephone;
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

        btnAddClient.setOnClickListener(v -> createClient());
    }

    private void createClient() {
        String nomValue = nom.getText().toString();
        String prenomValue = prenom.getText().toString();
        String emailValue = email.getText().toString();
        String telephoneValue = telephone.getText().toString();

        if (nomValue.isEmpty() || prenomValue.isEmpty() || emailValue.isEmpty() || telephoneValue.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Client client = new Client();
        client.setNom(nomValue);
        client.setPrenom(prenomValue);
        client.setEmail(emailValue);
        client.setTelephone(telephoneValue);

        sendClientDataWithSize(client);

        // Test with sample files (JSON and XML)
        measureAndSendData("json_10kb.png", "json");  // Example file for 10KB JSON
        measureAndSendData("json_100kb.png", "json");  // Example file for 100KB JSON
        measureAndSendData("json_500kb.png", "json");  // Example file for 500KB JSON
        measureAndSendData("json_1000kb.png", "json");  // Example file for 1000KB JSON
        measureAndSendData("xml_10kb.xml", "xml");  // Example file for 10KB XML
        measureAndSendData("xml_100kb.xml", "xml");  // Example file for 100KB XML
    }

    private void sendClientDataWithSize(Client client) {
        long startTime = System.currentTimeMillis();  // Capture start time

        Call<Client> call = RetrofitInstance.getApi().createClient(client);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                long endTime = System.currentTimeMillis();  // Capture end time
                long latency = endTime - startTime;  // Calculate latency
                Log.d("Latency", "Request latency: " + latency + " ms");

                if (response.isSuccessful()) {
                    Toast.makeText(AddClientActivity.this, "Client ajouté avec succès", Toast.LENGTH_SHORT).show();
                    Log.d(Tag, response.toString());
                } else {
                    Toast.makeText(AddClientActivity.this, "Erreur lors de l'ajout du client.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Toast.makeText(AddClientActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String filename, String fileType) {
        String data = readFileFromAssets(filename, fileType);

        Client client = new Client();
        client.setNom("Test");
        client.setPrenom("Test");
        client.setEmail("test@example.com");
        client.setTelephone("1234567890");

        Call<Client> call = RetrofitInstance.getApi().createClient(client);
        call.enqueue(new Callback<Client>() {
            @Override
            public void onResponse(Call<Client> call, Response<Client> response) {
                if (response.isSuccessful()) {
                    Log.d("Response", "Message sent successfully!");
                } else {
                    Log.d("Error", "Failed to send message: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Client> call, Throwable t) {
                Log.d("Error", "Failed to send message: " + t.getMessage());
            }
        });
    }

    private long measureLatency(String filename, String fileType) {
        long startTime = System.currentTimeMillis();  // Capture start time

        sendMessage(filename, fileType);

        long endTime = System.currentTimeMillis();  // Capture end time
        return endTime - startTime;  // Return latency
    }

    private void measureAndSendData(String filename, String fileType) {
        long latency = measureLatency(filename, fileType);
        Log.d("LatencyTest", "Latency for " + fileType + " " + filename + ": " + latency + " ms");

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
}