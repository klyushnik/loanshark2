package studio.sanguine.loanshark2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import studio.sanguine.loanshark2.R
import studio.sanguine.loanshark2.data.Contact
import studio.sanguine.loanshark2.data.ContactRecordFull
import studio.sanguine.loanshark2.ui.NonScrollListView
import java.text.NumberFormat

class ContactRecordAdapter(
    var data: List<ContactRecordFull>,
    val placeCall: (String) -> Unit,
    val editRecord: (Contact) -> Unit,
    val showTransactions: (Int) -> Unit) : RecyclerView.Adapter<ContactViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contacts_item, parent, false)
        context = parent.context
        return ContactViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = data.get(position)
        val numbers = item.phoneNumbers.split(",")
        val name = "${item.firstName} ${item.lastName}"
        var initials = ""
        if(!item.firstName.isEmpty()){
            initials += item.firstName.get(0)
        }
        if(!item.lastName.isEmpty()){
            initials += item.lastName.get(0).toString()
        }
        val numberFormat = NumberFormat.getCurrencyInstance()
        holder.adapter = PhoneNumberAdapter(numbers, context, placeCall)
        holder.listView.adapter = holder.adapter
        holder.editButton.setOnClickListener { editRecord(item.toContact()) }

        holder.nameText.setText(name)
        holder.shortNumberText.setText(numbers[0])
        holder.borrowerText.setText(numberFormat.format(item.borrowerDebt))
        holder.creditorText.setText(numberFormat.format(item.creditorDebt))
        holder.initials.setText(initials)

        holder.transactionButton.setOnClickListener {
            showTransactions(item.contactId)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(position: Int) : Contact {
        return data.get(position).toContact()
    }

}


class ContactViewHolder(view: View, context: Context) : RecyclerView.ViewHolder(view), View.OnClickListener{
    val listView: ListView

    lateinit var adapter: PhoneNumberAdapter
    init{
        itemView.setOnClickListener(this)
        listView = view.findViewById(R.id.phone_numbers_listview)
    }

    val longLayout : ConstraintLayout = view.findViewById(R.id.contact_long_desc_layout)
    val editButton : ImageButton = view.findViewById(R.id.contact_edit_button)
    val root : CardView = view.findViewById(R.id.contact_root_container)
    val creditorText: TextView = view.findViewById(R.id.contact_creditor_textview)
    val borrowerText: TextView = view.findViewById(R.id.contact_borrower_textview)
    val nameText: TextView = view.findViewById(R.id.contact_name_textview)
    val shortNumberText: TextView = view.findViewById(R.id.contact_first_phone_number)
    val initials : TextView = view.findViewById(R.id.contact_initials_textview)
    val transactionButton : Button = view.findViewById(R.id.contact_transactions_button)

    override fun onClick(p0: View?) {
        if(longLayout.visibility == View.VISIBLE){
            TransitionManager.beginDelayedTransition(root, AutoTransition().setDuration(250))
            longLayout.visibility = View.GONE
            editButton.visibility = View.GONE

        } else {
            TransitionManager.beginDelayedTransition(root, AutoTransition().setDuration(75))
            longLayout.visibility = View.VISIBLE
            editButton.visibility = View.VISIBLE
        }
    }

}