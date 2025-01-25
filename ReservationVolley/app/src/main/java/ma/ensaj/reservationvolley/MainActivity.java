package ma.ensaj.reservationvolley;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

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

        // Afficher la durée de lancement de l'application
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        Log.d("AppLaunchTime", "Durée de lancement de l'app : " + duration + " ms");

        // On clic sur le bouton Reservation
        btnReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité ReservationActivity
                Intent intent = new Intent(MainActivity.this, ListReservationActivity.class);
                startActivity(intent);
            }
        });

        // On clic sur le bouton Chambre
        btnChambre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité ListChambreActivity
                Intent intent = new Intent(MainActivity.this, ListChambreActivity.class);
                startActivity(intent);
            }
        });

        // On clic sur le bouton Client
        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer l'activité ListClientActivity
                Intent intent = new Intent(MainActivity.this, ListClientActivity.class);
                startActivity(intent);
            }
        });
    }
}
