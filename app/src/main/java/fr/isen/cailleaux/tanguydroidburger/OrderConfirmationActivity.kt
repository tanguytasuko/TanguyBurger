package fr.isen.cailleaux.tanguydroidburger

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class OrderConfirmationActivity : AppCompatActivity() {

    private lateinit var mainLayout: LinearLayout
    private lateinit var ordersContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
        }

        val restaurantName = TextView(this).apply {
            text = "TanguyDroid Burger"
            textSize = 24f
        }

        val confirmationMessage = TextView(this).apply {
            text = "Votre commande a été enregistrée avec succès!"
            textSize = 20f
        }

        ordersContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        mainLayout.apply {
            addView(restaurantName)
            addView(confirmationMessage)
            addView(ordersContainer)
        }

        setContentView(mainLayout)
        fetchPastOrders()
    }

    private fun fetchPastOrders() {
        val json = JSONObject().apply {
            put("id_shop", 1)
            put("id_user", 355)
        }

        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val request = Request.Builder()
            .url("http://test.api.catering.bluecodegames.com/listorders")
            .post(requestBody)
            .build()

        FetchOrdersTask().execute(request)
    }

    private inner class FetchOrdersTask : AsyncTask<Request, Void, String>() {
        override fun doInBackground(vararg params: Request): String {
            val client = OkHttpClient()
            try {
                client.newCall(params[0]).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                    return response.body?.string() ?: ""
                }
            } catch (e: IOException) {
                Log.e("HTTP Request", "Error fetching orders", e)
                return ""
            }
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            Log.d("API Response", "Response received: $result")
            if (result.isNotEmpty()) {
                displayPastOrders(result)
            } else {
                Toast.makeText(applicationContext, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayPastOrders(data: String) {
        try {
            val jsonObject = JSONObject(data)
            val jsonArray = jsonObject.getJSONArray("data")

            if (jsonArray.length() == 0) {
                Toast.makeText(this, "Pas de commandes à afficher", Toast.LENGTH_SHORT).show()
                return
            }

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                val messageJson = JSONObject(item.getString("message")) // Parse the nested JSON string
                val orderView = TextView(this).apply {
                    text = "Order ID: ${item.getString("id_sender")}, Burger: ${messageJson.getString("burger")}, Name: ${messageJson.getString("firstname")} ${messageJson.getString("lastname")}"
                    textSize = 18f
                }
                ordersContainer.addView(orderView)
            }
        } catch (e: JSONException) {
            Log.e("JSON Parsing", "Error parsing orders", e)
            Toast.makeText(this, "Erreur d'analyse des commandes", Toast.LENGTH_SHORT).show()
        }
    }

}
