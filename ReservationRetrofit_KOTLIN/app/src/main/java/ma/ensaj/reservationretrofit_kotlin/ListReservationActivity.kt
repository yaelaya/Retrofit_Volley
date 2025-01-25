package ma.ensaj.reservationretrofit_kotlin

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
import ma.ensaj.reservationretrofit_kotlin.adapters.ReservationAdapter
import ma.ensaj.reservationretrofit_kotlin.models.Reservation
import ma.ensaj.reservationretrofit_kotlin.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        val call = RetrofitInstance.getApi().getAllReservations()

        call.enqueue(object : Callback<List<Reservation>> {
            override fun onResponse(call: Call<List<Reservation>>, response: Response<List<Reservation>>) {
                if (response.isSuccessful) {
                    val fetchedReservations = response.body()
                    if (!fetchedReservations.isNullOrEmpty()) {
                        reservations.clear()
                        reservations.addAll(fetchedReservations)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(this@ListReservationActivity, "Aucune réservation trouvée.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ListReservationActivity, "Erreur de réponse : ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Reservation>>, t: Throwable) {
                Toast.makeText(this@ListReservationActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteReservation(reservationId: Long, position: Int) {
        val call = RetrofitInstance.getApi().deleteReservation(reservationId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    reservations.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(this@ListReservationActivity, "Réservation supprimée", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ListReservationActivity, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ListReservationActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setUpSwipeToDelete() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val reservation = reservations[position]

                android.app.AlertDialog.Builder(this@ListReservationActivity)
                    .setMessage("Voulez-vous vraiment supprimer cette réservation ?")
                    .setCancelable(false)
                    .setPositiveButton("Oui") { _, _ ->
                        reservation.id?.let { deleteReservation(it, position) }
                    }
                    .setNegativeButton("Non") { _, _ ->
                        adapter.notifyItemChanged(position)  // Restore the item
                    }
                    .create()
                    .show()
            }
        }

        ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView)
    }
}
