package ma.ensa.retrofitreservation;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ma.ensa.retrofitreservation.models.Chambre;
import ma.ensa.retrofitreservation.models.DispoChambre;
import ma.ensa.retrofitreservation.models.TypeChambre;
import ma.ensa.retrofitreservation.network.RetrofitInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddChambreActivity extends AppCompatActivity {

    Button btnAddChambre;
    EditText prix;
    Spinner typeChambreSpinner, dispoChambreSpinner; // Déclaration des Spinners
    private String Tag = "Chambre";

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
        btnAddChambre.setOnClickListener(v -> createChambre());
    }

    private void createChambre() {
        Chambre chambre = new Chambre();
        // Récupérer les valeurs sélectionnées dans les Spinners
        chambre.setTypeChambre(TypeChambre.valueOf(typeChambreSpinner.getSelectedItem().toString()));
        chambre.setPrix(Double.parseDouble(prix.getText().toString()));
        chambre.setDispoChambre(DispoChambre.valueOf(dispoChambreSpinner.getSelectedItem().toString()));

        // Envoi de la chambre à l'API
        Call<Chambre> call = RetrofitInstance.getApi().createChambre(chambre);

        call.enqueue(new Callback<Chambre>() {
            @Override
            public void onResponse(Call<Chambre> call, Response<Chambre> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddChambreActivity.this, "Chambre ajoutée avec succès", Toast.LENGTH_SHORT).show();
                    Log.d(Tag, response.toString());
                } else {
                    Toast.makeText(AddChambreActivity.this, "Erreur lors de l'ajout de la chambre.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Chambre> call, Throwable t) {
                Toast.makeText(AddChambreActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
