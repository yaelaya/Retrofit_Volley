package ma.ensaj.reservationvolley_kotlin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ma.ensaj.reservationvolley_kotlin.models.Reservation
import ma.ensaj.reservationvolley_kotlin.adapters.ReservationAdapter
import ma.ensaj.reservationvolley_kotlin.network.VolleySingleton
import com.google.gson.Gson
import ma.ensaj.reservationvolley_kotlin.models.Chambre
import ma.ensaj.reservationvolley_kotlin.models.Client
import ma.ensaj.reservationvolley_kotlin.models.DispoChambre
import ma.ensaj.reservationvolley_kotlin.models.TypeChambre

class ListReservationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReservationAdapter
    private val reservations: MutableList<Reservation> = mutableListOf()
    private lateinit var buttonAddReservation: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_reservation)

        recyclerView = findViewById(R.id.list_reservations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        buttonAddReservation = findViewById(R.id.button_add_reservation)
        adapter = ReservationAdapter(reservations)
        recyclerView.adapter = adapter

        getAllReservations()

        buttonAddReservation.setOnClickListener {
            val intent = Intent(this@ListReservationActivity, AddReservationActivity::class.java)
            startActivity(intent)
        }

        setUpSwipeToDelete()
    }

    private fun getAllReservations() {
        val url = "http://192.168.0.217/api/reservations"
        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                reservations.clear()
                for (i in 0 until response.length()) {
                    val item = response.getJSONObject(i)
                    val reservation = Reservation().apply {
                        id = item.optLong("id", -1) // Handle missing or null values
                        startDate = item.optString("startDate", null)
                        endDate = item.optString("endDate", null)

                        // Parse client object
                        client = item.optJSONObject("client")?.let { clientJson ->
                            Client(
                                id = clientJson.optLong("id", -1),
                                nom = clientJson.optString("nom", null),
                                prenom = clientJson.optString("prenom", null),
                                email = clientJson.optString("email", null),
                                telephone = clientJson.optString("telephone", null)
                            )
                        }

                        // Parse chambre object
                        chambre = item.optJSONObject("chambre")?.let { chambreJson ->
                            Chambre(
                                id = chambreJson.optLong("id", -1),
                                typeChambre = chambreJson.optString("typeChambre", null)?.let {
                                    try {
                                        TypeChambre.valueOf(it) // Convert string to enum
                                    } catch (e: IllegalArgumentException) {
                                        null // Handle invalid enum value
                                    }
                                },
                                prix = chambreJson.optDouble("prix", 0.0),
                                dispoChambre = chambreJson.optString("dispoChambre", null)?.let {
                                    try {
                                        DispoChambre.valueOf(it) // Convert string to enum
                                    } catch (e: IllegalArgumentException) {
                                        null // Handle invalid enum value
                                    }
                                }
                            )
                        }
                    }
                    reservations.add(reservation)

                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun deleteReservation(reservationId: Long, position: Int) {
        val url = "http://192.168.0.217/api/reservations/$reservationId"
        val request = StringRequest(
            Request.Method.DELETE, url,
            {
                reservations.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this, "Réservation supprimée", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }

    private fun setUpSwipeToDelete() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val reservation = reservations[position]

                AlertDialog.Builder(this@ListReservationActivity)
                    .setMessage("Voulez-vous vraiment supprimer cette réservation ?")
                    .setCancelable(false)
                    .setPositiveButton("Oui") { _, _ ->
                        reservation.id?.let { deleteReservation(it, position) }
                    }
                    .setNegativeButton("Non") { _, _ ->
                        adapter.notifyItemChanged(position)
                    }
                    .create()
                    .show()
            }
        }
        ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView)
    }
}
