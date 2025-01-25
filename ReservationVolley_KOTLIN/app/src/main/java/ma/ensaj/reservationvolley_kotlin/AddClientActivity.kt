package ma.ensaj.reservationvolley_kotlin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import ma.ensaj.reservationvolley_kotlin.models.Client
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

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

        btnAddClient = findViewById(R.id.btnAddClient)
        nom = findViewById(R.id.nom)
        prenom = findViewById(R.id.prenom)
        email = findViewById(R.id.email)
        telephone = findViewById(R.id.telephone)

        btnAddClient.setOnClickListener { createClient() }
    }

    private fun sendMessage(filename: String, fileType: String) {
        val data = readFileFromAssets(filename, fileType)

        val url = "http://192.168.0.217:8082/api/clients"
        val requestBody = JSONObject().apply {
            put("data", data)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                Log.d("Response", "Message sent successfully!")
            },
            { error ->
                Log.d("Error", "Failed to send message: ${error.message}")
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun measureLatency(filename: String, fileType: String): Long {
        val startTime = System.currentTimeMillis()
        sendMessage(filename, fileType)
        return System.currentTimeMillis() - startTime
    }

    private fun measureAndSendData(filename: String, fileType: String) {
        val latency = measureLatency(filename, fileType)
        Log.d("LatencyTest", "Latency for $fileType $filename: $latency ms")
        sendMessage(filename, fileType)
    }

    private fun readFileFromAssets(filename: String, fileType: String): String {
        return assets.open(filename).bufferedReader().use(BufferedReader::readText)
    }

    private fun sendClientDataWithSize(client: Client) {
        val url = "http://192.168.0.217:8082/api/clients"
        val requestBody = JSONObject().apply {
            put("nom", client.nom)
            put("prenom", client.prenom)
            put("email", client.email)
            put("telephone", client.telephone)
        }

        val dataSize = requestBody.toString().toByteArray().size
        Log.d("DataSize", "POST data size: $dataSize bytes")

        val startTime = System.currentTimeMillis()

        val request = JsonObjectRequest(
            Request.Method.POST, url, requestBody,
            { response ->
                val latency = System.currentTimeMillis() - startTime
                Log.d("Latency", "Request latency: $latency ms")
                Log.d("Response", "Client added successfully!")
            },
            { error ->
                val latency = System.currentTimeMillis() - startTime
                Log.e("Error", "Error adding client: ${error.message}")
                Log.d("Latency", "Request latency: $latency ms")
            }
        )

        Volley.newRequestQueue(this).add(request)
    }

    private fun createClient() {
        val client = Client().apply {
            nom = this@AddClientActivity.nom.text.toString()
            prenom = this@AddClientActivity.prenom.text.toString()
            email = this@AddClientActivity.email.text.toString()
            telephone = this@AddClientActivity.telephone.text.toString()
        }

        if (client.nom.isNullOrEmpty() || client.prenom.isNullOrEmpty() || client.email.isNullOrEmpty() || client.telephone.isNullOrEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }


        sendClientDataWithSize(client)
        Toast.makeText(this, "Client added successfully!", Toast.LENGTH_SHORT).show()

        measureAndSendData("json_10kb.json", "json")
        measureAndSendData("json_100kb.json", "json")
        measureAndSendData("json_500kb.json", "json")
        measureAndSendData("json_1000kb.json", "json")
        measureAndSendData("xml_10kb.xml", "xml")
        measureAndSendData("xml_100kb.xml", "xml")
    }
}
