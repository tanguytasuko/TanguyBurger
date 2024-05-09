package fr.isen.cailleaux.tanguydroidburger

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Création du LinearLayout qui sera le conteneur principal
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            var padding = 16
        }

        // Ajout d'un ImageView pour le logo
        val imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

        // Création des champs de texte
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
        val spinnerBurgers = Spinner(this)
        val burgers = arrayOf("Burger du chef", "Cheese Burger", "Burger Montagnard", "Burger Italien", "Burger Végétarien")
        spinnerBurgers.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, burgers)

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
        layout.addView(imageView)
        layout.addView(editTextFirstName)
        layout.addView(editTextLastName)
        layout.addView(editTextAddress)
        layout.addView(editTextPhone)
        layout.addView(spinnerBurgers)
        layout.addView(timePickerDelivery)
        layout.addView(buttonSubmit)

        // Définir le layout comme contenu de la vue de l'activité
        setContentView(layout)
    }
}