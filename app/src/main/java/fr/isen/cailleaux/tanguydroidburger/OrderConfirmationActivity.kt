package fr.isen.cailleaux.tanguydroidburger

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding

class OrderConfirmationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Créer un LinearLayout comme conteneur principal
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16)  // en pixels
        }

        // Créer un TextView pour afficher les détails de la commande
        val detailsTextView = TextView(this).apply {
            textSize = 16f  // taille du texte en SP
            text = intent.getStringExtra("orderDetails") ?: "Aucun détail fourni"
        }

        // Ajouter le TextView au LinearLayout
        layout.addView(detailsTextView)

        // Définir le LinearLayout comme vue de l'activité
        setContentView(layout)
    }
}
