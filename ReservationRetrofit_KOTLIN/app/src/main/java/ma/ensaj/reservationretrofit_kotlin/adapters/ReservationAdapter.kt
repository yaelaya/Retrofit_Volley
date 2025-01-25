package ma.ensaj.reservationretrofit_kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.reservationretrofit_kotlin.ListReservationActivity
import ma.ensaj.reservationretrofit_kotlin.R
import ma.ensaj.reservationretrofit_kotlin.models.Reservation
import java.lang.String
import kotlin.Int
import kotlin.plus
import kotlin.toString
class ReservationAdapter(
    private val reservations: List<Reservation>
) : RecyclerView.Adapter<ReservationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.clientName.text = "${reservation.client?.nom} ${reservation.client?.prenom}"
        holder.roomType.text = reservation.chambre?.typeChambre.toString()
        holder.roomPrice.text = reservation.chambre?.prix.toString()
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.client_name)
        val roomType: TextView = itemView.findViewById(R.id.room_type)
        val roomPrice: TextView = itemView.findViewById(R.id.room_price)
    }
}
