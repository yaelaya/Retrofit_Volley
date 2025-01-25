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

import ma.ensa.retrofitreservation.adapters.ReservationAdapter;
import ma.ensa.retrofitreservation.models.Reservation;
import ma.ensa.retrofitreservation.network.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListReservationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private List<Reservation> reservations = new ArrayList<>(); // Liste des réservations
    private Button buttonAddReservation;  // Déclarer le bouton

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_reservation);

        recyclerView = findViewById(R.id.list_reservations);

        // Initialiser le RecyclerView avec un LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        buttonAddReservation = findViewById(R.id.button_add_reservation);  // Initialiser le bouton

        // Initialiser l'adaptateur
        adapter = new ReservationAdapter(reservations);
        recyclerView.setAdapter(adapter);  // Assigner l'adaptateur au RecyclerView

        // Récupérer la liste des réservations et la mettre à jour
        getAllReservations();
        // Configurer le clic sur le bouton pour ouvrir AddClientActivity
        buttonAddReservation.setOnClickListener(v -> {
            // Lancer AddClientActivity
            Intent intent = new Intent(ListReservationActivity.this, AddReservationActivity.class);
            startActivity(intent);
        });
        // Ajouter le swipe-to-delete
        setUpSwipeToDelete();
    }

    private void getAllReservations() {
        Call<List<Reservation>> call = RetrofitInstance.getApi().getAllReservations();

        call.enqueue(new Callback<List<Reservation>>() {
            @Override
            public void onResponse(Call<List<Reservation>> call, Response<List<Reservation>> response) {
                if (response.isSuccessful()) {
                    List<Reservation> fetchedReservations = response.body();
                    if (fetchedReservations != null && !fetchedReservations.isEmpty()) {
                        reservations.clear();
                        reservations.addAll(fetchedReservations);
                        adapter.notifyDataSetChanged();  // Mettre à jour l'adaptateur
                    } else {
                        Toast.makeText(ListReservationActivity.this, "Aucune réservation trouvée.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListReservationActivity.this, "Erreur de réponse : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Reservation>> call, Throwable t) {
                Toast.makeText(ListReservationActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteReservation(long reservationId, int position) {
        // Appel à l'API pour supprimer la réservation
        Call<Void> call = RetrofitInstance.getApi().deleteReservation(reservationId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Si la suppression a réussi, supprimer la réservation de la liste et mettre à jour l'adaptateur
                    reservations.remove(position);  // Supprimer la réservation de la liste
                    adapter.notifyItemRemoved(position);  // Notifier l'adaptateur de la suppression
                    Toast.makeText(ListReservationActivity.this, "Réservation supprimée", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListReservationActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ListReservationActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                Reservation reservation = reservations.get(position);

                // Afficher une boîte de dialogue de confirmation avant de supprimer
                new android.app.AlertDialog.Builder(ListReservationActivity.this)
                        .setMessage("Voulez-vous vraiment supprimer cette réservation ?")
                        .setCancelable(false)
                        .setPositiveButton("Oui", (dialog, id) -> {
                            // Si l'utilisateur confirme, supprimer la réservation
                            deleteReservation(reservation.getId(), position);
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