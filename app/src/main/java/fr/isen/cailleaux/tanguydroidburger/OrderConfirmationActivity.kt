package fr.isen.cailleaux.tanguydroidburger

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class OrderConfirmationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Création du layout principal
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
        }

        // Ajout d'un TextView pour le nom du restaurant
        val restaurantName = TextView(this).apply {
            text = "TanguyDroid Burger"
            textSize = 24f
        }

        // Ajout d'un TextView pour la confirmation
        val confirmationMessage = TextView(this).apply {
            text = "Votre commande a été enregistrée avec succès!"
            textSize = 20f
        }

        // Ajout du conteneur pour les commandes passées
        val ordersContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        // Ajouter les vues au layout principal
        mainLayout.addView(restaurantName)
        mainLayout.addView(confirmationMessage)
        mainLayout.addView(ordersContainer)

        setContentView(mainLayout)

        // Appel pour récupérer les commandes passées
        fetchPastOrders(ordersContainer)
    }

    private fun fetchPastOrders(container: LinearLayout) {
        val client = OkHttpClient()
        val requestBody = FormBody.Builder()
            .add("id_shop", "1")
            .add("id_user", "355")
            .build()
        val request = Request.Builder()
            .url("http://test.api.catering.bluecodegames.com/listorders")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(applicationContext, "Failed to fetch orders: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    runOnUiThread {
                        displayPastOrders(responseData, container)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(applicationContext, "Error fetching orders", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun displayPastOrders(data: String?, container: LinearLayout) {
        // Ici vous devrez parser le JSON et créer des vues pour chaque commande
        // Pour simplifier, on suppose que data contient une chaîne simple.
        val orderView = TextView(this).apply {
            text = data ?: "No past orders"
            textSize = 18f
        }
        container.addView(orderView)
    }
}
