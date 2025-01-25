package ma.ensaj.reservationretrofit_kotlin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.reservationretrofit_kotlin.adapters.ChambreAdapter
import ma.ensaj.reservationretrofit_kotlin.models.Chambre
import ma.ensaj.reservationretrofit_kotlin.network.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        // Appel à l'API pour récupérer toutes les chambres
        val call = RetrofitInstance.getApi().getAllChambres()

        call.enqueue(object : Callback<List<Chambre>> {
            override fun onResponse(call: Call<List<Chambre>>, response: Response<List<Chambre>>) {
                if (response.isSuccessful) {
                    val fetchedChambres = response.body()
                    if (!fetchedChambres.isNullOrEmpty()) {
                        chambres.clear()  // Effacer la liste actuelle
                        chambres.addAll(fetchedChambres)  // Ajouter les chambres récupérées
                        adapter = ChambreAdapter(chambres, this@ListChambreActivity)
                        recyclerView.adapter = adapter
                    } else {
                        // Si la liste des chambres est vide
                        Toast.makeText(this@ListChambreActivity, "Aucune chambre trouvée.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Si la réponse de l'API échoue
                    Toast.makeText(this@ListChambreActivity, "Erreur lors de la récupération des chambres.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Chambre>>, t: Throwable) {
                // Si la requête échoue
                Toast.makeText(this@ListChambreActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteChambre(chambreId: Long, position: Int) {
        // Appel à l'API pour supprimer la chambre
        val call = RetrofitInstance.getApi().deleteChambre(chambreId)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Si la suppression a réussi, supprimer la chambre de la liste et mettre à jour l'adaptateur
                    chambres.removeAt(position)  // Supprimer la chambre de la liste
                    adapter.notifyItemRemoved(position)  // Notifier l'adaptateur de la suppression
                    Toast.makeText(this@ListChambreActivity, "Chambre supprimée", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ListChambreActivity, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@ListChambreActivity, "Erreur : ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
