package studio.sanguine.loanshark2.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import studio.sanguine.loanshark2.R

class PhoneNumberAdapter(val data: List<String>,
                         val context: Context,
                         val onPhoneNumberPressed:(number: String) -> Unit) : BaseAdapter() {
    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(p0: Int): Any {
        return data.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(index: Int, view: View?, viewGroup: ViewGroup?): View {
        val item = LayoutInflater.from(context).inflate(R.layout.phone_list_item, viewGroup, false)
        val phoneNumberText = item.findViewById<TextView>(R.id.phone_number_textview)
        phoneNumberText.setText(data.get(index))
        item.setOnClickListener {
            onPhoneNumberPressed(data.get(index))
        }
        return item
    }
}