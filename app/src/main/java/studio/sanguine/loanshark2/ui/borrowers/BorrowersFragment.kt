package studio.sanguine.loanshark2.ui.borrowers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import studio.sanguine.loanshark2.AddModifyDebtActivity
import studio.sanguine.loanshark2.AddModifyPerson
import studio.sanguine.loanshark2.FragmentOperations
import studio.sanguine.loanshark2.R
import studio.sanguine.loanshark2.adapters.DebtRecordAdapter
import studio.sanguine.loanshark2.data.DebtRecordDb
import studio.sanguine.loanshark2.data.DebtRecordFull
import studio.sanguine.loanshark2.data.History
import studio.sanguine.loanshark2.databinding.FragmentBorrowersBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BorrowersFragment : Fragment(), FragmentOperations {

    private var _binding: FragmentBorrowersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var recordList: ArrayList<DebtRecordFull>
    var sortDesc = false
    lateinit var adapter: DebtRecordAdapter
    lateinit var vm: BorrowersViewModel
    var searchQuery = "%%"
    var offset = 0
    var pageSize = 10
    var isLoading = false

    var isCreditor = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        isCreditor = arguments?.getBoolean("isCreditor") == true

        vm = BorrowersViewModel(requireContext(), isCreditor)


        _binding = FragmentBorrowersBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recordList = ArrayList()


        val recyclerView: RecyclerView = binding.itemsContainerRecyclerview
        adapter = DebtRecordAdapter(recordList, editRecord, handleCall, handleSms, recordPayment)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        recyclerView.itemAnimator = DefaultItemAnimator()
        val swipeHelper = RecordSwipeHelper(vm, adapter, requireContext())
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(recyclerView)


        vm.data?.observe(viewLifecycleOwner){
            recordList -> getRecords(recordList)
        }

        binding.borrowersSearchEdittext.addTextChangedListener {
            searchQuery = "%${binding.borrowersSearchEdittext.text.toString()}%"

            search()
        }

        binding.recNextButton.setOnClickListener { nextPage() }
        binding.recPreviousButton.setOnClickListener { prevPage() }
        binding.borrowersClearSearchButton.setOnClickListener { hideSearchBar() }
        binding.newItemButton.setOnClickListener { newItem() }

        return root
    }

    override fun search(){
        isLoading = true
        vm.searchRecords(searchQuery, sortDesc, offset, pageSize, isCreditor)?.observe(
            viewLifecycleOwner
        ) { list ->
            list.let {
                adapter.data = it
                adapter.notifyDataSetChanged()
                if(adapter.itemCount > 0){
                    binding.noItems.visibility = View.GONE
                } else {
                    binding.noItems.visibility = View.VISIBLE
                    binding.borrowersProgressbar.visibility = View.GONE
                    binding.newItemButton.visibility = View.VISIBLE
                }
                isLoading = false
            }
        }


    }

    override fun searchBar() {
        if(binding.borrowersSearchLayout.visibility == View.VISIBLE){
            binding.borrowersSearchLayout.visibility = View.GONE
            binding.borrowersSearchEdittext.setText("")
        }else{
            binding.borrowersSearchLayout.visibility = View.VISIBLE
        }
    }

    fun hideSearchBar() {
        binding.borrowersSearchLayout.visibility = View.GONE
        binding.borrowersSearchEdittext.setText("")
        adapter.notifyDataSetChanged()
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

    fun getRecords(list: List<DebtRecordFull>){
        recordList.clear()
        recordList.addAll(list)
        adapter.notifyDataSetChanged()
        if(adapter.itemCount > 0){
            binding.noItems.visibility = View.GONE
        } else{
            binding.noItems.visibility = View.VISIBLE
            binding.borrowersProgressbar.visibility = View.GONE
            binding.newItemButton.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.removeItem(R.id.menu_newPerson)
    }

    val editRecord = fun(debtRecord: DebtRecordFull) {
        val intent = Intent(context, AddModifyDebtActivity::class.java)
        intent.putExtra("editMode", true)
        intent.putExtra("debtRecord", debtRecord)
        intent.putExtra("isCreditor", isCreditor)
        startActivity(intent)
    }

    override fun newItem() {
        val intent = Intent(requireContext(), AddModifyDebtActivity::class.java)
        intent.putExtra("isCreditor", isCreditor)
        startActivity(intent)
    }

    val handleCall = fun (param: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Do you want to call $param?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:" + param)
                startActivity(dialIntent)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            .setNeutralButton("SMS", DialogInterface.OnClickListener { dialogInterface, i ->
                val smsIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", param, null))
                smsIntent.putExtra("sms_body", "Remember me?")
                startActivity(smsIntent)
            })

        val dialog = dialogBuilder.create()
        dialog.setTitle("Call number")
        dialog.show()
    }

    val handleSms = fun (param: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Do you want to message $param?")
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                val smsIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", param, null))
                smsIntent.putExtra("sms_body", "Remember me?")
                startActivity(smsIntent)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->

            })

        val dialog = dialogBuilder.create()
        dialog.setTitle("Call number")
        dialog.show()

    }

    val recordPayment = fun(item: DebtRecordFull, debtAmount: Double){
        //show a dialog prompt, then create history record with debt id, finally notifydatasetchanged
        var text = EditText(requireContext())
        text.setPadding(50,50,50,50)
        text.setInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Payment amount:")
            .setView(text)
            .setPositiveButton("Pay part", DialogInterface.OnClickListener { dialogInterface, i ->
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd").format(Date())
                val string = text.text.toString()
                var paymentAmt = if(string.isEmpty()) 0.0 else string.toDouble()
                val history = History(
                    null,
                    item.debtId,
                    item.contactId,
                    item.firstName + " " + item.lastName,
                    item.debtDescription,
                    paymentAmt,
                    dateFormatter,
                    paymentAmt >= debtAmount
                )
                vm.addHistoryRecord(history)
                if(history.isFinal){
                    val debtRecordDb = DebtRecordDb(item.debtId, 0, 0.0,false,"","","",0.0,"n")
                    vm.deleteRecord(debtRecordDb)
                }

            })
            .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialogInterface, i ->

            })
            .setNeutralButton("Pay All", DialogInterface.OnClickListener { dialogInterface, i ->
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd").format(Date())
                val string = text.text.toString()
                var paymentAmt = if(string.isEmpty()) 0.0 else string.toDouble()
                val history = History(
                    null,
                    item.debtId,
                    item.contactId,
                    item.firstName + " " + item.lastName,
                    item.debtDescription,
                    debtAmount,
                    dateFormatter,
                    true
                )
                vm.addHistoryRecord(history)
                val debtRecordDb = DebtRecordDb(item.debtId, 0, 0.0,false,"","","",0.0,"n")
                vm.deleteRecord(debtRecordDb)


            }).create().show()

    }
}
open class RecordSwipeHelper(val vm: BorrowersViewModel,
                              val adapter: DebtRecordAdapter,
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
                vm.deleteRecord(item.toDebtRecord())
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                adapter.notifyDataSetChanged()
            })
        val dialog = dialogBuilder.create()
        dialog.setTitle("Delete record")
        dialog.show()
    }
}