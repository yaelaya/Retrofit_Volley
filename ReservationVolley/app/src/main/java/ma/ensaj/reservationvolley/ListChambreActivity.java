package ma.ensaj.reservationvolley;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import ma.ensaj.reservationvolley.adapters.ChambreAdapter;
import ma.ensaj.reservationvolley.models.Chambre;
import ma.ensaj.reservationvolley.models.DispoChambre;
import ma.ensaj.reservationvolley.models.TypeChambre;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListChambreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChambreAdapter adapter;
    private java.util.List<Chambre> chambres = new java.util.ArrayList<>();
    private Button buttonAddChambre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chambre);

        recyclerView = findViewById(R.id.list_chambres);
        buttonAddChambre = findViewById(R.id.button_add_chambre);

        // Initialize RecyclerView with LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Retrieve and update the list of chambres
        getAllChambres();

        // Set up the click on the button to open AddChambreActivity
        buttonAddChambre.setOnClickListener(view -> {
            // Launch AddChambreActivity
            Intent intent = new Intent(ListChambreActivity.this, AddChambreActivity.class);
            startActivity(intent);
        });

        // Add swipe-to-delete functionality
        setUpSwipeToDelete();
    }

    private void getAllChambres() {
        String url = "http://192.168.0.217:8082/api/chambres";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    chambres.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject chambreJson = response.getJSONObject(i);
                            Chambre chambre = new Chambre(
                                    chambreJson.getLong("id"),
                                    TypeChambre.valueOf(chambreJson.getString("typeChambre")),
                                    chambreJson.getDouble("prix"),
                                    DispoChambre.valueOf(chambreJson.getString("dispoChambre"))
                            );
                            chambres.add(chambre);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter = new ChambreAdapter(chambres, this);
                    recyclerView.setAdapter(adapter);
                },
                error -> Toast.makeText(ListChambreActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        // Add request to the queue
        Volley.newRequestQueue(this).add(request);
    }

    private void deleteChambre(long chambreId, int position) {
        String url = "http://192.168.0.217:8082/api/chambres/" + chambreId;

        StringRequest request = new StringRequest(
                Request.Method.DELETE, url,
                response -> {
                    chambres.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(ListChambreActivity.this, "Chambre supprimée", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(ListChambreActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void setUpSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;  // We don't handle moving items
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Get the position of the swiped item
                int position = viewHolder.getAdapterPosition();
                Chambre chambre = chambres.get(position);

                // Show a confirmation dialog before deletion
                new AlertDialog.Builder(ListChambreActivity.this)
                        .setMessage("Voulez-vous vraiment supprimer cette chambre ?")
                        .setCancelable(false)
                        .setPositiveButton("Oui", (dialog, which) -> {
                            // If the user confirms, delete the chambre
                            deleteChambre(chambre.getId(), position);
                        })
                        .setNegativeButton("Non", (dialog, which) -> {
                            // If the user cancels, restore the item in the list
                            adapter.notifyItemChanged(position);
                        })
                        .create()
                        .show();
            }
        };

        // Attach ItemTouchHelper to the RecyclerView
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
}
