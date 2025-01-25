package ma.ensaj.reservationvolley;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import ma.ensaj.reservationvolley.adapters.ReservationAdapter;
import ma.ensaj.reservationvolley.models.Reservation;
import ma.ensaj.reservationvolley.models.Chambre;
import ma.ensaj.reservationvolley.models.Client;
import ma.ensaj.reservationvolley.models.DispoChambre;
import ma.ensaj.reservationvolley.models.TypeChambre;

public class ListReservationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private final java.util.List<Reservation> reservations = new java.util.ArrayList<>();
    private Button buttonAddReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_reservation);

        recyclerView = findViewById(R.id.list_reservations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buttonAddReservation = findViewById(R.id.button_add_reservation);
        adapter = new ReservationAdapter(reservations);
        recyclerView.setAdapter(adapter);

        getAllReservations();

        buttonAddReservation.setOnClickListener(v -> {
            Intent intent = new Intent(ListReservationActivity.this, AddReservationActivity.class);
            startActivity(intent);
        });

        setUpSwipeToDelete();
    }

    private void getAllReservations() {
        String url = "http://192.168.0.217/api/reservations";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    reservations.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject item = response.getJSONObject(i);
                            Reservation reservation = new Reservation();
                            reservation.setId(item.optLong("id", -1)); // Handle missing or null values
                            reservation.setStartDate(item.optString("startDate", null));
                            reservation.setEndDate(item.optString("endDate", null));

                            // Parse client object
                            JSONObject clientJson = item.optJSONObject("client");
                            if (clientJson != null) {
                                Client client = new Client(
                                        clientJson.optLong("id", -1),
                                        clientJson.optString("nom", null),
                                        clientJson.optString("prenom", null),
                                        clientJson.optString("email", null),
                                        clientJson.optString("telephone", null)
                                );
                                reservation.setClient(client);
                            }

                            // Parse chambre object
                            JSONObject chambreJson = item.optJSONObject("chambre");
                            if (chambreJson != null) {
                                Chambre chambre = new Chambre();
                                chambre.setId(chambreJson.optLong("id", -1));
                                chambre.setPrix(chambreJson.optDouble("prix", 0.0));

                                // Handle Enum parsing
                                try {
                                    TypeChambre typeChambre = TypeChambre.valueOf(chambreJson.optString("typeChambre", ""));
                                    chambre.setTypeChambre(typeChambre);
                                } catch (IllegalArgumentException e) {
                                    // Handle invalid TypeChambre enum value
                                    chambre.setTypeChambre(null);
                                }

                                try {
                                    DispoChambre dispoChambre = DispoChambre.valueOf(chambreJson.optString("dispoChambre", ""));
                                    chambre.setDispoChambre(dispoChambre);
                                } catch (IllegalArgumentException e) {
                                    // Handle invalid DispoChambre enum value
                                    chambre.setDispoChambre(null);
                                }

                                reservation.setChambre(chambre);
                            }

                            reservations.add(reservation);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(ListReservationActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void deleteReservation(long reservationId, int position) {
        String url = "http://192.168.0.217/api/reservations/" + reservationId;
        StringRequest request = new StringRequest(
                Request.Method.DELETE, url,
                response -> {
                    reservations.remove(position);
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(ListReservationActivity.this, "Réservation supprimée", Toast.LENGTH_SHORT).show();
                },
                error -> Toast.makeText(ListReservationActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show()
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
                Reservation reservation = reservations.get(position);

                // Check if reservation id is not null or -1
                if (reservation.getId() != null && reservation.getId() != -1) {
                    new AlertDialog.Builder(ListReservationActivity.this)
                            .setMessage("Voulez-vous vraiment supprimer cette réservation ?")
                            .setCancelable(false)
                            .setPositiveButton("Oui", (dialog, which) -> deleteReservation(reservation.getId(), position))
                            .setNegativeButton("Non", (dialog, which) -> adapter.notifyItemChanged(position))
                            .create()
                            .show();
                } else {
                    // Handle case where id is null or -1 if necessary
                    Toast.makeText(ListReservationActivity.this, "ID de réservation invalide", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position);
                }
            }

        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
}
