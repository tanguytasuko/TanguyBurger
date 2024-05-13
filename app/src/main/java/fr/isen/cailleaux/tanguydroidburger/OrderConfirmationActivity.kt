package fr.isen.cailleaux.tanguydroidburger

import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
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
            gravity = Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(resources.getColor(R.color.vlg))
        }

        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        ordersContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val logo = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                500,
                400
            ).apply {
                topMargin = 12.dpToPx()
                gravity = Gravity.CENTER_HORIZONTAL
            }
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.burger)
        }

        val restaurantName = TextView(this).apply {
            text = "TanguyDroid Burger"
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(resources.getColor(R.color.custom_cyan))
            gravity = Gravity.CENTER
        }

        val confirmationMessage = TextView(this).apply {
            text = "Votre commande a été enregistrée avec succès!"
            textSize = 20f
            setTextColor(resources.getColor(R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16.dpToPx()
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

        val historyTitle = TextView(this).apply {
            text = "Historique des commandes"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16.dpToPx()
                bottomMargin = 12.dpToPx()
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

        ordersContainer.apply {
            addView(logo)
            addView(restaurantName)
            addView(confirmationMessage)
            addView(historyTitle)
        }

        scrollView.addView(ordersContainer)
        mainLayout.addView(scrollView)

        setContentView(mainLayout)
        fetchPastOrders()
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun fetchPastOrders() {
        val json = JSONObject().apply {
            put("id_shop", "1")
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
                val messageJson = JSONObject(item.getString("message"))

                val orderView = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(12.dpToPx(), 12.dpToPx(), 12.dpToPx(), 12.dpToPx())
                    }
                }

                val orderIdView = TextView(this).apply {
                    text = "Order ID: ${item.getString("id_sender")}"
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(resources.getColor(R.color.white))
                }


                val customerNameView = TextView(this).apply {
                    text = "Name: ${messageJson.getString("firstname")} ${messageJson.getString("lastname")}"
                    textSize = 16f
                    setTextColor(resources.getColor(R.color.white))
                }

                val burgerNameView = TextView(this).apply {
                    text = "Burger: ${messageJson.getString("burger")}"
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                    setTextColor(resources.getColor(R.color.custom_cyan))
                }

                orderView.apply {
                    addView(orderIdView)
                    addView(customerNameView)
                    addView(burgerNameView)
                }

                ordersContainer.addView(orderView)
            }
        } catch (e: JSONException) {
            Log.e("JSON Parsing", "Error parsing orders", e)
            Toast.makeText(this, "Erreur d'analyse des commandes", Toast.LENGTH_SHORT).show()
        }
    }
}