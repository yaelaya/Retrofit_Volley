package ma.ensaj.reservationvolley_kotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import ma.ensaj.reservationvolley_kotlin.models.Chambre
import ma.ensaj.reservationvolley_kotlin.models.DispoChambre
import ma.ensaj.reservationvolley_kotlin.models.TypeChambre
import ma.ensaj.reservationvolley_kotlin.network.VolleySingleton
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback

class AddChambreActivity : AppCompatActivity() {

    private lateinit var btnAddChambre: Button
    private lateinit var prix: EditText
    private lateinit var typeChambreSpinner: Spinner
    private lateinit var dispoChambreSpinner: Spinner // Déclaration des Spinners
    private val Tag = "Chambre"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_chambre)

        // Initialisation des vues
        btnAddChambre = findViewById(R.id.btnAddChambre)
        prix = findViewById(R.id.prix)
        typeChambreSpinner = findViewById(R.id.typeChambreSpinner)  // Initialisation du Spinner pour le type de chambre
        dispoChambreSpinner = findViewById(R.id.dispoChambreSpinner) // Initialisation du Spinner pour la disponibilité de la chambre

        // Remplir les Spinner avec les valeurs des enums
        val typeChambreAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, TypeChambre.values()) // Utilisation de l'ArrayAdapter pour remplir le Spinner
        typeChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeChambreSpinner.adapter = typeChambreAdapter // Affectation de l'adaptateur au Spinner

        val dispoChambreAdapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, DispoChambre.values()) // Utilisation de l'ArrayAdapter pour le deuxième Spinner
        dispoChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dispoChambreSpinner.adapter = dispoChambreAdapter // Affectation de l'adaptateur au Spinner

        // Action du bouton pour ajouter une chambre
        btnAddChambre.setOnClickListener { createChambre() }
    }

    private fun createChambre() {
        val chambre = JSONObject()
        try {
            chambre.put("typeChambre", typeChambreSpinner.selectedItem.toString())
            chambre.put("prix", prix.text.toString().toDouble())
            chambre.put("dispoChambre", dispoChambreSpinner.selectedItem.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur dans les données de la chambre", Toast.LENGTH_SHORT).show()
            return
        }

        val url = "http://192.168.0.217:8082/api/chambres"

        val request = JsonObjectRequest(
            Request.Method.POST, url, chambre,
            { response ->
                Toast.makeText(this, "Chambre ajoutée avec succès", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Add request to the queue
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

}
