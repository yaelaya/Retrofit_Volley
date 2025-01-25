package ma.ensaj.reservationretrofit_kotlin

import android.os.Bundle
import android.util.Log
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ma.ensaj.reservationretrofit_kotlin.models.Client
import ma.ensaj.reservationretrofit_kotlin.network.RetrofitInstance
import ma.ensaj.reservationretrofit_kotlin.service.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddClientActivity : AppCompatActivity() {

    private lateinit var btnAddClient: Button
    private lateinit var nom: EditText
    private lateinit var prenom: EditText
    private lateinit var email: EditText
    private lateinit var telephone: EditText
    private val Tag = "Client"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client)

        // Enable Edge-to-Edge layout by hiding status and navigation bars
        val windowInsetsController = window.insetsController
        windowInsetsController?.hide(WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE)

        btnAddClient = findViewById(R.id.btnAddClient)
        nom = findViewById(R.id.nom)
        prenom = findViewById(R.id.prenom)
        email = findViewById(R.id.email)
        telephone = findViewById(R.id.telephone)

        btnAddClient.setOnClickListener { createClient() }
    }

    private fun createClient() {
        val client = Client().apply {
            nom = this@AddClientActivity.nom.text.toString()
            prenom = this@AddClientActivity.prenom.text.toString()
            email = this@AddClientActivity.email.text.toString()
            telephone = this@AddClientActivity.telephone.text.toString()
        }

        if (client.nom.isNullOrEmpty() || client.prenom.isNullOrEmpty() ||
            client.email.isNullOrEmpty() || client.telephone.isNullOrEmpty()
        ) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Appel réseau réel avec Retrofit via ApiInterface
        val apiService = RetrofitInstance.getApi()
        val call = apiService.createClient(client)

        // Mesurer le temps de latence
        val startTime = System.currentTimeMillis()

        call.enqueue(object : Callback<Client> {
            override fun onResponse(call: Call<Client>, response: Response<Client>) {
                val latency = System.currentTimeMillis() - startTime
                if (response.isSuccessful) {
                    Toast.makeText(this@AddClientActivity, "Client added successfully!", Toast.LENGTH_SHORT).show()
                    Log.d(Tag, "Client added: $client")
                    Log.d(Tag, "Latency: $latency ms")
                } else {
                    Toast.makeText(this@AddClientActivity, "Failed to add client. Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e(Tag, "Error code: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Client>, t: Throwable) {
                val latency = System.currentTimeMillis() - startTime
                Toast.makeText(this@AddClientActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e(Tag, "Network error: ${t.message}")
                Log.d(Tag, "Latency: $latency ms")
            }
        })
    }
}
