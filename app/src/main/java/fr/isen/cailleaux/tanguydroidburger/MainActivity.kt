package fr.isen.cailleaux.tanguydroidburger

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ScrollView qui permettra le défilement
        val scrollView = ScrollView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Création du LinearLayout qui sera le conteneur principal à l'intérieur du ScrollView
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Ajout d'un ImageView pour le logo
        val imageView = ImageView(this).apply {
            setImageResource(R.drawable.burger)
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
            layoutParams = LinearLayout.LayoutParams(
                500,  // Largeur en pixels
                500   // Hauteur en pixels
            ).apply {
                gravity = Gravity.CENTER
            }
        }




        // Création des champs de texte et autres composants
        val editTextFirstName = EditText(this).apply {
            hint = "Prénom"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val editTextLastName = EditText(this).apply {
            hint = "Nom"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val editTextAddress = EditText(this).apply {
            hint = "Adresse"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val editTextPhone = EditText(this).apply {
            hint = "Téléphone"
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Spinner pour les burgers
        val spinnerBurgers = Spinner(this).apply {
            val burgers = arrayOf("Burger du chef", "Cheese Burger", "Burger Montagnard", "Burger Italien", "Burger Végétarien")
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, burgers)
        }

        // TimePicker pour l'heure de livraison
        val timePickerDelivery = TimePicker(this)

        // Bouton de soumission
        val buttonSubmit = Button(this).apply {
            text = "Passer la commande"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setOnClickListener {
                val firstName = editTextFirstName.text.toString()
                val lastName = editTextLastName.text.toString()
                val address = editTextAddress.text.toString()
                val phone = editTextPhone.text.toString()
                val burger = spinnerBurgers.selectedItem.toString()
                val hour = timePickerDelivery.hour
                val minute = timePickerDelivery.minute

                Toast.makeText(this@MainActivity, "Commande enregistrée: $firstName $lastName, $address, $phone, $burger à $hour:$minute", Toast.LENGTH_LONG).show()
            }
        }

        // Ajout des vues au layout principal
        layout.apply {
            addView(imageView)
            addView(editTextFirstName)
            addView(editTextLastName)
            addView(editTextAddress)
            addView(editTextPhone)
            addView(spinnerBurgers)
            addView(timePickerDelivery)
            addView(buttonSubmit)
        }

        // Ajout du LinearLayout au ScrollView
        scrollView.addView(layout)

        // Définir le ScrollView comme contenu de la vue de l'activité
        setContentView(scrollView)
    }
}
