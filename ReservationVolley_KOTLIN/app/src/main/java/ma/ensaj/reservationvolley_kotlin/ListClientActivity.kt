package ma.ensaj.reservationvolley_kotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ma.ensaj.reservationvolley_kotlin.adapters.ClientAdapter
import ma.ensaj.reservationvolley_kotlin.models.Client
import org.json.JSONObject

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
        adapter = ClientAdapter(clients)
        recyclerView.adapter = adapter

        getAllClients()

        setUpSwipeToDelete()

        buttonAddClient.setOnClickListener {
            val intent = Intent(this, AddClientActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getAllClients() {
        val url = "http://192.168.0.217:8082/api/clients"
        measureLatency(url, "GET")
    }

    private fun deleteClient(clientId: Long, position: Int) {
        val url = "http://192.168.0.217:8082/api/clients/$clientId"
        measureLatency(url, "DELETE")
    }

    private fun measureLatency(url: String, method: String) {
        val startTime = System.currentTimeMillis()  // Capture start time

        val request = when (method) {
            "GET" -> JsonArrayRequest(
                Request.Method.GET, url, null,
                { response ->
                    val endTime = System.currentTimeMillis()  // Capture end time
                    val latency = endTime - startTime  // Calculate the actual latency
                    val responseSize = response.toString().length // Measure the size of the response data
                    Log.d("NetworkLatency", "$method Request. Response Size: $responseSize bytes, Latency: $latency ms")

                    // Parse the response and update the clients list
                    clients.clear()
                    for (i in 0 until response.length()) {
                        val clientJson = response.getJSONObject(i)
                        val client = Client(
                            id = clientJson.getLong("id"),
                            nom = clientJson.getString("nom"),
                            prenom = clientJson.getString("prenom"),
                            email = clientJson.getString("email"),
                            telephone = clientJson.getString("telephone")
                        )
                        clients.add(client)
                    }
                    adapter.notifyDataSetChanged()
                },
                { error ->
                    Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ListClientActivity", "Error: ${error.message}")
                }
            )
            "DELETE" -> StringRequest(
                Request.Method.DELETE, url,
                { response ->
                    val endTime = System.currentTimeMillis()  // Capture end time
                    val latency = endTime - startTime  // Calculate the actual latency
                    // Check if the response body is empty
                    val responseSize = if (response.isEmpty()) {
                        0
                    } else {
                        response.length // Measure the size of the response data if not empty
                    }
                    Log.d("NetworkLatency", "$method Request. Response Size: $responseSize bytes, Latency: $latency ms")

                    // Proceed with successful deletion
                    Toast.makeText(this, "Client supprimé", Toast.LENGTH_SHORT).show()
                },
                { error ->
                    Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("ListClientActivity", "Error: ${error.message}")
                }
            )
            else -> null
        }

        if (request != null) {
            Volley.newRequestQueue(this).add(request)
        }
    }


    private fun setUpSwipeToDelete() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val client = clients[position]

                AlertDialog.Builder(this@ListClientActivity)
                    .setMessage("Voulez-vous vraiment supprimer ce client ?")
                    .setCancelable(false)
                    .setPositiveButton("Oui") { _, _ ->
                        client.id?.let {
                            deleteClient(it, position)  // For DELETE request
                        }
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
