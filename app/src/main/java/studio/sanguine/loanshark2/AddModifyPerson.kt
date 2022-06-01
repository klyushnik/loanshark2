package studio.sanguine.loanshark2

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import studio.sanguine.loanshark2.data.Contact
import studio.sanguine.loanshark2.data.Repo
import studio.sanguine.loanshark2.databinding.ActivityAddModifyPersonBinding

class AddModifyPerson : AppCompatActivity() {

    lateinit var binding :ActivityAddModifyPersonBinding
    lateinit var repo: Repo

    var editMode = false
    var contactId = -1
    var validationMessage = ""
    val okColor = "#e0e0e0"
    val errorColor = "#FF9E80"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityAddModifyPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = Repo(this)

        editMode = intent.getBooleanExtra("editMode", false)

        if(editMode){
            val contact: Contact = intent.getSerializableExtra("contact") as Contact
            loadPerson(contact)
        }

        binding.addcontactFirstNameText.addTextChangedListener {
            binding.firstNameLayout.boxBackgroundColor = Color.parseColor(okColor)
            binding.addcontactAddContactButton.isEnabled = true
        }
    }

    fun processPerson(view: View){
        if(validate()){
            val fName = binding.addcontactFirstNameText.text.toString()
            val lName = binding.addcontactLastNameText.text.toString()
            var phoneNumber = ""
            if(!binding.addcontactPrimaryPhoneNumberText.text.toString().isEmpty())
                phoneNumber += binding.addcontactPrimaryPhoneNumberText.text.toString()
            if(!binding.addcontactSecondaryPhoneNumberText.text.toString().isEmpty())
                phoneNumber += ",${binding.addcontactSecondaryPhoneNumberText.text.toString()}"
            if(!binding.addcontactAltPhoneNumberText.text.toString().isEmpty())
                phoneNumber += ",${binding.addcontactAltPhoneNumberText.text.toString()}"

            val contact = Contact(if (editMode) contactId else null, fName, lName, phoneNumber)
            if(editMode){
                repo.updateContact(contact)
            } else {
                repo.insertContact(contact)
            }

            finish()
        } else {
            val dialog = AlertDialog.Builder(this)
                .setMessage("Please fix the following errors:\n\n" + validationMessage)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                    validationMessage = ""
                })
                .create()
            dialog.show()
        }
    }

    fun loadPerson(contact: Contact){
        contactId = contact!!.contactId!!
        binding.addcontactTitle.setText("Edit Contact")
        binding.addcontactAddContactButton.setText("Edit Contact")
        binding.addcontactFirstNameText.setText(contact.firstName)
        binding.addcontactLastNameText.setText(contact.lastName)
        val phoneNumbers = contact.phoneNumbers.split(",")
        if(!phoneNumbers[0].isEmpty())
            binding.addcontactPrimaryPhoneNumberText.setText(phoneNumbers[0])
        if(phoneNumbers.size >= 2 && !phoneNumbers[1].isEmpty())
            binding.addcontactSecondaryPhoneNumberText.setText(phoneNumbers[1])
        if(phoneNumbers.size >= 3 && !phoneNumbers[2].isEmpty())
            binding.addcontactAltPhoneNumberText.setText(phoneNumbers[2])
    }

    fun cancel(view: View){
        finish()
    }

    fun pickFromPhonebook(view: View){

    }

    fun validate() : Boolean {
        if(binding.addcontactFirstNameText.text.isNullOrBlank()){
            validationMessage += "Debt amount cannot be empty\n"
            binding.firstNameLayout.boxBackgroundColor = Color.parseColor(errorColor)
            binding.addcontactAddContactButton.isEnabled = false
            return false
        }
        return true
    }

}