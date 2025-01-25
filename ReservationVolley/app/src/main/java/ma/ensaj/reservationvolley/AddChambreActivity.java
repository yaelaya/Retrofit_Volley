package ma.ensaj.reservationvolley;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import ma.ensaj.reservationvolley.models.DispoChambre;
import ma.ensaj.reservationvolley.models.TypeChambre;
import ma.ensaj.reservationvolley.network.VolleySingleton;


public class AddChambreActivity extends AppCompatActivity {

    private Button btnAddChambre;
    private EditText prix;
    private Spinner typeChambreSpinner;
    private Spinner dispoChambreSpinner;
    private final String Tag = "Chambre";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chambre);

        // Initialisation des vues
        btnAddChambre = findViewById(R.id.btnAddChambre);
        prix = findViewById(R.id.prix);
        typeChambreSpinner = findViewById(R.id.typeChambreSpinner);  // Initialisation du Spinner pour le type de chambre
        dispoChambreSpinner = findViewById(R.id.dispoChambreSpinner); // Initialisation du Spinner pour la disponibilité de la chambre

        // Remplir les Spinner avec les valeurs des enums
        ArrayAdapter<TypeChambre> typeChambreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TypeChambre.values()); // Utilisation de l'ArrayAdapter pour remplir le Spinner
        typeChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeChambreSpinner.setAdapter(typeChambreAdapter); // Affectation de l'adaptateur au Spinner

        ArrayAdapter<DispoChambre> dispoChambreAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, DispoChambre.values()); // Utilisation de l'ArrayAdapter pour le deuxième Spinner
        dispoChambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dispoChambreSpinner.setAdapter(dispoChambreAdapter); // Affectation de l'adaptateur au Spinner

        // Action du bouton pour ajouter une chambre
        btnAddChambre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createChambre();
            }
        });
    }

    private void createChambre() {
        JSONObject chambre = new JSONObject();
        try {
            chambre.put("typeChambre", typeChambreSpinner.getSelectedItem().toString());
            chambre.put("prix", Double.parseDouble(prix.getText().toString()));
            chambre.put("dispoChambre", dispoChambreSpinner.getSelectedItem().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur dans les données de la chambre", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://192.168.0.217:8082/api/chambres";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, chambre,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(AddChambreActivity.this, "Chambre ajoutée avec succès", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.VolleyError error) {
                        Toast.makeText(AddChambreActivity.this, "Erreur : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add request to the queue
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }
}
