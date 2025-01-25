package ma.ensa.retrofitreservation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.retrofitreservation.ListReservationActivity;
import ma.ensa.retrofitreservation.R;
import ma.ensa.retrofitreservation.models.Reservation;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {

    private List<Reservation> reservations;
    private ListReservationActivity activity;

    public ReservationAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.clientName.setText(reservation.getClient().getNom() + " " + reservation.getClient().getPrenom());
        holder.roomType.setText(reservation.getChambre().getTypeChambre().toString());
        holder.roomPrice.setText(String.valueOf(reservation.getChambre().getPrix()));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView clientName, roomType, roomPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            clientName = itemView.findViewById(R.id.client_name);
            roomType = itemView.findViewById(R.id.room_type);
            roomPrice = itemView.findViewById(R.id.room_price);
        }
    }
}
