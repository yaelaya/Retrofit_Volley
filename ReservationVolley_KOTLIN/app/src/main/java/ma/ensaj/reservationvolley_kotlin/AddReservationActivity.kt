package ma.ensaj.reservationvolley_kotlin

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import ma.ensaj.reservationvolley_kotlin.models.Chambre
import ma.ensaj.reservationvolley_kotlin.models.Client
import ma.ensaj.reservationvolley_kotlin.models.DispoChambre
import ma.ensaj.reservationvolley_kotlin.models.TypeChambre
import org.json.JSONObject
import java.util.*

class AddReservationActivity : AppCompatActivity() {

    private lateinit var btnAddReservation: Button
    private lateinit var startDate: TextView
    private lateinit var endDate: TextView
    private lateinit var clientSpinner: Spinner
    private lateinit var chambreSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reservation)

        // Initialize UI components
        btnAddReservation = findViewById(R.id.btnAddReservation)
        startDate = findViewById(R.id.startDate)
        endDate = findViewById(R.id.endDate)
        clientSpinner = findViewById(R.id.clientSpinner)
        chambreSpinner = findViewById(R.id.chambreSpinner)

        // Fetch and populate spinners
        getClients()
        getChambres()

        // Handle reservation creation
        btnAddReservation.setOnClickListener { createReservation() }

        // Show date picker dialogs
        startDate.setOnClickListener { showDatePickerDialog(startDate) }
        endDate.setOnClickListener { showDatePickerDialog(endDate) }
    }

    private fun createReservation() {
        val url = "http://192.168.0.217/api/reservations"
        val requestBody = JSONObject().apply {
            put("startDate", startDate.text.toString())
            put("endDate", endDate.text.toString())
            put("clientId", (clientSpinner.selectedItem as Client).id)
            put("chambreId", (chambreSpinner.selectedItem as Chambre).id)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                Toast.makeText(this, "Réservation ajoutée avec succès", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun getClients() {
        val url = "http://192.168.0.217/api/clients"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val clients = mutableListOf<Client>()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    clients.add(
                        Client(
                            id = item.optLong("id"),
                            nom = item.optString("nom"),
                            prenom = item.optString("prenom"),
                            email = item.optString("email"),
                            telephone = item.optString("telephone")
                        )
                    )
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, clients)
                clientSpinner.adapter = adapter
            },
            { error ->
                Toast.makeText(this, "Erreur : Impossible de charger les clients. ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun getChambres() {
        val url = "http://192.168.0.217/api/chambres"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val chambres = mutableListOf<Chambre>()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    chambres.add(
                        Chambre(
                            id = item.optLong("id"),
                            typeChambre = item.optString("typeChambre")?.let {
                                try {
                                    TypeChambre.valueOf(it)
                                } catch (e: IllegalArgumentException) {
                                    null
                                }
                            },
                            prix = item.optDouble("prix"),
                            dispoChambre = item.optString("dispoChambre")?.let {
                                try {
                                    DispoChambre.valueOf(it)
                                } catch (e: IllegalArgumentException) {
                                    null
                                }
                            }
                        )
                    )
                }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, chambres)
                chambreSpinner.adapter = adapter
            },
            { error ->
                Toast.makeText(this, "Erreur : Impossible de charger les chambres. ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun showDatePickerDialog(dateField: TextView) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                dateField.text = "$day/${month + 1}/$year"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
