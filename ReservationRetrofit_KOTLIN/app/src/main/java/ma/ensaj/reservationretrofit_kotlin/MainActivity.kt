package ma.ensaj.reservationretrofit_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var startTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Capture du temps au démarrage de l'application
        startTime = System.currentTimeMillis()

        setContentView(R.layout.activity_main)

        // Afficher la durée de lancement de l'application
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        Log.d("AppLaunchTime", "Durée de lancement de l'app : $duration ms")

        // Initialisation des boutons
        val btnReservation: Button = findViewById(R.id.btn_reservation)
        val btnChambre: Button = findViewById(R.id.btn_chambre)
        val btnClient: Button = findViewById(R.id.btn_client)

        // On clic sur le bouton Reservation
        btnReservation.setOnClickListener {
            // Lancer l'activité ReservationActivity
            val intent = Intent(this, ListReservationActivity::class.java)
            startActivity(intent)
        }

        // On clic sur le bouton Chambre
        btnChambre.setOnClickListener {
            // Lancer l'activité ListChambreActivity
            val intent = Intent(this, ListChambreActivity::class.java)
            startActivity(intent)
        }

        // On clic sur le bouton Client
        btnClient.setOnClickListener {
            // Lancer l'activité ListClientActivity
            val intent = Intent(this, ListClientActivity::class.java)
            startActivity(intent)
        }
    }
}
