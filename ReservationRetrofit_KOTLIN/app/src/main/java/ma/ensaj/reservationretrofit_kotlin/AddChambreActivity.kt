package ma.ensaj.reservationretrofit_kotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ma.ensaj.reservationretrofit_kotlin.models.Chambre
import ma.ensaj.reservationretrofit_kotlin.models.DispoChambre
import ma.ensaj.reservationretrofit_kotlin.models.TypeChambre
import ma.ensaj.reservationretrofit_kotlin.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        val chambre = Chambre()
        // Récupérer les valeurs sélectionnées dans les Spinners
        chambre.typeChambre = TypeChambre.valueOf(typeChambreSpinner.selectedItem.toString())
        chambre.prix = prix.text.toString().toDouble()
        chambre.dispoChambre = DispoChambre.valueOf(dispoChambreSpinner.selectedItem.toString())

        // Envoi de la chambre à l'API
        val call = RetrofitInstance.getApi().createChambre(chambre)

        call.enqueue(object : Callback<Chambre> {
            override fun onResponse(call: Call<Chambre>, response: Response<Chambre>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@AddChambreActivity, "Chambre ajoutée avec succès", Toast.LENGTH_SHORT).show()
                    Log.d(Tag, response.toString())
                } else {
                    Toast.makeText(this@AddChambreActivity, "Erreur lors de l'ajout de la chambre.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Chambre>, t: Throwable) {
                Toast.makeText(this@AddChambreActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
