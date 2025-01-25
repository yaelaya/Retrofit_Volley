package ma.ensaj.reservationretrofit_kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.reservationretrofit_kotlin.ListChambreActivity
import ma.ensaj.reservationretrofit_kotlin.R
import ma.ensaj.reservationretrofit_kotlin.models.Chambre
import java.lang.String

class ChambreAdapter(
    private val chambres: List<Chambre>,
    listChambreActivity: ListChambreActivity
) : RecyclerView.Adapter<ChambreAdapter.ChambreViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChambreViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chambre, parent, false)
        return ChambreViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChambreViewHolder, position: Int) {
        val chambre = chambres[position]
        holder.chambreName.text = chambre.typeChambre.toString() // Affichage du type de chambre
        holder.chambrePrice.text = String.format("Prix: %.2f", chambre.prix) // Affichage du prix
        holder.chambreAvailability.text = chambre.dispoChambre.toString() // Affichage de la disponibilité
    }

    override fun getItemCount(): Int {
        return chambres.size
    }

    class ChambreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chambreName: TextView = itemView.findViewById(R.id.chambre_name)
        val chambrePrice: TextView = itemView.findViewById(R.id.chambre_price)
        val chambreAvailability: TextView = itemView.findViewById(R.id.chambre_availability)
    }
}
