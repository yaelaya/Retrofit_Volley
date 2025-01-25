package ma.ensa.retrofitreservation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Capture du temps au démarrage de l'application
        startTime = System.currentTimeMillis();

        setContentView(R.layout.activity_main);

        // Initialisation des boutons
        Button btnReservation = findViewById(R.id.btn_reservation);
        Button btnChambre = findViewById(R.id.btn_chambre);
        Button btnClient = findViewById(R.id.btn_client);

        // Calcul de la taille totale des données de l'application
        double totalDataSizeKB = calculateTotalDataSize();
        Log.d("AppDataSize", "Taille totale des données de l'application : " + totalDataSizeKB + " KB");

        // Afficher la durée de lancement de l'application
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        Log.d("AppLaunchTime", "Durée de lancement de l'app : " + duration + " ms");

        // On clic sur le bouton Reservation
        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListReservationActivity.class);
                startActivity(intent);
            }
        });

        // On clic sur le bouton Chambre
        btnChambre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListChambreActivity.class);
                startActivity(intent);
            }
        });

        // On clic sur le bouton Client
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListClientActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Calculer la taille simulée des données pour chaque entité (clients, chambres, réservations)
     * et retourner la taille totale.
     *
     * @return Taille totale des données en kilooctets (KB).
     */
    private double calculateTotalDataSize() {
        double reservationDataSizeKB = simulateDataSize("{\"reservations\": [...]}");
        double chambreDataSizeKB = simulateDataSize("{\"chambres\": [...]}");
        double clientDataSizeKB = simulateDataSize("{\"clients\": [...]}");

        // Additionner les tailles des données
        return reservationDataSizeKB + chambreDataSizeKB + clientDataSizeKB;
    }


    private double simulateDataSize(String jsonData) {
        byte[] byteArray = jsonData.getBytes(StandardCharsets.UTF_8);
        return byteArray.length / 1024.0; // Convertir en kilooctets (KB)
    }
}
