package ma.ensa.retrofitreservation.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ma.ensa.retrofitreservation.models.Client;

// Classe d'adaptateur personnalisé pour le Spinner
public class ClientSpinnerAdapter extends ArrayAdapter<Client> {

    public ClientSpinnerAdapter(Context context, List<Client> clients) {
        super(context, android.R.layout.simple_spinner_item, clients);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getNom() + " " + getItem(position).getPrenom()); // Afficher nom et prénom
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = view.findViewById(android.R.id.text1);
        textView.setText(getItem(position).getNom() + " " + getItem(position).getPrenom()); // Afficher nom et prénom
        return view;
    }
}
