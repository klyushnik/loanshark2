package studio.sanguine.loanshark2

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import studio.sanguine.loanshark2.data.Contact
import studio.sanguine.loanshark2.data.DebtRecordDb
import studio.sanguine.loanshark2.data.DebtRecordFull
import studio.sanguine.loanshark2.databinding.ActivityAddModifyDebtBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddModifyDebtActivity : AppCompatActivity() {

    lateinit var b : ActivityAddModifyDebtBinding
    var editMode = false
    val okColor = "#e0e0e0"
    val errorColor = "#FF9E80"
    lateinit var vm : AddModifyDebtViewModel
    lateinit var contacts : ArrayList<Contact>
    lateinit var adapter : ArrayAdapter<Contact>
    var hasSelectedPerson = false
    var dueDate = "Indefinite"
    var validationMessage = ""
    var isCreditor = false


    //to be generated from DebtRecordFull
    var editedRecord: DebtRecordFull? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddModifyDebtBinding.inflate(layoutInflater)
        setContentView(b.root)

        isCreditor = intent.getBooleanExtra("isCreditor", false)

        if(intent.getBooleanExtra("editMode", false)){
            editMode = true
            editedRecord = intent.getSerializableExtra("debtRecord") as DebtRecordFull?
            loadRecord(editedRecord)
        }



        vm = AddModifyDebtViewModel(this)
        contacts = ArrayList()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contacts)

        b.addrecPersonSpinner.adapter = adapter

        vm.contacts?.observe(this){
            contacts -> getContacts(contacts)
        }

        b.addrecDebtAmountEditText.addTextChangedListener {
            b.debtAmtLayout.boxBackgroundColor = Color.parseColor(okColor)
            b.addrecAddRecordButton.isEnabled = true
        }

        b.addrecInterestRateEdittext.addTextChangedListener {
            b.addrecAddRecordButton.isEnabled = true
            b.interestRateLayout.boxBackgroundColor = Color.parseColor(okColor)
        }

    }

    private fun getContacts(data: List<Contact>) {
        contacts.clear()
        contacts.addAll(data)

        if(contacts.isEmpty()){
            b.additemEmptyListTextview.visibility = View.VISIBLE
            b.addrecPersonSpinner.visibility = View.GONE
        } else {
            b.additemEmptyListTextview.visibility = View.GONE
            b.addrecPersonSpinner.visibility = View.VISIBLE
            adapter.notifyDataSetChanged()

            //if this is edit mode and we haven't selected a person yet, select the person
            if(editMode && !hasSelectedPerson){
                var pos = 0
                for(item in contacts){
                    if(item.contactId == editedRecord?.contactId) {
                        pos = contacts.indexOf(item)
                        break
                    }
                }
                b.addrecPersonSpinner.setSelection(pos)
                hasSelectedPerson = true
            }
        }
    }

    fun resetFieldsAppearance(){
        b.debtAmtLayout.boxBackgroundColor = Color.parseColor(okColor)
        b.debtDescLayout.boxBackgroundColor = Color.parseColor(okColor)
        b.interestRateLayout.boxBackgroundColor = Color.parseColor(okColor)
    }

    fun loadRecord(debtRecord: DebtRecordFull?){
        if(debtRecord != null){
            b.addrecTitle.setText("Edit Record")
            b.addrecAddRecordButton.setText("Edit Record")
            dueDate = debtRecord.debtDueDate
            b.addrecDueDateTextview.setText(dueDate)
            b.addrecDebtAmountEditText.setText(debtRecord.debtAmount.toString())
            b.addrecDebtDescEdittext.setText(debtRecord.debtDescription)
            b.addrecInterestRateEdittext.setText(debtRecord.interestRate.toString())
            b.interestTypeSpinner.setSelection(
            when (debtRecord.interestType){
                "n" -> 0
                "y" -> 1
                "m" -> 2
                "bw" -> 3
                "w" -> 4
                "d" -> 5
                else -> 0
            })
            isCreditor = debtRecord.debtIsCreditor
        }
    }

    fun pickDate(view: View){
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "date")
    }

    fun setDate(s : String){
        dueDate = s
        b.addrecDueDateTextview.setText(dueDate)
    }

    fun processInfo(view: View){
        if(validate()){
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd").format(Date())

            var debtAmount = 0.0
            var interestPercentage = 0.0
            if(!b.addrecDebtAmountEditText.text.toString().isEmpty())
                debtAmount = b.addrecDebtAmountEditText.text.toString().toDouble()

            if(!b.addrecInterestRateEdittext.text.toString().isEmpty())
                interestPercentage = b.addrecInterestRateEdittext.text.toString().toDouble()

            val record = DebtRecordDb(
                if(editMode) editedRecord!!.debtId else null,
                (b.addrecPersonSpinner.selectedItem as Contact).contactId!!,
                debtAmount,
                isCreditor,
                b.addrecDebtDescEdittext.text.toString(),
                if(!editMode) dateFormatter else editedRecord!!.debtInitialDate,
                dueDate,
                interestPercentage,
                when(b.interestTypeSpinner.selectedItemPosition){
                    0 -> "n"
                    1 -> "y"
                    2 -> "m"
                    3 -> "bw"
                    4 -> "w"
                    5 -> "d"
                    else -> "n"
                }
            )
            if(editMode){
                vm.updateRecord(record)
            } else {
                vm.insertRecord(record)
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

    fun validate() : Boolean {
        if(adapter.isEmpty){
            validationMessage += "No contacts are defined or selected\n"
            b.addrecAddRecordButton.isEnabled = false
            return false
        }

        if(b.addrecDebtAmountEditText.text.isNullOrBlank()){
            validationMessage += "Debt amount cannot be empty\n"
            b.debtAmtLayout.boxBackgroundColor = Color.parseColor(errorColor)
            b.addrecAddRecordButton.isEnabled = false
            return false
        }

        if(b.interestTypeSpinner.selectedItemPosition != 0 && (
                    b.addrecInterestRateEdittext.text.isNullOrBlank() ||
                            b.addrecInterestRateEdittext.text.toString().equals("0.0")
                )){
            validationMessage += "Interest rate cannot be blank or zero if interest type is other than 'none'\n"
            b.interestRateLayout.boxBackgroundColor = Color.parseColor(errorColor)
            b.addrecAddRecordButton.isEnabled = false
            return false
        }
        return true
    }

    fun exit(view: View){
        finish()
    }

    fun addPerson(view: View){
        val intent = Intent(this, AddModifyPerson::class.java)
        startActivity(intent)
    }
}

class DatePickerFragment() : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        val date = "$year-${String.format("%02d",(month + 1))}-${String.format("%02d",day)}"
        (context as AddModifyDebtActivity).setDate(date)
    }
}