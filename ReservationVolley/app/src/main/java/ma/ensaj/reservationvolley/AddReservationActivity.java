package ma.ensaj.reservationvolley;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import ma.ensaj.reservationvolley.models.Chambre;
import ma.ensaj.reservationvolley.models.Client;
import ma.ensaj.reservationvolley.models.DispoChambre;
import ma.ensaj.reservationvolley.models.TypeChambre;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddReservationActivity extends AppCompatActivity {

    private Button btnAddReservation;
    private TextView startDate;
    private TextView endDate;
    private Spinner clientSpinner;
    private Spinner chambreSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        // Initialize UI components
        btnAddReservation = findViewById(R.id.btnAddReservation);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        clientSpinner = findViewById(R.id.clientSpinner);
        chambreSpinner = findViewById(R.id.chambreSpinner);

        // Fetch and populate spinners
        getClients();
        getChambres();

        // Handle reservation creation
        btnAddReservation.setOnClickListener(v -> createReservation());

        // Show date picker dialogs
        startDate.setOnClickListener(v -> showDatePickerDialog(startDate));
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));
    }

    private void createReservation() {
        String url = "http://192.168.0.217/api/reservations";
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("startDate", startDate.getText().toString());
            requestBody.put("endDate", endDate.getText().toString());
            requestBody.put("clientId", ((Client) clientSpinner.getSelectedItem()).getId());
            requestBody.put("chambreId", ((Chambre) chambreSpinner.getSelectedItem()).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, requestBody,
                response -> Toast.makeText(this, "Réservation ajoutée avec succès", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void getClients() {
        String url = "http://192.168.0.217/api/clients";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    List<Client> clients = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject item = response.getJSONObject(i);
                            clients.add(new Client(
                                    item.optLong("id"),
                                    item.optString("nom"),
                                    item.optString("prenom"),
                                    item.optString("email"),
                                    item.optString("telephone")
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<Client> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, clients);
                    clientSpinner.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Erreur : Impossible de charger les clients. " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void getChambres() {
        String url = "http://192.168.0.217/api/chambres";
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    List<Chambre> chambres = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject item = response.getJSONObject(i);
                            chambres.add(new Chambre(
                                    item.optLong("id"),
                                    item.optString("typeChambre") != null ? TypeChambre.valueOf(item.optString("typeChambre")) : null,
                                    item.optDouble("prix"),
                                    item.optString("dispoChambre") != null ? DispoChambre.valueOf(item.optString("dispoChambre")) : null
                            ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<Chambre> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, chambres);
                    chambreSpinner.setAdapter(adapter);
                },
                error -> Toast.makeText(this, "Erreur : Impossible de charger les chambres. " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(request);
    }

    private void showDatePickerDialog(TextView dateField) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> dateField.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
