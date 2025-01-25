package ma.ensa.retrofitreservation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import ma.ensa.retrofitreservation.adapters.ClientAdapter;
import ma.ensa.retrofitreservation.models.Client;
import ma.ensa.retrofitreservation.network.RetrofitInstance;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListClientActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClientAdapter adapter;
    private List<Client> clients = new ArrayList<>();
    private Button buttonAddClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_client);

        recyclerView = findViewById(R.id.list_clients);
        buttonAddClient = findViewById(R.id.button_add_client);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with empty data
        adapter = new ClientAdapter(clients, this);
        recyclerView.setAdapter(adapter);

        // Fetch all clients and measure latency
        getAllClients();

        // Set up swipe-to-delete functionality
        setUpSwipeToDelete();

        buttonAddClient.setOnClickListener(v -> {
            Intent intent = new Intent(ListClientActivity.this, AddClientActivity.class);
            startActivity(intent);
        });
    }

    private void getAllClients() {
        long startTime = System.currentTimeMillis();  // Capture start time

        Call<List<Client>> call = RetrofitInstance.getApi().getAllClients();
        call.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                long endTime = System.currentTimeMillis();  // Capture end time
                long latency = endTime - startTime;  // Calculate latency
                Log.d("NetworkLatency", "GET Request Latency: " + latency + " ms");

                if (response.isSuccessful()) {
                    List<Client> fetchedClients = response.body();
                    if (fetchedClients != null && !fetchedClients.isEmpty()) {
                        clients.clear();
                        clients.addAll(fetchedClients);
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(ListClientActivity.this, "Aucun client trouvé.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ListClientActivity.this, "Erreur lors de la récupération des clients.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Toast.makeText(ListClientActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteClient(Long clientId, int position) {
        long startTime = System.currentTimeMillis();  // Capture start time

        Call<Void> call = RetrofitInstance.getApi().deleteClient(clientId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                long endTime = System.currentTimeMillis();  // Capture end time
                long latency = endTime - startTime;  // Calculate latency
                Log.d("NetworkLatency", "DELETE Request Latency: " + latency + " ms");

                if (response.isSuccessful()) {
                    clients.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(ListClientActivity.this, "Client supprimé", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListClientActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ListClientActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Client client = clients.get(position);

                new AlertDialog.Builder(ListClientActivity.this)
                        .setMessage("Voulez-vous vraiment supprimer ce client ?")
                        .setCancelable(false)
                        .setPositiveButton("Oui", (dialog, which) -> {
                            if (client.getId() != null) {
                                deleteClient(client.getId(), position);
                            }
                        })
                        .setNegativeButton("Non", (dialog, which) -> adapter.notifyItemChanged(position))
                        .create()
                        .show();
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
}vv