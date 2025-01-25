package ma.ensaj.reservationvolley;

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
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import ma.ensaj.reservationvolley.adapters.ClientAdapter;
import ma.ensaj.reservationvolley.models.Client;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

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
        adapter = new ClientAdapter(clients);
        recyclerView.setAdapter(adapter);

        getAllClients();

        setUpSwipeToDelete();

        buttonAddClient.setOnClickListener(v -> {
            Intent intent = new Intent(ListClientActivity.this, AddClientActivity.class);
            startActivity(intent);
        });
    }

    private void getAllClients() {
        String url = "http://192.168.0.217:8082/api/clients";
        // Fetch all clients from the server
        requestClients(url);
    }

    private void deleteClient(Long clientId, int position) {
        String url = "http://192.168.0.217:8082/api/clients/" + clientId;
        // Delete the client from the server
        requestDelete(url, position);
    }

    private void requestClients(String url) {
        long startTime = System.currentTimeMillis();  // Capture start time

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    long endTime = System.currentTimeMillis();  // Capture end time
                    long latency = endTime - startTime;  // Calculate latency
                    Log.d("NetworkLatency", "GET Request Latency: " + latency + " ms");

                    // Parse the response and update the clients list
                    clients.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject clientJson = response.getJSONObject(i);
                            Client client = new Client(
                                    clientJson.getLong("id"),
                                    clientJson.getString("nom"),
                                    clientJson.getString("prenom"),
                                    clientJson.getString("email"),
                                    clientJson.getString("telephone")
                            );
                            clients.add(client);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Toast.makeText(ListClientActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void requestDelete(String url, int position) {
        long startTime = System.currentTimeMillis();  // Capture start time

        StringRequest request = new StringRequest(
                Request.Method.DELETE, url,
                response -> {
                    long endTime = System.currentTimeMillis();  // Capture end time
                    long latency = endTime - startTime;  // Calculate latency
                    Log.d("NetworkLatency", "DELETE Request Latency: " + latency + " ms");

                    Toast.makeText(ListClientActivity.this, "Client supprimé", Toast.LENGTH_SHORT).show();
                    clients.remove(position);
                    adapter.notifyItemRemoved(position);
                },
                error -> {
                    Toast.makeText(ListClientActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
        );
        Volley.newRequestQueue(this).add(request);
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
}
