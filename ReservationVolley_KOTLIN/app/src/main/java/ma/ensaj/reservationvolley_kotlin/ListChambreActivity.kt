package ma.ensaj.reservationvolley_kotlin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ma.ensaj.reservationvolley_kotlin.adapters.ChambreAdapter
import ma.ensaj.reservationvolley_kotlin.models.Chambre
import ma.ensaj.reservationvolley_kotlin.models.DispoChambre
import ma.ensaj.reservationvolley_kotlin.models.TypeChambre
import ma.ensaj.reservationvolley_kotlin.AddChambreActivity
import ma.ensaj.reservationvolley_kotlin.R
import ma.ensaj.reservationvolley_kotlin.network.VolleySingleton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class ListChambreActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChambreAdapter
    private var chambres = mutableListOf<Chambre>()  // Liste des chambres
    private lateinit var buttonAddChambre: Button  // Déclarer le bouton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_chambre)

        recyclerView = findViewById(R.id.list_chambres)
        buttonAddChambre = findViewById(R.id.button_add_chambre)  // Initialiser le bouton

        // Initialiser le RecyclerView avec un LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Récupérer la liste des chambres et la mettre à jour
        getAllChambres()

        // Configurer le clic sur le bouton pour ouvrir AddChambreActivity
        buttonAddChambre.setOnClickListener {
            // Lancer AddChambreActivity
            val intent = Intent(this@ListChambreActivity, AddChambreActivity::class.java)
            startActivity(intent)
        }

        // Ajouter le swipe-to-delete
        setUpSwipeToDelete()
    }

    private fun getAllChambres() {
        val url = "http://192.168.0.217:8082/api/chambres"

        val request = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                chambres.clear()
                for (i in 0 until response.length()) {
                    try {
                        val chambreJson = response.getJSONObject(i)
                        val chambre = Chambre(
                            id = chambreJson.getLong("id"),
                            typeChambre = TypeChambre.valueOf(chambreJson.getString("typeChambre")),
                            prix = chambreJson.getDouble("prix"),
                            dispoChambre = DispoChambre.valueOf(chambreJson.getString("dispoChambre"))
                        )
                        chambres.add(chambre)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                adapter = ChambreAdapter(chambres, this)
                recyclerView.adapter = adapter
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        // Add request to the queue
        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }


    private fun deleteChambre(chambreId: Long, position: Int) {
        val url = "http://192.168.0.217:8082/api/chambres/$chambreId"

        val request = StringRequest(
            Request.Method.DELETE, url,
            {
                chambres.removeAt(position)
                adapter.notifyItemRemoved(position)
                Toast.makeText(this, "Chambre supprimée", Toast.LENGTH_SHORT).show()
            },
            { error ->
                Toast.makeText(this, "Erreur : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }


    private fun setUpSwipeToDelete() {
        val simpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false  // Nous ne gérons pas le déplacement des items
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Récupérer la position de l'élément qui a été swipé
                val position = viewHolder.adapterPosition
                val chambre = chambres[position]

                // Afficher une boîte de dialogue de confirmation avant de supprimer
                AlertDialog.Builder(this@ListChambreActivity)
                    .setMessage("Voulez-vous vraiment supprimer cette chambre ?")
                    .setCancelable(false)
                    .setPositiveButton("Oui") { _, _ ->
                        // Si l'utilisateur confirme, supprimer la chambre
                        chambre.id?.let { deleteChambre(it, position) }
                    }
                    .setNegativeButton("Non") { _, _ ->
                        // Si l'utilisateur annule, restaurer l'élément dans la liste
                        adapter.notifyItemChanged(position)
                    }
                    .create()
                    .show()
            }
        }

        // Attacher l'ItemTouchHelper au RecyclerView
        ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView)
    }
}
