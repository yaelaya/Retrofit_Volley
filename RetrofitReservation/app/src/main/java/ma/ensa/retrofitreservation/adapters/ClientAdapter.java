package ma.ensa.retrofitreservation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.retrofitreservation.ListClientActivity;
import ma.ensa.retrofitreservation.R;
import ma.ensa.retrofitreservation.models.Client;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private List<Client> clients;

    public ClientAdapter(List<Client> clients, ListClientActivity listClientActivity) {
        this.clients = clients;
    }

    @Override
    public ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClientViewHolder holder, int position) {
        Client client = clients.get(position);
        holder.clientName.setText(client.getNom() + " " + client.getPrenom()); // Affichage du nom et prénom
        holder.clientEmail.setText(client.getEmail()); // Affichage de l'email
        holder.clientPhone.setText(client.getTelephone()); // Affichage du téléphone
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    public static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView clientName, clientEmail, clientPhone;

        public ClientViewHolder(View itemView) {
            super(itemView);
            clientName = itemView.findViewById(R.id.client_name);
            clientEmail = itemView.findViewById(R.id.client_email);
            clientPhone = itemView.findViewById(R.id.client_phone);
        }
    }
}
