package ma.ensaj.reservationvolley_kotlin.adapters

import android.R
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ma.ensaj.reservationvolley_kotlin.models.Client

// Classe d'adaptateur personnalisé pour le Spinner
class ClientSpinnerAdapter(
    context: Context,
    clients: List<Client>
) : ArrayAdapter<Client>(context, android.R.layout.simple_spinner_item, clients) {

    init {
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = "${getItem(position)?.nom} ${getItem(position)?.prenom}" // Afficher nom et prénom
        return view
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = "${getItem(position)?.nom} ${getItem(position)?.prenom}" // Afficher nom et prénom
        return view
    }
}
