package ma.ensa.retrofitreservation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.retrofitreservation.ListChambreActivity;
import ma.ensa.retrofitreservation.R;
import ma.ensa.retrofitreservation.models.Chambre;

public class ChambreAdapter extends RecyclerView.Adapter<ChambreAdapter.ChambreViewHolder> {

    private List<Chambre> chambres;

    public ChambreAdapter(List<Chambre> chambres, ListChambreActivity listChambreActivity) {
        this.chambres = chambres;
    }

    @Override
    public ChambreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chambre, parent, false);
        return new ChambreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChambreViewHolder holder, int position) {
        Chambre chambre = chambres.get(position);
        holder.chambreName.setText(chambre.getTypeChambre().toString()); // Affichage du type de chambre
        holder.chambrePrice.setText(String.format("Prix: %.2f", chambre.getPrix())); // Affichage du prix
        holder.chambreAvailability.setText(chambre.getDispoChambre().toString()); // Affichage de la disponibilité
    }

    @Override
    public int getItemCount() {
        return chambres.size();
    }

    public static class ChambreViewHolder extends RecyclerView.ViewHolder {
        TextView chambreName, chambrePrice, chambreAvailability;

        public ChambreViewHolder(View itemView) {
            super(itemView);
            chambreName = itemView.findViewById(R.id.chambre_name);
            chambrePrice = itemView.findViewById(R.id.chambre_price);
            chambreAvailability = itemView.findViewById(R.id.chambre_availability);
        }
    }
}
