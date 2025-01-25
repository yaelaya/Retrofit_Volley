package ma.ensa.retrofitreservation;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ma.ensa.retrofitreservation.adapters.ClientSpinnerAdapter;
import ma.ensa.retrofitreservation.models.Chambre;
import ma.ensa.retrofitreservation.models.Client;
import ma.ensa.retrofitreservation.models.Reservation;
import ma.ensa.retrofitreservation.network.RetrofitInstance;
import ma.ensa.retrofitreservation.service.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddReservationActivity extends AppCompatActivity {

    Button btnAddReservation;
    TextView startDate, endDate;  // Utilisation de TextView pour afficher la date
    Spinner clientSpinner, chambreSpinner;  // Spinners pour le client et la chambre
    private String Tag = "Reservation";
    private ApiInterface apiInterface;  // Déclaration de l'interface API
    private List<Client> clientsList = new ArrayList<>();  // Liste des clients
    private List<Chambre> chambresList = new ArrayList<>();  // Liste des chambres

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        // Initialisation des vues
        btnAddReservation = findViewById(R.id.btnAddReservation);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        clientSpinner = findViewById(R.id.clientSpinner);  // Spinner pour le client
        chambreSpinner = findViewById(R.id.chambreSpinner); // Spinner pour la chambre

        // Récupérer les clients via Retrofit
        getClients();

        // Récupérer les chambres via Retrofit
        getChambres();

        // Action du bouton pour ajouter une réservation
        btnAddReservation.setOnClickListener(v -> createReservation());

        // Ouvrir le DatePicker lorsque l'utilisateur clique sur la date de début
        startDate.setOnClickListener(v -> showDatePickerDialog(startDate));

        // Ouvrir le DatePicker lorsque l'utilisateur clique sur la date de fin
        endDate.setOnClickListener(v -> showDatePickerDialog(endDate));
    }

    private void showDatePickerDialog(TextView dateField) {
        // Obtenir la date actuelle
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Créer un DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Afficher la date sélectionnée dans le TextView
                    String selectedDate = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateField.setText(selectedDate);
                },
                year, month, day);

        // Afficher le DatePickerDialog
        datePickerDialog.show();
    }

    private void createReservation() {
        // Créer un objet Reservation
        Reservation reservation = new Reservation();

        // Récupérer les valeurs des champs
        reservation.setStartDate(startDate.getText().toString());
        reservation.setEndDate(endDate.getText().toString());
        reservation.setClient((Client) clientSpinner.getSelectedItem());
        reservation.setChambre((Chambre) chambreSpinner.getSelectedItem());

        // Envoi de la réservation à l'API
        Call<Reservation> call = RetrofitInstance.getApi().createReservation(reservation);

        call.enqueue(new Callback<Reservation>() {
            @Override
            public void onResponse(Call<Reservation> call, Response<Reservation> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddReservationActivity.this, "Réservation ajoutée avec succès", Toast.LENGTH_SHORT).show();
                    Log.d(Tag, response.toString());
                } else {
                    Toast.makeText(AddReservationActivity.this, "Erreur lors de l'ajout de la réservation.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Reservation> call, Throwable t) {
                Toast.makeText(AddReservationActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getClients() {
        // Créer un appel Retrofit pour obtenir les clients
        Call<List<Client>> call = RetrofitInstance.getApi().getAllClients();

        call.enqueue(new Callback<List<Client>>() {
            @Override
            public void onResponse(Call<List<Client>> call, Response<List<Client>> response) {
                if (response.isSuccessful()) {
                    clientsList = response.body();

                    // Adapter personnalisé pour le Spinner
                    ClientSpinnerAdapter clientAdapter = new ClientSpinnerAdapter(AddReservationActivity.this, clientsList);
                    clientSpinner.setAdapter(clientAdapter);
                } else {
                    Toast.makeText(AddReservationActivity.this, "Erreur lors de la récupération des clients", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Client>> call, Throwable t) {
                Toast.makeText(AddReservationActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getChambres() {
        // Créer un appel Retrofit pour obtenir les chambres
        Call<List<Chambre>> call = RetrofitInstance.getApi().getAllChambres();

        call.enqueue(new Callback<List<Chambre>>() {
            @Override
            public void onResponse(Call<List<Chambre>> call, Response<List<Chambre>> response) {
                if (response.isSuccessful()) {
                    List<Chambre> chambres = response.body();

                    // Remplir le Spinner avec les chambres récupérées
                    ArrayAdapter<Chambre> chambreAdapter = new ArrayAdapter<>(AddReservationActivity.this,
                            android.R.layout.simple_spinner_item, chambres);
                    chambreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    chambreSpinner.setAdapter(chambreAdapter);
                } else {
                    Toast.makeText(AddReservationActivity.this, "Erreur lors de la récupération des chambres", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Chambre>> call, Throwable t) {
                Toast.makeText(AddReservationActivity.this, "Erreur : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}