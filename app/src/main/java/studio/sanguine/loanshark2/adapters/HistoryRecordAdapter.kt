package studio.sanguine.loanshark2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import studio.sanguine.loanshark2.R
import studio.sanguine.loanshark2.data.History
import java.text.NumberFormat

class HistoryRecordAdapter(var data: List<History>) : RecyclerView.Adapter<HistoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val numberFormat = NumberFormat.getCurrencyInstance()
        val item = data.get(position)
        holder.name.setText(item.name)
        holder.paymentAmt.setText(numberFormat.format(item.paymentAmount))
        holder.date.setText(item.paymentDate)
        holder.desc.setText(item.desc)
        holder.final.visibility = if(item.isFinal) View.VISIBLE else View.GONE
        holder.initial.setText(item.name.get(0).toString())
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun getItem(position: Int) : History{
        return data.get(position)
    }
}

class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val name = view.findViewById<TextView>(R.id.item_name_textview)
    val paymentAmt = view.findViewById<TextView>(R.id.item_short_debt_amt_textview)
    val date = view.findViewById<TextView>(R.id.item_short_date_textview)
    val desc = view.findViewById<TextView>(R.id.hist_desc)
    val final = view.findViewById<TextView>(R.id.item_status_textview)
    val initial = view.findViewById<TextView>(R.id.item_initials_textview)
}