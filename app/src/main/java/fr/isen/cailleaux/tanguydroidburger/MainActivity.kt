package fr.isen.cailleaux.tanguydroidburger

import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var spinnerBurgers: Spinner
    private lateinit var timePickerDelivery: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ScrollView et LinearLayout setup
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

        // Setup des composants
        editTextFirstName = EditText(this).apply { hint = "Prénom" }
        editTextLastName = EditText(this).apply { hint = "Nom" }
        editTextAddress = EditText(this).apply { hint = "Adresse" }
        editTextPhone = EditText(this).apply { hint = "Téléphone"; inputType = android.text.InputType.TYPE_CLASS_PHONE }

        spinnerBurgers = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                arrayOf("Burger du chef", "Cheese Burger", "Burger Montagnard", "Burger Italien", "Burger Végétarien"))
        }

        timePickerDelivery = TimePicker(this).apply { setIs24HourView(true) }

        val buttonSubmit = Button(this).apply {
            text = "Passer la commande"
            setOnClickListener { submitOrder() }
        }

        // Ajout des views au layout
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
        val firstName = editTextFirstName.text.toString()
        val lastName = editTextLastName.text.toString()
        val address = editTextAddress.text.toString()
        val phone = editTextPhone.text.toString()
        val burger = spinnerBurgers.selectedItem.toString()
        val deliveryTime = "${timePickerDelivery.hour}:${timePickerDelivery.minute}"

        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Tous les champs doivent être remplis.", Toast.LENGTH_LONG).show()
            return
        }

        val json = JSONObject().apply {
            put("id_shop", "1")
            put("id_user", "355")
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
        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toString().toRequestBody(mediaType)

        // Affichage du JSON envoyé pour vérification
        Log.d("OrderDebug", "JSON being sent: ${json.toString()}")

        val request = Request.Builder()
            .url("http://test.api.catering.bluecodegames.com/user/order")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Gestion d'une erreur de réseau ou de requête
                runOnUiThread {
                    Toast.makeText(applicationContext, "Échec de la connexion: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        // La réponse est un succès
                        val intent = Intent(this@MainActivity, OrderConfirmationActivity::class.java)
                        intent.putExtra("orderDetails", response.body?.string())
                        startActivity(intent)
                    } else {
                        // La réponse indique une erreur
                        Toast.makeText(applicationContext, "Erreur lors de la commande: ${response.body?.string()}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}
