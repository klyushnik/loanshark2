package studio.sanguine.loanshark2.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.combine
import studio.sanguine.loanshark2.R
import studio.sanguine.loanshark2.adapters.HistoryRecordAdapter
import studio.sanguine.loanshark2.data.History
import studio.sanguine.loanshark2.databinding.FragmentTransactionsBinding
import studio.sanguine.loanshark2.ui.history.HistoryViewModel

class TransactionsFragment(val contactId: Int) : DialogFragment() {

    companion object {
        fun newInstance(contactId: Int) = TransactionsFragment(contactId)
    }

    lateinit var historyList : ArrayList<History>
    private lateinit var viewModel: TransactionsViewModel
    lateinit var adapter : HistoryRecordAdapter
    lateinit var binding: FragmentTransactionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTransactionsBinding.inflate(inflater, container, false)

        val vm = TransactionsViewModel(requireContext(), contactId)
        historyList = ArrayList()

        adapter = HistoryRecordAdapter(historyList)

        binding.transactionsRecyclerView.adapter = adapter
        binding.transactionsRecyclerView.layoutManager = LinearLayoutManager(context)

        vm.data?.observe(viewLifecycleOwner){
                contactList -> getStudents(contactList)
        }

        binding.transactionsCloseButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    fun getStudents(list: List<History>){
        historyList.clear()
        historyList.addAll(list)
        adapter.notifyDataSetChanged()
        if(adapter.itemCount == 0){
            binding.noItems.visibility = View.VISIBLE
        }
    }

}