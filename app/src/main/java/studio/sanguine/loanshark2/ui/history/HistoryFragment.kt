package studio.sanguine.loanshark2.ui.history

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import studio.sanguine.loanshark2.FragmentOperations
import studio.sanguine.loanshark2.R
import studio.sanguine.loanshark2.adapters.ContactRecordAdapter
import studio.sanguine.loanshark2.adapters.HistoryRecordAdapter
import studio.sanguine.loanshark2.data.ContactRecordFull
import studio.sanguine.loanshark2.data.History
import studio.sanguine.loanshark2.databinding.FragmentDashboardBinding
import studio.sanguine.loanshark2.ui.contacts.ContactSwipeHelper
import studio.sanguine.loanshark2.ui.contacts.ContactsViewModel

class HistoryFragment : Fragment(), FragmentOperations {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var vm: HistoryViewModel
    lateinit var adapter: HistoryRecordAdapter
    var searchQuery = "%%"
    var offset = 0
    var pageSize = 10
    var isLoading = false
    var sortDesc = false

    lateinit var historyList: ArrayList<History>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        vm = HistoryViewModel(requireContext())

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        historyList = ArrayList()

        vm.data?.observe(viewLifecycleOwner){
                contactList -> getStudents(contactList)
        }

        val recyclerView = binding.historyRecyclerView
        adapter = HistoryRecordAdapter(historyList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.itemAnimator = DefaultItemAnimator()
        val swipeHelper = HistorySwipeHelper(vm, adapter, requireContext())
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        binding.hisNextButton.setOnClickListener { nextPage() }
        binding.hisPreviousButton.setOnClickListener { prevPage() }
        binding.historyClearSearchButton.setOnClickListener { hideSearchBar() }

        binding.historySearchEdittext.addTextChangedListener {
            searchQuery = "%${binding.historySearchEdittext.text.toString()}%"

            search()
        }

        return root
    }

    fun getStudents(list: List<History>){
        historyList.clear()
        historyList.addAll(list)
        if(adapter.itemCount > 0){
            binding.noItems.visibility = View.GONE
        } else {
            binding.noItems.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.removeItem(R.id.menu_newRecord)
        menu.removeItem(R.id.menu_newPerson)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun hideSearchBar() {
        binding.historySearchLayout.visibility = View.GONE
        binding.historySearchEdittext.setText("")
        adapter.notifyDataSetChanged()
    }

    override fun search() {
        isLoading = true
        vm.searchHistory(searchQuery, sortDesc, offset, pageSize)?.observe(
            viewLifecycleOwner
        ) { list ->
            list.let {
                adapter.data = it
                adapter.notifyDataSetChanged()
                if(adapter.itemCount > 0){
                    binding.noItems.visibility = View.GONE
                } else {
                    binding.noItems.visibility = View.VISIBLE
                }
                isLoading = false
            }
        }
    }

    override fun sortAsc() {
        sortDesc = false
        search()
    }

    override fun sortDesc() {
        sortDesc = true
        search()
    }

    override fun nextPage() {
        if(adapter.itemCount == 10 && !isLoading)
            offset += 10
        search()
    }

    override fun prevPage() {
        offset -= 10
        if(offset < 0) offset = 0
        search()
    }

    override fun searchBar() {
        if(binding.historySearchLayout.visibility == View.VISIBLE){
            binding.historySearchLayout.visibility = View.GONE
            binding.historySearchEdittext.setText("")
        }else{
            binding.historySearchLayout.visibility = View.VISIBLE
        }
    }

    override fun newItem() {

    }
}
open class HistorySwipeHelper(val vm: HistoryViewModel,
                              val adapter: HistoryRecordAdapter,
                              val context: Context
) : ItemTouchHelper.Callback(){
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        return makeMovementFlags(0, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val item = adapter.getItem(viewHolder.adapterPosition)

        val dialogBuilder = AlertDialog.Builder(context)
        val message = "Are you sure you want to delete this record?"
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                vm.deleteHistory(item)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                adapter.notifyDataSetChanged()
            })
        val dialog = dialogBuilder.create()
        dialog.setTitle("Delete record")
        dialog.show()
    }
}