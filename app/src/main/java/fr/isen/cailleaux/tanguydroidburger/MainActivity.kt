package fr.isen.cailleaux.tanguydroidburger

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var spinnerBurgers: Spinner
    private lateinit var timePickerDelivery: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuration du ScrollView et du LinearLayout
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Initialisation des composants de l'interface utilisateur
        editTextFirstName = EditText(this).apply { hint = "Prénom" }
        editTextLastName = EditText(this).apply { hint = "Nom" }
        editTextAddress = EditText(this).apply { hint = "Adresse" }
        editTextPhone = EditText(this).apply { hint = "Téléphone"; inputType = android.text.InputType.TYPE_CLASS_PHONE }
        spinnerBurgers = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                arrayOf("Burger du chef", "Cheese Burger", "Burger Montagnard", "Burger Italien", "Burger Végétarien"))
        }
        timePickerDelivery = TimePicker(this).apply { setIs24HourView(true) }

        // Bouton de soumission de commande
        val buttonSubmit = Button(this).apply {
            text = "Passer la commande"
            setOnClickListener { submitOrder() }
        }

        // Ajout des éléments à la vue
        layout.apply {
            addView(editTextFirstName)
            addView(editTextLastName)
            addView(editTextAddress)
            addView(editTextPhone)
            addView(spinnerBurgers)
            addView(timePickerDelivery)
            addView(buttonSubmit)
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }

    private fun submitOrder() {
        val firstName = editTextFirstName.text.toString().trim()
        val lastName = editTextLastName.text.toString().trim()
        val address = editTextAddress.text.toString().trim()
        val phone = editTextPhone.text.toString().trim()
        val burger = spinnerBurgers.selectedItem.toString()
        val deliveryTime = "${timePickerDelivery.hour}:${timePickerDelivery.minute}"

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Tous les champs doivent être remplis.", Toast.LENGTH_LONG).show()
            return
        }

        val json = JSONObject().apply {
            put("id_shop", "1")
            put("id_user", 355)
            put("msg", JSONObject().apply {
                put("firstname", firstName)
                put("lastname", lastName)
                put("address", address)
                put("phone", phone)
                put("burger", burger)
                put("delivery_time", deliveryTime)
            }.toString())
        }

        sendOrder(json)
    }

    private fun sendOrder(json: JSONObject) {
        NetworkTask().execute(json.toString())
    }

    inner class NetworkTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String? {
            val client = OkHttpClient()
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = params[0].toRequestBody(mediaType)

            val request = Request.Builder()
                .url("http://test.api.catering.bluecodegames.com/user/order")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                return response.body?.string()
            }
        }

        override fun onPostExecute(result: String?) {
            result?.let {
                Intent(this@MainActivity, OrderConfirmationActivity::class.java).apply {
                    putExtra("orderDetails", it)
                    startActivity(this)
                }
            } ?: Toast.makeText(applicationContext, "Réponse nulle du serveur", Toast.LENGTH_LONG).show()
        }
    }
}