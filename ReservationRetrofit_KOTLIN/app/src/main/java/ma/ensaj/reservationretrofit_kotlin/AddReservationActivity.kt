package ma.ensaj.reservationretrofit_kotlin

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ma.ensaj.reservationretrofit_kotlin.adapters.ClientSpinnerAdapter
import ma.ensaj.reservationretrofit_kotlin.models.Chambre
import ma.ensaj.reservationretrofit_kotlin.models.Client
import ma.ensaj.reservationretrofit_kotlin.models.Reservation
import ma.ensaj.reservationretrofit_kotlin.network.RetrofitInstance
import ma.ensaj.reservationretrofit_kotlin.service.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import android.view.WindowInsetsController


class AddReservationActivity : AppCompatActivity() {

    private lateinit var btnAddReservation: Button
    private lateinit var startDate: TextView
    private lateinit var endDate: TextView
    private lateinit var clientSpinner: Spinner
    private lateinit var chambreSpinner: Spinner
    private val tag = "Reservation"
    private lateinit var apiInterface: ApiInterface
    private var clientsList: List<Client> = ArrayList()
    private var chambresList: List<Chambre> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reservation)

        // Enable Edge-to-Edge layout by hiding status and navigation bars
        val windowInsetsController = window.insetsController
        windowInsetsController?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)

        // Initialize views
        btnAddReservation = findViewById(R.id.btnAddReservation)
        startDate = findViewById(R.id.startDate)
        endDate = findViewById(R.id.endDate)
        clientSpinner = findViewById(R.id.clientSpinner)
        chambreSpinner = findViewById(R.id.chambreSpinner)

        // Fetch clients and chambres via Retrofit
        getClients()
        getChambres()

        // Button click to create reservation
        btnAddReservation.setOnClickListener { createReservation() }

        // Date Picker for start date
        startDate.setOnClickListener { showDatePickerDialog(startDate) }

        // Date Picker for end date
        endDate.setOnClickListener { showDatePickerDialog(endDate) }
    }

    private fun showDatePickerDialog(dateField: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Display the selected date in the TextView
                val selectedDate = "$selectedDayOfMonth/${selectedMonth + 1}/$selectedYear"
                dateField.text = selectedDate
            },
            year, month, day
        )

        // Show the DatePickerDialog
        datePickerDialog.show()
    }

    private fun createReservation() {
        // Create a Reservation object
        val reservation = Reservation().apply {
            startDate = this@AddReservationActivity.startDate.text.toString()
            endDate = this@AddReservationActivity.endDate.text.toString()
            client = clientSpinner.selectedItem as Client
            chambre = chambreSpinner.selectedItem as Chambre
        }

        // Send reservation to the API
        val call = RetrofitInstance.getApi().createReservation(reservation)

        call.enqueue(object : Callback<Reservation> {
            override fun onResponse(call: Call<Reservation>, response: Response<Reservation>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddReservationActivity, "Réservation ajoutée avec succès", Toast.LENGTH_SHORT).show()
                    Log.d(tag, response.toString())
                } else {
                    Toast.makeText(this@AddReservationActivity, "Erreur lors de l'ajout de la réservation.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Reservation>, t: Throwable) {
                Toast.makeText(this@AddReservationActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getClients() {
        val call = RetrofitInstance.getApi().getAllClients()

        call.enqueue(object : Callback<List<Client>> {
            override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                if (response.isSuccessful) {
                    clientsList = response.body() ?: emptyList()

                    // Custom adapter for the client spinner
                    val clientAdapter = ClientSpinnerAdapter(this@AddReservationActivity, clientsList)
                    clientSpinner.adapter = clientAdapter
                } else {
                    Toast.makeText(this@AddReservationActivity, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Client>>, t: Throwable) {
                Toast.makeText(this@AddReservationActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getChambres() {
        val call = RetrofitInstance.getApi().getAllChambres()

        call.enqueue(object : Callback<List<Chambre>> {
            override fun onResponse(call: Call<List<Chambre>>, response: Response<List<Chambre>>) {
                if (response.isSuccessful) {
                    chambresList = response.body() ?: emptyList()

                    // Fill the chambre spinner with retrieved data
                    val chambreAdapter = ArrayAdapter(
                        this@AddReservationActivity,
                        android.R.layout.simple_spinner_item, chambresList
                    )
                    chambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    chambreSpinner.adapter = chambreAdapter
                } else {
                    Toast.makeText(this@AddReservationActivity, "Erreur lors de la récupération des chambres", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Chambre>>, t: Throwable) {
                Toast.makeText(this@AddReservationActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}