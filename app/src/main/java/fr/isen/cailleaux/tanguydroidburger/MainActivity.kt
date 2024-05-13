package fr.isen.cailleaux.tanguydroidburger

import android.content.Intent
import android.content.res.ColorStateList
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
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var spinnerBurgers: Spinner
    private lateinit var timePickerDelivery: TimePicker
    private lateinit var ordersContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainLayout = LinearLayout(this).apply {
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

        val commandtext = TextView(this).apply {
            text = "Commandez votre burger !"
            textSize = 20f
            setTextColor(resources.getColor(R.color.white))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16.dpToPx()
                bottomMargin = 16.dpToPx()
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

        editTextFirstName = EditText(this).apply {
            hint = "Prénom"
            setSingleLine()
            setPadding(16.dpToPx(), 12.dpToPx(), 16.dpToPx(), 12.dpToPx())
            setHintTextColor(resources.getColor(R.color.white))
            setTextColor(resources.getColor(R.color.white))
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }

        editTextLastName = EditText(this).apply {
            hint = "Nom"
            setSingleLine()
            setPadding(16.dpToPx(), 12.dpToPx(), 16.dpToPx(), 12.dpToPx())
            setHintTextColor(resources.getColor(R.color.white))
            setTextColor(resources.getColor(R.color.white))
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }

        editTextAddress = EditText(this).apply {
            hint = "Adresse"
            setSingleLine()
            setPadding(16.dpToPx(), 12.dpToPx(), 16.dpToPx(), 12.dpToPx())
            setHintTextColor(resources.getColor(R.color.white))
            setTextColor(resources.getColor(R.color.white))
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }

        editTextPhone = EditText(this).apply {
            hint = "Téléphone"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setSingleLine()
            setPadding(16.dpToPx(), 12.dpToPx(), 16.dpToPx(), 12.dpToPx())
            setHintTextColor(resources.getColor(R.color.white))
            setTextColor(resources.getColor(R.color.white))
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
        }

        spinnerBurgers = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity, android.R.layout.simple_spinner_dropdown_item,
                arrayOf("Burger du chef", "Cheese Burger", "Burger Montagnard", "Burger Italien", "Burger Végétarien")
            )
            background = resources.getDrawable(androidx.appcompat.R.color.material_grey_600)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = 16.dpToPx()
            }
        }

        timePickerDelivery = TimePicker(this).apply {
            setIs24HourView(true)
        }

        val buttonSubmit = Button(this).apply {
            text = "Passer la commande"
            setOnClickListener { submitOrder() }
            setBackgroundColor(resources.getColor(androidx.appcompat.R.color.material_blue_grey_800))
            setTextColor(Color.WHITE)
        }

        ordersContainer.apply {
            addView(logo)
            addView(restaurantName)
            addView(commandtext)
            addView(editTextFirstName)
            addView(editTextLastName)
            addView(editTextAddress)
            addView(editTextPhone)
            addView(spinnerBurgers)
            addView(timePickerDelivery)
            addView(buttonSubmit)
        }

        setContentView(mainLayout)
        scrollView.addView(ordersContainer)
        mainLayout.addView(scrollView)
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
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}
