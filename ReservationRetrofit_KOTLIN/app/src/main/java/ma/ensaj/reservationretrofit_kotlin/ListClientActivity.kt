package ma.ensaj.reservationretrofit_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.reservationretrofit_kotlin.adapters.ClientAdapter
import ma.ensaj.reservationretrofit_kotlin.models.Client
import ma.ensaj.reservationretrofit_kotlin.network.RetrofitInstance
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListClientActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ClientAdapter
    private val clients: MutableList<Client> = mutableListOf()
    private lateinit var buttonAddClient: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_client)

        recyclerView = findViewById(R.id.list_clients)
        buttonAddClient = findViewById(R.id.button_add_client)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize adapter with empty data
        adapter = ClientAdapter(clients)
        recyclerView.adapter = adapter

        // Measure latency for fetching clients
        measureLatencyForFetchingClients()

        // Set up swipe-to-delete functionality
        setUpSwipeToDelete()

        buttonAddClient.setOnClickListener {
            val intent = Intent(this@ListClientActivity, AddClientActivity::class.java)
            startActivity(intent)
        }
    }

    private fun measureLatencyForFetchingClients() {
        val startTime = System.currentTimeMillis()

        // Perform network call to fetch all clients
        val call = RetrofitInstance.getApi().getAllClients()
        call.enqueue(object : Callback<List<Client>> {
            override fun onResponse(call: Call<List<Client>>, response: Response<List<Client>>) {
                val latency = System.currentTimeMillis() - startTime

                // Calculate the data size in KB
                val dataSizeInKB = calculateResponseSizeInKB(response)
                Log.d("ListClientActivity", "Latency for GET (fetch clients): $latency ms")
                Log.d("ListClientActivity", "Data size for GET (fetch clients): $dataSizeInKB KB")

                if (response.isSuccessful) {
                    val fetchedClients = response.body()
                    Log.d("ListClientActivity", "Fetched clients: $fetchedClients")
                    if (!fetchedClients.isNullOrEmpty()) {
                        clients.clear()
                        clients.addAll(fetchedClients)
                        adapter.notifyDataSetChanged() // Notify adapter to refresh the data
                    } else {
                        Toast.makeText(this@ListClientActivity, "Aucun client trouvé.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ListClientActivity, "Erreur lors de la récupération des clients.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Client>>, t: Throwable) {
                Toast.makeText(this@ListClientActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun measureLatencyForDelete(clientId: Long, position: Int) {
        val startTime = System.currentTimeMillis()

        // Proceed with deleting the client
        deleteClient(clientId, position, startTime)
    }

    private fun deleteClient(clientId: Long, position: Int, startTime: Long) {
        // In many cases, DELETE requests don't include a body, just the ID in the URL
        val call = RetrofitInstance.getApi().deleteClient(clientId)

        // Log the data size for the DELETE request (optional, if sending data)
        val deleteDataSizeInKB = calculateRequestSizeInKB(clientId)
        Log.d("ListClientActivity", "Data size for DELETE (delete client): $deleteDataSizeInKB KB")

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val latency = System.currentTimeMillis() - startTime
                Log.d("ListClientActivity", "Latency for DELETE (delete client): $latency ms")

                if (response.isSuccessful) {
                    clients.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(this@ListClientActivity, "Client supprimé", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ListClientActivity, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ListClientActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun calculateResponseSizeInKB(response: Response<List<Client>>): Double {
        // Convert the response body to bytes and then to kilobytes
        val responseBody = response.body()?.let {
            val jsonString = RetrofitInstance.getGson().toJson(it)
            val byteArray = jsonString.toByteArray(Charsets.UTF_8)
            byteArray.size.toDouble()
        } ?: 0.0

        return responseBody // Convert bytes to kilobytes
    }

    private fun calculateRequestSizeInKB(clientId: Long): Double {
        // Prepare a JSON-like string for the DELETE request (just the ID)
        val jsonString = "{\"id\": $clientId}"
        val byteArray = jsonString.toByteArray(Charsets.UTF_8)

        return byteArray.size.toDouble()
    }

    private fun setUpSwipeToDelete() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val client = clients[position]

                android.app.AlertDialog.Builder(this@ListClientActivity)
                    .setMessage("Voulez-vous vraiment supprimer ce client ?")
                    .setCancelable(false)
                    .setPositiveButton("Oui") { _, _ ->
                        client.id?.let { measureLatencyForDelete(it, position) }
                    }
                    .setNegativeButton("Non") { _, _ ->
                        adapter.notifyItemChanged(position) // Restore the item
                    }
                    .create()
                    .show()
            }
        }

        ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView)
    }
}
