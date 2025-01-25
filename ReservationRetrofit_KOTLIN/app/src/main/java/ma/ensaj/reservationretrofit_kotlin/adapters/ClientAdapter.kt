package ma.ensaj.reservationretrofit_kotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ma.ensaj.reservationretrofit_kotlin.ListClientActivity
import ma.ensaj.reservationretrofit_kotlin.R
import ma.ensaj.reservationretrofit_kotlin.models.Client

import android.util.Log // Correct Log import

class ClientAdapter(
    private val clients: List<Client>
) : RecyclerView.Adapter<ClientAdapter.ClientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_client, parent, false)
        return ClientViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
        val client = clients[position]
        Log.d("ClientAdapter", "Binding client at position $position: ${client.nom} ${client.prenom}")

        holder.clientName.text = "${client.nom} ${client.prenom}"
        holder.clientEmail.text = client.email
        holder.clientPhone.text = client.telephone
    }


    override fun getItemCount(): Int {
        return clients.size
    }

    class ClientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clientName: TextView = itemView.findViewById(R.id.client_name)
        val clientEmail: TextView = itemView.findViewById(R.id.client_email)
        val clientPhone: TextView = itemView.findViewById(R.id.client_phone)
    }
}
