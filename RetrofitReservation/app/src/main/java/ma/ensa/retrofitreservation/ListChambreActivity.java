package ma.ensa.retrofitreservation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;

import ma.ensa.retrofitreservation.adapters.ChambreAdapter;
import ma.ensa.retrofitreservation.models.Chambre;
import ma.ensa.retrofitreservation.network.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListChambreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChambreAdapter adapter;
    private List<Chambre> chambres = new ArrayList<>(); // Liste des chambres
    private Button buttonAddChambre;  // Déclarer le bouton

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chambre);

        recyclerView = findViewById(R.id.list_chambres);
        buttonAddChambre = findViewById(R.id.button_add_chambre);  // Initialiser le bouton

        // Initialiser le RecyclerView avec un LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Récupérer la liste des chambres et la mettre à jour
        getAllChambres();

        // Configurer le clic sur le bouton pour ouvrir AddChambreActivity
        buttonAddChambre.setOnClickListener(v -> {
            // Lancer AddChambreActivity
            Intent intent = new Intent(ListChambreActivity.this, AddChambreActivity.class);
            startActivity(intent);
        });

        // Ajouter le swipe-to-delete
        setUpSwipeToDelete();
    }

    private void getAllChambres() {
        // Appel à l'API pour récupérer toutes les chambres
        Call<List<Chambre>> call = RetrofitInstance.getApi().getAllChambres();

        call.enqueue(new Callback<List<Chambre>>() {
            @Override
            public void onResponse(Call<List<Chambre>> call, Response<List<Chambre>> response) {
                if (response.isSuccessful()) {
                    List<Chambre> fetchedChambres = response.body();
                    if (fetchedChambres != null && !fetchedChambres.isEmpty()) {
                        chambres.clear();  // Effacer la liste actuelle
                        chambres.addAll(fetchedChambres);  // Ajouter les chambres récupérées
                        adapter = new ChambreAdapter(chambres, ListChambreActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Si la liste des chambres est vide
                        Toast.makeText(ListChambreActivity.this, "Aucune chambre trouvée.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Si la réponse de l'API échoue
                    Toast.makeText(ListChambreActivity.this, "Erreur lors de la récupération des chambres.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Chambre>> call, Throwable t) {
                // Si la requête échoue
                Toast.makeText(ListChambreActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteChambre(long chambreId, int position) {
        // Appel à l'API pour supprimer la chambre
        Call<Void> call = RetrofitInstance.getApi().deleteChambre(chambreId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Si la suppression a réussi, supprimer la chambre de la liste et mettre à jour l'adaptateur
                    chambres.remove(position);  // Supprimer la chambre de la liste
                    adapter.notifyItemRemoved(position);  // Notifier l'adaptateur de la suppression
                    Toast.makeText(ListChambreActivity.this, "Chambre supprimée", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListChambreActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ListChambreActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;  // Nous ne gérons pas le déplacement des items
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Récupérer la position de l'élément qui a été swipé
                int position = viewHolder.getAdapterPosition();
                Chambre chambre = chambres.get(position);

                // Afficher une boîte de dialogue de confirmation avant de supprimer
                new android.app.AlertDialog.Builder(ListChambreActivity.this)
                        .setMessage("Voulez-vous vraiment supprimer cette chambre ?")
                        .setCancelable(false)
                        .setPositiveButton("Oui", (dialog, id) -> {
                            // Si l'utilisateur confirme, supprimer la chambre
                            deleteChambre(chambre.getId(), position);
                        })
                        .setNegativeButton("Non", (dialog, id) -> {
                            // Si l'utilisateur annule, restaurer l'élément dans la liste
                            adapter.notifyItemChanged(position);
                        })
                        .create()
                        .show();
            }
        };

        // Attacher l'ItemTouchHelper au RecyclerView
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
}
