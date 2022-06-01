package studio.sanguine.loanshark2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import studio.sanguine.loanshark2.R
import studio.sanguine.loanshark2.Util
import studio.sanguine.loanshark2.data.DebtRecordFull
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DebtRecordAdapter(
    var data: List<DebtRecordFull>,
    val editPerson: (DebtRecordFull) -> Unit,
    val makeCall: (String) -> Unit,
    val makeSms: (String) -> Unit,
    val recordPayment: (item: DebtRecordFull, debtAmount : Double) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.debt_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Set up the elements here, for now it's only the layouts
        val numberFormat = NumberFormat.getCurrencyInstance()
        val item = data.get(position)
        val name = "${item.firstName} ${item.lastName}"
        val phoneNumbers = item.phoneNumbers.split(",")
        if(phoneNumbers.isEmpty()){
            holder.smsButton.isEnabled = false
            holder.callButton.isEnabled = false
        } else {
            holder.smsButton.isEnabled = true
            holder.callButton.isEnabled = true
        }
        var interestRate = item.interestRate.toString() + "% "
        interestRate +=
                when (item.interestType){
                    "n" -> "N/A"
                    "y" -> "Annual"
                    "m" -> "Monthly"
                    "bw" -> "Bi-weekly"
                    "w" -> "Weekly"
                    "d" -> "Daily"
                    else -> "N/A"

                }


        holder.initialsTextView.setText(item.firstName.get(0).toString())
        if(!item.lastName.isEmpty())
            holder.initialsTextView.setText("${item.firstName.get(0)}${item.lastName.get(0)}")

        holder.nameText.setText(name)
        holder.shortDebtAmtText.setText(numberFormat.format(item.debtAmount))
        holder.shortInterestText.setText(interestRate)
        holder.shortDateText.setText(item.debtDueDate)

        holder.debtReasonText.setText(item.debtDescription)
        holder.initialAmtText.setText(numberFormat.format(item.debtAmount))
        holder.interestRateText.setText(interestRate)
        holder.dueDateText.setText(item.debtDueDate)
        holder.totalDebtText.setText(numberFormat.format(Util.withInterest(item.debtAmount, item.interestRate, item.interestType, item.debtInitialDate)))
        holder.totalPaidText.setText(numberFormat.format(item.paidAmount))
        holder.balanceLeftText.setText(numberFormat.format(item.debtAmount - item.paidAmount))

        var overdue = false
        if(item.debtDueDate != "Indefinite"){
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val date = Date()
            var currentDate = dateFormat.format(date)

            overdue = (Util.compareDates(currentDate, item.debtDueDate) < 0)
        }
        holder.overdue = overdue

        if(overdue && !holder.isOpen){
            holder.statusLabel.visibility = View.VISIBLE
        } else {
            holder.statusLabel.visibility = View.GONE
        }

        holder.editButton.setOnClickListener{ editPerson(item) }
        holder.callButton.setOnClickListener { makeCall(
            if(phoneNumbers.isEmpty()) "" else phoneNumbers[0]
        ) }
        holder.smsButton.setOnClickListener { makeSms(
            if(phoneNumbers.isEmpty()) "" else phoneNumbers[0]
        ) }
        holder.paymentButton.setOnClickListener { recordPayment(item, item.debtAmount - item.paidAmount) }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(position: Int): DebtRecordFull{
        return data.get(position)
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{
    init{
        itemView.setOnClickListener(this)
    }
    var overdue = false

    val root : CardView = view.findViewById(R.id.item_root_container)
    val shortDescLayout: ConstraintLayout = view.findViewById(R.id.item_short_description_layout)
    val statusLabel: TextView = view.findViewById(R.id.item_status_textview)

    val longLayout : ConstraintLayout = view.findViewById(R.id.item_long_desc_layout)
    val editButton : ImageButton = view.findViewById(R.id.item_edit_button)
    val smsButton: ImageButton = view.findViewById(R.id.item_sms_button)
    val callButton: ImageButton = view.findViewById(R.id.item_call_button)
    val paymentButton: Button = view.findViewById(R.id.item_record_payment_button)

    val nameText : TextView = view.findViewById(R.id.item_name_textview)
    val shortDebtAmtText : TextView = view.findViewById(R.id.item_short_debt_amt_textview)
    val shortInterestText : TextView = view.findViewById(R.id.item_percentage_short_textview)
    val shortDateText : TextView = view.findViewById(R.id.item_short_date_textview)

    val debtReasonText : TextView = view.findViewById(R.id.item_debt_reason_textview)
    val initialAmtText : TextView = view.findViewById(R.id.item_initial_amount_textview)
    val interestRateText : TextView = view.findViewById(R.id.item_interest_rate_textview)
    val dueDateText : TextView = view.findViewById(R.id.item_due_date_textview)
    val totalDebtText : TextView = view.findViewById(R.id.item_total_debt_textview)
    val totalPaidText : TextView = view.findViewById(R.id.item_total_paid_textview)
    val balanceLeftText : TextView = view.findViewById(R.id.item_balance_left_textview)

    val initialsTextView: TextView = view.findViewById(R.id.item_initials_textview)

    var isOpen = false

    override fun onClick(p0: View?) {
        if(shortDescLayout.visibility == View.VISIBLE){
            TransitionManager.beginDelayedTransition(root, AutoTransition().setDuration(250))
            shortDescLayout.visibility = View.GONE
            statusLabel.visibility = View.GONE

            longLayout.visibility = View.VISIBLE
            editButton.visibility = View.VISIBLE
            debtReasonText.visibility = View.VISIBLE
            isOpen = true

        } else {
            TransitionManager.beginDelayedTransition(root, AutoTransition().setDuration(75))
            longLayout.visibility = View.GONE
            editButton.visibility = View.GONE
            debtReasonText.visibility = View.GONE

            if(overdue){
                statusLabel.visibility = View.VISIBLE
            } else {
                statusLabel.visibility = View.GONE
            }
            shortDescLayout.visibility = View.VISIBLE
            isOpen = false

        }
    }

}